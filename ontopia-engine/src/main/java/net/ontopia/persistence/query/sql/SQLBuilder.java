/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.persistence.query.sql;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.persistence.proxy.ClassInfoIF;
import net.ontopia.persistence.proxy.FieldInfoIF;
import net.ontopia.persistence.proxy.FieldUtils;
import net.ontopia.persistence.proxy.ObjectAccessIF;
import net.ontopia.persistence.proxy.ObjectRelationalMappingIF;
import net.ontopia.persistence.query.jdo.JDOAggregateIF;
import net.ontopia.persistence.query.jdo.JDOAnd;
import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOCollection;
import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEndsWith;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOExpressionIF;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOFunction;
import net.ontopia.persistence.query.jdo.JDOIsEmpty;
import net.ontopia.persistence.query.jdo.JDOLike;
import net.ontopia.persistence.query.jdo.JDONativeValue;
import net.ontopia.persistence.query.jdo.JDONot;
import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOOr;
import net.ontopia.persistence.query.jdo.JDOOrderBy;
import net.ontopia.persistence.query.jdo.JDOParameter;
import net.ontopia.persistence.query.jdo.JDOPrimitive;
import net.ontopia.persistence.query.jdo.JDOQuery;
import net.ontopia.persistence.query.jdo.JDOSetOperation;
import net.ontopia.persistence.query.jdo.JDOStartsWith;
import net.ontopia.persistence.query.jdo.JDOString;
import net.ontopia.persistence.query.jdo.JDOValueExpression;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.persistence.query.jdo.JDOVariable;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used to build SQL queries from JDO queries. 
 */

public class SQLBuilder {

  protected boolean debug;
  protected ObjectRelationalMappingIF mapping;

  public SQLBuilder(ObjectRelationalMappingIF mapping) {
    this(mapping, false);
 } 

  public SQLBuilder(ObjectRelationalMappingIF mapping, boolean debug) {
    this.mapping = mapping;
    this.debug = debug;
  }
 
  /**
   * INTERNAL: Class used to hold information collected after having
   * analyzed the SQL filter.
   */
  class BuildInfo {

    // Object access
    protected ObjectAccessIF oaccess;
    
    // The JDO query
    protected JDOQuery jdoquery;
    
    // The SQL query
    protected SQLQuery sqlquery;

    // Mapping between table aliases and table names
    protected Map<String, SQLTable> tables = new HashMap<String, SQLTable>(); // { alias : SQLTable }
    protected Map<JDOValueIF, String> valiases = new HashMap<JDOValueIF, String>(); // { JDOVariable|JDOParameter : alias }
    protected int tblcount;

    // Mapping between values of non-identifiable type and their field infos.
    protected Map<JDOValueIF, FieldInfoIF> nfvals = new HashMap<JDOValueIF, FieldInfoIF>(); // { JDOVariable|JDOParameter : FieldInfoIF }
    // Values of non-identifiable types and their corresponding tables
    protected Map<JDOValueIF, String> ntvals = new HashMap<JDOValueIF, String>(); // { JDOVariable|JDOParameter : String tblname }

    private SQLTable createNamedValueTable(JDOValueIF value, List expressions) {

      String valname;
      String prefix;
      switch (value.getType()) {      
      case JDOValueIF.VARIABLE: {
        valname = ((JDOVariable)value).getName();
        prefix = "V";
        break;
      }
      case JDOValueIF.PARAMETER: {
        valname = ((JDOParameter)value).getName();
        prefix = "P";
        break;
      }
      default:
        throw new OntopiaRuntimeException("Non-supported named value: '" + value + "'");
      }
        
      // Get or create value alias
      String alias = (debug ? valname : valiases.get(value));
      if (alias == null) {
        while (true) { 
          alias = prefix + (tblcount++);
          if (tables.containsKey(alias)) {
            continue;
          }
          valiases.put(value, alias);
          break;
        }
      }
      
      // Check to see if value table already has been registered
      if (tables.containsKey(alias)) {
        // Return existing table
        return tables.get(alias);
      } else {
        
        // Value is of non-identifiable type
        if (nfvals.containsKey(value)) {
          // Lookup field info
          FieldInfoIF finfo = nfvals.get(value);
          // Create table information

          // BUG: null pointer exception (tblname==null) thrown on
          // (O.locator.address = V). This is caused by the fact that
          // address is a primitive field and its parent is an
          // aggregate. Need to propagate the table all the way down.

          String tblname = finfo.getTable();
          if (tblname == null) {
            tblname = ntvals.get(value);
          }
          if (tblname == null) {
            throw new OntopiaRuntimeException("Not able to figure out table for value: '" + value + "'");
          }
          
          SQLTable table = new SQLTable(tblname, alias);
          tables.put(table.getAlias(), table);

          // ISSUE: do we need to add parameter expression in this
          // case? Probably not since it is not the parameter's own
          // table, but rather the adjacent.
          return table;
        }
        // Value is of identifiable type
        else {
          // Get value type information
          Class valtype = getValueType(value, this);
          ClassInfoIF cinfo = mapping.getClassInfo(valtype);          

          // Create table information
          SQLTable table = new SQLTable(cinfo.getMasterTable(), alias);
          tables.put(table.getAlias(), table);

          // Bind parameter value to parameter table
          if (value.getType() == JDOValueIF.PARAMETER) {

            // Get hold of parameter identity field info
            FieldInfoIF finfo = cinfo.getIdentityFieldInfo();

            // Create expression that ties the parameter value with the parameter table key
            SQLParameter sqlparam = new SQLParameter(valname, finfo.getColumnCount());
            sqlparam.setValueType(valtype);
            sqlparam.setFieldHandler(finfo);
            expressions.add(new SQLEquals(new SQLColumns(table, finfo.getValueColumns()),
                                          sqlparam));
          }
            
          return table;
        }
        
      }
    }
    
    private String createTableAlias(String prefix) {
      if (!debug) {
        return prefix + (tblcount++);
      }
      
      // Create new alias
      String alias = prefix + (tblcount++);
      // Check if alias collides with variable name or existing table.
      if (jdoquery.hasVariableName(alias) ||
          tables.containsKey(alias)) {
        // FIXME: May not be necessary to do this when not in debug mode.
        return createTableAlias(prefix);
      } else {
        return alias;
      }
    }
    
    // -----------------------------------------------------------------------------
    // Pre-build JDO query analysis
    // -----------------------------------------------------------------------------

    private void analyze() {
      if (jdoquery == null) {
        throw new OntopiaRuntimeException("JDO query not registered with SQLbuilder build info.");
      }
      
      // TASK: We're trying to collect field info for variables of
      // non-identifiable type (aggregate or primitive). We do this by
      // matching them up with other field infos that they are used
      // together with.

      // Start analysis with JDO filter
      JDOExpressionIF filter = jdoquery.getFilter();
      if (filter != null) {
        analyzeExpression(filter);
      }
    }

    protected void analyzeExpression(JDOExpressionIF jdoexpr) {
      // Check expression type and delegate to appropriate analyze method.
      switch (jdoexpr.getType()) {

      // SIMPLE EXPRESSIONS
      case JDOExpressionIF.EQUALS:
        // Compare left and right values
        JDOEquals eq = (JDOEquals)jdoexpr;
        analyzeCompatible(eq.getLeft(), eq.getRight());
        break;
      case JDOExpressionIF.CONTAINS:
        JDOContains contains = (JDOContains)jdoexpr;
        analyzeCompatible(contains.getLeft(), contains.getRight());
        break;
      case JDOExpressionIF.NOT_EQUALS:
        JDONotEquals neq = (JDONotEquals)jdoexpr;
        analyzeCompatible(neq.getLeft(), neq.getRight());
        break;
      case JDOExpressionIF.IS_EMPTY:
        // Nothing to compare with
        break;
      case JDOExpressionIF.STARTS_WITH:
        JDOStartsWith swith = (JDOStartsWith)jdoexpr;
        analyzeCompatible(swith.getLeft(), swith.getRight());
        break;
      case JDOExpressionIF.ENDS_WITH:
        JDOEndsWith ewith = (JDOEndsWith)jdoexpr;
        analyzeCompatible(ewith.getLeft(), ewith.getRight());
        break;
      case JDOExpressionIF.LIKE:
        JDOLike like = (JDOLike)jdoexpr;
        analyzeCompatible(like.getLeft(), like.getRight());
        break;

      // LOGICAL EXPRESSIONS
      case JDOExpressionIF.AND:
        // Analyze nested expression
        analyzeExpression(((JDOAnd)jdoexpr).getExpressions());
        break;
      case JDOExpressionIF.OR:
        analyzeExpression(((JDOOr)jdoexpr).getExpressions());
        break;
      case JDOExpressionIF.NOT:
        analyzeExpression(((JDONot)jdoexpr).getExpression());
        break;
      case JDOExpressionIF.BOOLEAN:
        // no-op
        break;
      case JDOExpressionIF.VALUE_EXPRESSION: {
        // TODO: should verify that expression is boolean

        // Make sure that comparison functions get analyzed properly
        JDOValueIF value = ((JDOValueExpression)jdoexpr).getValue();
        if (value.getType() == JDOValueIF.FUNCTION) {
          JDOFunction func = (JDOFunction)value;
          String fname = func.getName();
          if (">".equals(fname) || ">=".equals(fname) ||
              "<".equals(fname) || "<=".equals(fname) ||
              "substring".equals(fname)) {
            JDOValueIF[] args = func.getArguments();
            analyzeCompatible(args[0], args[1]);
          }
        }
        break;
      }
      // SET OPERATIONS
      case JDOExpressionIF.SET_OPERATION:
        break;
      default:
        throw new OntopiaRuntimeException("Invalid expression: '" + jdoexpr + "'");
      }
    }
    
    protected void analyzeExpression(JDOExpressionIF[] exprs) {
      // Loop over JDO expressions and analyze them individually
      for (int i=0; i < exprs.length; i++) {
        analyzeExpression(exprs[i]);
      }
    }

    //! protected void analyzeCompatible(JDOValueIF[] values) {
    //!   if (values.length < 2) return;
    //!   JDOValueIF pv = values[0];
    //!   for (int i=1; i < values.length; i++) {
    //!     analyzeCompatible(pv, values[i]);
    //!     pv = values[i];
    //!   }
    //! }
  
    protected JDOValueIF extractValue(JDOValueIF jdovalue) {
      // Extract variable from value
      switch (jdovalue.getType()) {
      case JDOValueIF.FIELD:
      case JDOValueIF.VARIABLE:
      case JDOValueIF.PARAMETER:
      case JDOValueIF.PRIMITIVE:
      case JDOValueIF.OBJECT:
      case JDOValueIF.STRING:
      case JDOValueIF.COLLECTION:
      case JDOValueIF.NULL:
        return (JDOValueIF)jdovalue;
      case JDOValueIF.FUNCTION: {
        return ((JDOFunction)jdovalue).getArguments()[0];
      }
      case JDOValueIF.NATIVE_VALUE:
        return ((JDONativeValue)jdovalue).getRoot();
      default:
        throw new OntopiaRuntimeException("Cannot extract root from unknown JDOValueIF: " + jdovalue);
      }
    }
  
    protected JDOValueIF extractRootValue(JDOValueIF jdovalue) {
      // Extract variable from value
      switch (jdovalue.getType()) {
      case JDOValueIF.FIELD:
        return extractRootValue(((JDOField)jdovalue).getRoot());
      case JDOValueIF.VARIABLE:
      case JDOValueIF.PARAMETER:
      case JDOValueIF.PRIMITIVE:
      case JDOValueIF.OBJECT:
      case JDOValueIF.STRING:
      case JDOValueIF.COLLECTION:
      case JDOValueIF.NULL:
        return (JDOValueIF)jdovalue;
      case JDOValueIF.FUNCTION: {
        //! return (JDOValueIF)jdovalue;
        //! System.out.println("FOO: " + Arrays.asList(((JDOFunction)jdovalue).getArguments()));
        //! System.out.println("BAR: " + extractRootValue(((JDOFunction)jdovalue).getArguments()[0]));
        return extractRootValue(((JDOFunction)jdovalue).getArguments()[0]);
      }
      case JDOValueIF.NATIVE_VALUE:
        return extractRootValue(((JDONativeValue)jdovalue).getRoot());
      default:
        throw new OntopiaRuntimeException("Cannot extract root from unknown JDOValueIF: " + jdovalue);
      }
    }
    
    protected void analyzeCompatible(JDOValueIF value1, JDOValueIF value2) {

      // TASK: We're trying to collect field info for variables of
      // aggregate[1] or alias type.

      // extract leaf values
      value1 = extractValue(value1);
      value2 = extractValue(value2);

      // extract root values
      JDOValueIF rvalue1 = extractRootValue(value1);
      JDOValueIF rvalue2 = extractRootValue(value2);

      //! System.out.println("\nROOT: " + value1 + "/" + rvalue1 + " " + value2 + "/" + rvalue2);
      
      // Return if a value does not reference a root value or they're
      // referencing the same value.
      if (rvalue1 == null || rvalue2 == null || rvalue1.equals(rvalue2)) {
        return;
      }

      // Figure out if value types are identifiable
      boolean identifiable1 = isIdentifiableValueType(rvalue1, this);
      boolean identifiable2 = isIdentifiableValueType(rvalue2, this);
      
      // Ignore analysis if both are identifiable.
      int rvtype1 = rvalue1.getType();
      int rvtype2 = rvalue2.getType();

      //! System.out.println("1: " + (rvalue1 == value1) + " " +
      //!                    getIdentifiableValueType(rvalue1, this) + " " + getValueType(rvalue1, this));
      //! System.out.println("2: "  + (rvalue2 == value2) + " "
      //!                    + getIdentifiableValueType(rvalue2, this) + " " + getValueType(rvalue2, this));

      if (identifiable1 && !identifiable2 &&
          (rvtype2 == JDOValueIF.VARIABLE ||
           rvtype2 == JDOValueIF.PARAMETER)) {
        analyzeMatchingNonIdentifiableValue(rvalue2, value1);
      } else if (identifiable2 && !identifiable1 &&
               (rvtype1 == JDOValueIF.VARIABLE ||
                rvtype1 == JDOValueIF.PARAMETER)) {
        analyzeMatchingNonIdentifiableValue(rvalue1, value2);
      } else if (!identifiable1 && !identifiable2) {

        // figure out table if value 1
        FieldInfoIF finfo1 = getFieldInfo(value1, this);
        if (finfo1 == null) {
          finfo1 = nfvals.get(value1);
        }
        String table1 = (finfo1 != null && finfo1.getTable() != null ? finfo1.getTable() : null);
        
        // figure out table if value 2
        FieldInfoIF finfo2 = getFieldInfo(value2, this);
        if (finfo2 == null) {
          finfo2 = nfvals.get(value2);
        }
        String table2 = (finfo2 != null && finfo2.getTable() != null ? finfo2.getTable() : null);

        // Analyze left value
        if (!nfvals.containsKey(value1)) {
          // Figure out field info
          if (finfo1 != null) {
            nfvals.put(value1, finfo1);
          }
          else if (finfo2 != null) {
            nfvals.put(value1, finfo2);
          }

          // Figure out table name
          if (table1 != null) {
            ntvals.put(value1, table1);
          } else if (table2 != null) {
            ntvals.put(value1, table2);
          } else {
            if (nfvals.containsKey(rvalue1)) {
              FieldInfoIF finfo = nfvals.get(rvalue1);
              if (finfo.getTable() != null) {
                ntvals.put(value1, finfo.getTable());
              }
            }
            if (!ntvals.containsKey(value1) && nfvals.containsKey(rvalue2)) {
              FieldInfoIF finfo = nfvals.get(rvalue2);
              if (finfo.getTable() != null) {
                ntvals.put(value1, finfo.getTable());
              }
            }
          }
        }

        // Analyze right value
        if (!nfvals.containsKey(value2)) {
          // Figure out field info
          if (finfo2 != null) {
            nfvals.put(value2, finfo2);
          }
          else if (finfo1 != null) {
            nfvals.put(value2, finfo1);
          }

          // Figure out table name
          if (table2 != null) {
            ntvals.put(value2, table2);
          } else if (table1 != null) {
            ntvals.put(value2, table1);
          } else {
            if (nfvals.containsKey(rvalue2)) {
              FieldInfoIF finfo = nfvals.get(rvalue2);
              if (finfo.getTable() != null) {
                ntvals.put(value2, finfo.getTable());
              }
            }
            if (!ntvals.containsKey(value2) && nfvals.containsKey(rvalue1)) {
              FieldInfoIF finfo = nfvals.get(rvalue1);
              if (finfo.getTable() != null) {
                ntvals.put(value2, finfo.getTable());
              }
            }
          }
        }
                
      }
    }

    protected void analyzeMatchingNonIdentifiableValue(JDOValueIF value1, JDOValueIF value2) {
      // Check that value1 is either a variable or parameter
      // NOTE: this check is not really neccessary
      switch (value1.getType()) {      
      case JDOValueIF.VARIABLE:
      case JDOValueIF.PARAMETER:
        break;
      default:
        throw new OntopiaRuntimeException("Non-supported named value: '" + value1 + "'");
      }

      // Ignore when entry already exists
      if (nfvals.containsKey(value1)) {
        return;
      }
      // Get field info of secondary value
      FieldInfoIF finfo = getFieldInfo(value2, this);

      // Register matching field info
      nfvals.put(value1, finfo);
      //! System.out.println("==> NF: " + value1 + " " + finfo + " " + finfo.getTable() + " from " + value2);
      
      // If field info have no associated table get table of identifiable parent
      if (!finfo.isIdentityField()) {
        if (finfo.getTable() == null) {
          Class ctype = getIdentifiableValueType(value2, this);
          // Register table of identifiable parent
          ntvals.put(value1, mapping.getClassInfo(ctype).getMasterTable());
        } else {
          // Register table of identifiable parent
          ntvals.put(value1, finfo.getTable());
        }
      }
    }
    
  }

  // -----------------------------------------------------------------------------
  // Build SQL query object
  // -----------------------------------------------------------------------------
  
  public SQLQuery makeQuery(JDOQuery jdoquery, ObjectAccessIF oaccess) {

    // Initialize SQL query object
    SQLQuery sqlquery = new SQLQuery();
    sqlquery.setDistinct(jdoquery.getDistinct());
    sqlquery.setOffset(jdoquery.getOffset());
    sqlquery.setLimit(jdoquery.getLimit());
    
    // Create build info instance
    BuildInfo info = new BuildInfo();
    // Register object access
    info.oaccess = oaccess;
    // Register queries
    info.jdoquery = jdoquery;
    info.sqlquery = sqlquery;
    // Analyze JDO query
    info.analyze();

    // Is query a set operation query?
    if (!jdoquery.isSetQuery()) {
      
      // Get JDO filter
      List<SQLExpressionIF> expressions = new ArrayList<SQLExpressionIF>();
      JDOExpressionIF filter = jdoquery.getFilter();
      if (filter != null) {
        produceExpression(filter, expressions, info);
      }
      
      // Add select clauses
      List<Object> select = jdoquery.getSelect();
      int select_length = select.size();
      for (int i=0; i < select_length; i++) {
        Object value = select.get(i);
        // create alias
        String alias = String.valueOf((char)(97+i));
        if (value instanceof JDOValueIF) {
          // value
          SQLValueIF sval = produceSelectSQLValueIF((JDOValueIF)value, expressions, info);
          sval.setAlias(alias);
          sqlquery.addSelect(sval);
        } else {
          // aggregate
          SQLAggregateIF sagg = produceSelectSQLAggregateIF((JDOAggregateIF)value, expressions, info);
          sagg.setAlias(alias);
          sqlquery.addSelect(sagg);
        }
      }
      
      // Add order by clauses
      List<JDOOrderBy> orderby = jdoquery.getOrderBy();
      int orderby_length = orderby.size();
      for (int i=0; i < orderby_length; i++) {
        sqlquery.addOrderBy(produceSQLOrderBy(orderby.get(i), expressions, info));
      }        
      
      // Make sql expressions and register filter
      if (!expressions.isEmpty()) {
        sqlquery.setFilter(makeAndExpression(expressions));
      }
      
      // Return produced SQL query
      return sqlquery;
    } else {
      
      // Produce set operation filter
      JDOSetOperation jdoset = (JDOSetOperation)jdoquery.getFilter();
      SQLSetOperation sqlset = produceSetOperation(jdoset, info);
      sqlquery.setFilter(sqlset);

      // Copy select clauses from first sub set and add aggregation if specified.
      SQLQuery _first = getFirstSQLQuery(sqlset);
      Map<Object, Object> valuemap = new HashMap<Object, Object>();
      List<Object> jdoselect = jdoquery.getSelect();
      List<Object> sqlselect = _first.getSelect();
      //! System.out.println("JS: " + jdoselect + " SS: " + sqlselect);
      for (int i=0; i < jdoselect.size(); i++) {
        Object jdoval = jdoselect.get(i);
        SQLValueIF sqlval = (SQLValueIF)sqlselect.get(i); // FIXME: always SQLValueIF?
        // create alias
        String alias = String.valueOf((char)(97+i));
        // copy values
        if (jdoval instanceof JDOValueIF) {
          sqlval.setAlias(alias);
          SQLValueIF pval = new SQLValueReference(sqlval);
          sqlquery.addSelect(pval);
          valuemap.put(jdoval, pval);
        } else {
          // selected is aggregate
          sqlval.setAlias(alias);
          int aggtype = ((JDOAggregateIF)jdoval).getType();
          SQLAggregateIF pval = new SQLAggregateReference(wrapAggregate(aggtype, sqlval));
          sqlquery.addSelect(pval);
          valuemap.put(jdoval, pval);
        }
      }
      
      // Add order by clauses.
      List<JDOOrderBy> jdoorderby = jdoquery.getOrderBy();
      for (int i=0; i < jdoorderby.size(); i++) {
        JDOOrderBy jdoob = jdoorderby.get(i);
        int sqlorder = getSQLOrder(jdoob);
        // TODO: USE ALIASES INSTEAD
        if (jdoob.isAggregate()) {
          JDOAggregateIF jdoagg = jdoob.getAggregate();
          SQLAggregateIF sqlagg = (SQLAggregateIF)valuemap.get(jdoagg);
          if (sqlagg == null) {
            throw new OntopiaRuntimeException("SQL aggregate for JDO aggregate not found: " + jdoagg);
          }
          sqlquery.addOrderBy(new SQLOrderBy(sqlagg, sqlorder));
        } else {
          JDOValueIF jdoval = jdoob.getValue();
          SQLValueIF sqlval = (SQLValueIF)valuemap.get(jdoval);
          if (sqlval == null) {
            throw new OntopiaRuntimeException("SQL value for JDO value not found: " + jdoval);
          }
          sqlquery.addOrderBy(new SQLOrderBy(sqlval, sqlorder));
        }
      }        
      return sqlquery;
    }
  }
  
  protected SQLQuery getFirstSQLQuery(SQLSetOperation sqlset) {
    Object first = sqlset.getSets().get(0);
    if (first instanceof SQLQuery) {
      return (SQLQuery)first;
    } else {
      return getFirstSQLQuery((SQLSetOperation)first);
    }
  }
  
  protected SQLOrderBy produceSQLOrderBy(JDOOrderBy orderby, List<SQLExpressionIF> expressions, BuildInfo info) {
    int order = getSQLOrder(orderby);
      
    if (orderby.isAggregate()) {
      return new SQLOrderBy(produceSelectSQLAggregateIF(orderby.getAggregate(), expressions, info), order);
    } else {
      return new SQLOrderBy(produceSelectSQLValueIF(orderby.getValue(), expressions, info), order);
    }
  }

  protected int getSQLOrder(JDOOrderBy orderby) {
    if (orderby.getOrder() == JDOOrderBy.ASCENDING) {
      return SQLOrderBy.ASCENDING;
    } else {
      return SQLOrderBy.DESCENDING;
    }
  }
  
  protected SQLValueIF produceSelectSQLValueIF(JDOValueIF value, List<SQLExpressionIF> expressions, BuildInfo info) {
    // Note: all table objects have been created at this time
    switch (value.getType()) {
    case JDOValueIF.PARAMETER:
    case JDOValueIF.VARIABLE: {
      // Get value type
      Class valtype = getValueType(value, info);
      
      // Note: aggregate values use special field infos
      FieldInfoIF finfo;
      
      if (isAggregateType(valtype) || isPrimitiveType(valtype)) {
        // Non-identifiable value type
        finfo = (FieldInfoIF)info.nfvals.get(value);
      } else {
        // Identifiable or primitive value type
        finfo = getFieldInfo(value, info);
      }
      
      // Register selected tuple
      SQLTable table = info.createNamedValueTable(value, expressions);
      SQLColumns columns = new SQLColumns(table, finfo.getValueColumns());
      columns.setValueType(valtype);
      columns.setFieldHandler(finfo);
      return columns;
    }
    case JDOValueIF.FIELD: {
      // FIXME: Field root must be a variable in this case.
      // FIXME: Should select all inline columns of this table.
      JDOField field = (JDOField)value;
      // NOTE: Joins produced to get at selected columns are registered.
      // FIXME: The following only works for M:M fields.

      // FIXME: Use following code to identify SQLColumns table. Later
      // use that information to select _all_ needed data columns in
      // that table.
      // FIXME: Should we maintain a map of field objects that have been resolved/joined?

      // FIXME: Error if field is N-ary
      Values values = produceFieldValues(field, null, expressions, info);
      
      //! System.out.println("=> " + field + " - " + values + " | " + values.finfo);

      // Select either keys or inline values
      //! boolean keyonly = false;
      //! if (finfo.isReferenceField() && !keyonly) {
      //!   String[] vcols = (keyonly ? finfo.getJoinKeys() : getInlineColumns(finfo));
      //!   
      //! } else {
        SQLColumns columns = (SQLColumns)values.vcols;
        columns.setValueType(getValueType(field, info));
        columns.setFieldHandler(getFieldInfo(field, info));
        return columns;
        //! }
    }
    case JDOValueIF.NULL: {
      return new SQLNull();
    }
    default:
      throw new OntopiaRuntimeException("Non-supported select value: '" + value + "'");
    }
  }

  protected String[] getKeyColumns(FieldInfoIF finfo) {
    return finfo.getValueClassInfo().getIdentityFieldInfo().getValueColumns();
  }
  
  protected String[] getInlineColumns(FieldInfoIF finfo) {
    ClassInfoIF cinfo = finfo.getValueClassInfo();
    List<String> vcols = new ArrayList<String>();
    FieldUtils.addColumns(cinfo.getIdentityFieldInfo(), vcols);
    FieldUtils.addColumns(cinfo.getOne2OneFieldInfos(), vcols);
    return FieldUtils.toStringArray(vcols);
  }

  protected SQLAggregateIF produceSelectSQLAggregateIF(JDOAggregateIF aggregate, List<SQLExpressionIF> expressions, BuildInfo info) {
    // Produce sql value
    SQLValueIF sqlvalue = produceSelectSQLValueIF(aggregate.getValue(), expressions, info);
    // Then wrap in aggregate
    return wrapAggregate(aggregate.getType(), sqlvalue);
  }

  protected SQLAggregateIF wrapAggregate(int aggtype, SQLValueIF sqlvalue) {
    //wrap SQL value in aggregate
    switch (aggtype) {      
    case JDOAggregateIF.COUNT:
      // Wrap sql value in aggregate function
      return new SQLAggregate(sqlvalue, SQLAggregateIF.COUNT);
    default:
      throw new OntopiaRuntimeException("Invalid aggregate function type: '" + aggtype + "'");
    }
  }

  protected SQLExpressionIF makeAndExpression(List<SQLExpressionIF> expressions) {
    if (expressions.size() > 1) {
      SQLExpressionIF[] exprlist = new SQLExpressionIF[expressions.size()];
      expressions.toArray(exprlist);
      return new SQLAnd(exprlist);
    }
    else if (expressions.size() == 1) {
      return expressions.get(0);
    } else {
      throw new OntopiaRuntimeException("No expressions were found.");
    }
  }
  
  protected SQLExpressionIF makeOrExpression(SQLExpressionIF[] expressions) {
    if (expressions.length > 1) {
      return new SQLOr(expressions);
    }
    else if (expressions.length == 1) {
      return expressions[0];
    } else {
      throw new OntopiaRuntimeException("No expressions were found.");
    }
  }
  
  protected void produceExpression(JDOExpressionIF jdoexpr, List<SQLExpressionIF> expressions, BuildInfo info) {
    // Check expression type and delegate to appropriate produce method.
    switch (jdoexpr.getType()) {
      // method calls
    case JDOExpressionIF.EQUALS: {
      JDOEquals expr = (JDOEquals)jdoexpr;
      checkCompatibility(expr.getLeft(), expr.getRight(), info);
      produceEquals(expr.getLeft(), expr.getRight(), expressions, info);
      return;
    }
    case JDOExpressionIF.NOT_EQUALS: {
      JDONotEquals expr = (JDONotEquals)jdoexpr;
      checkCompatibility(expr.getLeft(), expr.getRight(), info);
      produceNotEquals(expr.getLeft(), expr.getRight(), expressions, info);
      return;
    }
    case JDOExpressionIF.CONTAINS: {
      JDOContains expr = (JDOContains)jdoexpr;
      checkCompatibility(expr.getLeft(), Collection.class, info);
      // FIXME: The following does not work.
      //! Class vtype = checkCompatibility(expr.getLeft(), expr.getRight(), info);
      produceContains(expr.getLeft(), expr.getRight(), expressions, info);
      return;
    }
    case JDOExpressionIF.IS_EMPTY: {
      JDOIsEmpty expr = (JDOIsEmpty)jdoexpr;
      checkCompatibility(expr.getValue(), Collection.class, info);
      produceIsEmpty(expr.getValue(), expressions, info);
      return;
    }
    case JDOExpressionIF.STARTS_WITH: {
      JDOStartsWith expr = (JDOStartsWith)jdoexpr;
      checkCompatibility(expr.getLeft(), expr.getRight(), info);
      produceStartsWith(expr.getLeft(), expr.getRight(), expressions, info);
      return;
    }
    case JDOExpressionIF.ENDS_WITH: {
      JDOEndsWith expr = (JDOEndsWith)jdoexpr;
      checkCompatibility(expr.getLeft(), expr.getRight(), info);
      produceEndsWith(expr.getLeft(), expr.getRight(), expressions, info);
      return;
    }
    case JDOExpressionIF.LIKE: {
      JDOLike expr = (JDOLike)jdoexpr;
      checkCompatibility(expr.getLeft(), expr.getRight(), info);
      produceLike(expr.getLeft(), expr.getRight(), expr.getCaseSensitive(), expressions, info);
      return;
    }
      // logical operators
    case JDOExpressionIF.AND: {
      produceAnd((JDOAnd)jdoexpr, expressions, info);
      return;
    }
    case JDOExpressionIF.OR: {
      produceOr((JDOOr)jdoexpr, expressions, info);
      return;
    }
    case JDOExpressionIF.NOT: {
      produceNot((JDONot)jdoexpr, expressions, info);
      return;
    }
    case JDOExpressionIF.BOOLEAN: {
      produceBoolean((JDOBoolean)jdoexpr, expressions, info);
      return;
    }
    case JDOExpressionIF.VALUE_EXPRESSION: {
      // produce expression value
      produceValueExpression((JDOValueExpression)jdoexpr, expressions, info);
      return;
    }
    //!   // set operators
    //! case JDOExpressionIF.SET_OPERATION: {
    //!   produceSetOperation((JDOSetOperation)jdoexpr, expressions, info);
    //!   return;
    //! }
    default:
      throw new OntopiaRuntimeException("Expression is of unknown type: '" + jdoexpr + "'");
    }
  }

  protected SQLSetOperation produceSetOperation(JDOSetOperation setop_expr, BuildInfo info) {
    List<Object> jdosets = setop_expr.getSets();
    List<Object> sqlsets = new ArrayList<Object>(jdosets.size());

    int length = jdosets.size();
    for (int i=0; i < length; i++) {
      Object set = jdosets.get(i);
      if (set instanceof JDOQuery) {
        sqlsets.add(makeQuery((JDOQuery)set, info.oaccess));
      } else {
        sqlsets.add(produceSetOperation((JDOSetOperation)set, info));
      }
    }

    int optype;
    switch (setop_expr.getOperator()) {
    case JDOSetOperation.UNION:
      optype = SQLSetOperation.UNION;
      break;
    case JDOSetOperation.UNION_ALL:
      optype = SQLSetOperation.UNION_ALL;
      break;
    case JDOSetOperation.INTERSECT:
      optype = SQLSetOperation.INTERSECT;
      break;
    case JDOSetOperation.EXCEPT:
      optype = SQLSetOperation.EXCEPT;
      break;
    default:
      throw new OntopiaRuntimeException("Unsupported set operator: '" + setop_expr.getOperator() + "'");
    }
    return new SQLSetOperation(sqlsets, optype);
  }

  protected void produceBoolean(JDOBoolean boolean_expr, List<SQLExpressionIF> expressions, BuildInfo info) {
    SQLValueIF value = new SQLPrimitive(0, Types.INTEGER);
    if (boolean_expr.getValue()) {
      expressions.add(new SQLEquals(value, value));
    } else {
      expressions.add(new SQLNotEquals(value, value));
    }
  }

  protected void produceValueExpression(JDOValueExpression jdoexpr, List<SQLExpressionIF> expressions, BuildInfo info) {
    expressions.add(new SQLValueExpression(produceValue(jdoexpr.getValue(), expressions, info)));
  }
  
  protected void produceAnd(JDOAnd and_expr, List<SQLExpressionIF> expressions, BuildInfo info) {
    expressions.add(new SQLAnd(produceExpressions(and_expr.getExpressions(), info)));
  }
  
  protected void produceNot(JDONot not_expr, List<SQLExpressionIF> expressions, BuildInfo info) {
    JDOExpressionIF jdoexpr = not_expr.getExpression();
    List<SQLExpressionIF> templist = new ArrayList<SQLExpressionIF>();
    produceExpression(jdoexpr, templist, info);
    expressions.add(new SQLNot(new SQLExists(makeAndExpression(templist))));
  }
  
  protected void produceOr(JDOOr or_expr, List<SQLExpressionIF> expressions, BuildInfo info) {
    // TODO: Break expressions into SQLExists if they are complex enough.
    // ISSUE: When are they complex enough? Only when new variables
    // are introduced?    
    //! expressions.add(new SQLOr(produceExpressions(or_expr.getExpressions(), info)));    
    JDOExpressionIF[] jdoexprs = or_expr.getExpressions();
    SQLExpressionIF[] sqlexprs = new SQLExpressionIF[jdoexprs.length];
    for (int i=0; i < jdoexprs.length; i++) {
      List<SQLExpressionIF> templist = new ArrayList<SQLExpressionIF>();
      produceExpression(jdoexprs[i], templist, info);
      sqlexprs[i] = new SQLExists(makeAndExpression(templist));
    }
    expressions.add(makeOrExpression(sqlexprs));
  }
  
  protected SQLExpressionIF[] produceExpressions(JDOExpressionIF[] jdoexprs, BuildInfo info) {
    // Loop over JDO expression and produce SQL expression
    List<SQLExpressionIF> expressions = new ArrayList<SQLExpressionIF>();
    for (int i=0; i < jdoexprs.length; i++) {
      produceExpression(jdoexprs[i], expressions, info);
    }
    SQLExpressionIF[] result = new SQLExpressionIF[expressions.size()];
    expressions.toArray(result);
    return result;
  }
  
  // -----------------------------------------------------------------------------
  // SQL expression
  // -----------------------------------------------------------------------------
  
  protected void produceEquals(JDOValueIF left, JDOValueIF right, List<SQLExpressionIF> expressions, BuildInfo info) {
    
    // -----------------------------------------------------------------------------
    // EXPRESSION: variable1.field == variable2
    //  - join variable1 field value columns with identity field columns of variable2
    //  : A1.type == T1 - > A1.type = T1.id [not an ordinary join / no join object]
    
    // EXPRESSION: variable.field == parameter
    //  - compare variable field value columns with parameter values
    //  : A1.type == P1 - > A1.type = ?P.id
    
    // EXPRESSION: variable1.field == variable2.field
    //  - join variable1 field value columns with variable2 field value columns
    //  : A1.type == A2.type - > A1.type = A2.type
    
    // EXPRESSION: variable == parameter
    //  - compare identity field columns of variable with parameter values
    //  : A1 == P1 - > A1.id = ?P.id
    // -----------------------------------------------------------------------------

    // Produce values for left and right JDO value and wrap in equals expression
    expressions.add(new SQLEquals(produceValue(left, expressions, info), 
                                  produceValue(right, expressions, info)));
  }

  protected void produceNotEquals(JDOValueIF left, JDOValueIF right, List<SQLExpressionIF> expressions, BuildInfo info) {

    // Produce values for left and right JDO value and wrap in equals expression
    expressions.add(new SQLNotEquals(produceValue(left, expressions, info), 
                                     produceValue(right, expressions, info)));
  }
  
  protected void produceContains(JDOValueIF left, JDOValueIF right, List<SQLExpressionIF> expressions, BuildInfo info) {

    // -----------------------------------------------------------------------------
    // EXPRESSION: variable1.field[OM].contains(variable2.field)
    // EXPRESSION: variable1.field[MM].contains(variable2.field)
    // EXPRESSION: variable1.field<collection>.contains(variable2.field)
    // EXPRESSION: parameter<collection>.contains(variable)
    // EXPRESSION: parameter<collection>.contains(variable.field)
    // EXPRESSION: object.field[OM].contains(...)
    // EXPRESSION: object.field[MM].contains(...)
    // EXPRESSION: variable1.field<collection>.contains(...)
    // EXPRESSION: variable1<collection>.contains(...)
    // EXPRESSION: <collection>.contains(...)
    //  - ...
    //
    // COMMON:
    //  - register all variable tables using variable name as the table alias
    // -----------------------------------------------------------------------------

    // Check left value type
    switch (left.getType()) {
    case JDOValueIF.FIELD: {  // -- left
      // -----------------------------------------------------------------------------
      // EXPRESSION: X.field.contains(variable)        
      // -----------------------------------------------------------------------------
      JDOField field = (JDOField)left;
      
      // Complain if not collection field
      FieldInfoIF finfo = getFieldInfo(field, info);
      if (!finfo.isCollectionField() &&
          (finfo.getValueClassInfo() != null &&
           finfo.getValueClassInfo().getStructure() != ClassInfoIF.STRUCTURE_COLLECTION)) {
        throw new OntopiaRuntimeException("contains's left field must be a collection field: '" + left + "'");
      }
      
      // Check right value type
      SQLTable endtable = null;
      switch (right.getType()) {
        
      case JDOValueIF.PARAMETER:
      case JDOValueIF.VARIABLE: { // -- right
        // Get right value table
        endtable = info.createNamedValueTable(right, expressions);
        break;
      }
      case JDOValueIF.OBJECT: {
        break;
      }
      default:
        throw new OntopiaRuntimeException("Unsupported contains right value: '" + right + "'");
      }

      // Figure out if the field value is a collection
      JDOValueIF root = field.getRoot();
      switch (root.getType()) {
      //! case JDOValueIF.PARAMETER: {
      //!   TODO: not yet supported
      //! }
      case JDOValueIF.PARAMETER:
      case JDOValueIF.VARIABLE: {
        
        // Produce field values [and join many-tables]
        //! Values lvalues = produceFieldValues(field, endtable, expressions, info);
        Values lvalues = produceVariableFieldValues(root, field.getPath(), endtable, expressions, info);
        SQLValueIF vcols = lvalues.vcols;
        
        // TODO: If left value evaluates to a collection at runtime we
        // have to invert the operation. If collection is empty the
        // expression is false.
        
        // If M:M field we also have to join with rvalue table (if different).
        if (finfo.getCardinality() == FieldInfoIF.MANY_TO_MANY ||
            finfo.getCardinality() == FieldInfoIF.ONE_TO_MANY) {
          // Produce right value
          SQLValueIF rvalue = produceValue(right, expressions, info);
          // Compare M:M many-keys with right value
          //! System.out.println("Joining: MM2 " + vcols + " <-> " + rvalue);
          expressions.add(new SQLEquals(vcols, rvalue));
        }
        // Need extra join if field value is of collection structure
        else if (finfo.getValueClassInfo() != null &&
                 finfo.getValueClassInfo().getStructure() == ClassInfoIF.STRUCTURE_COLLECTION) {
          ClassInfoIF vcinfo = finfo.getValueClassInfo();
          SQLTable table = new SQLTable(vcinfo.getMasterTable(), info.createTableAlias("T"));
          SQLColumns rvalue = new SQLColumns(table, vcinfo.getIdentityFieldInfo().getValueColumns());      
          // Add join between field table and collection structure table
          expressions.add(new SQLEquals(vcols, rvalue));
          
          // Produce right value (same as in last if)
          SQLValueIF rvalue1 = produceValue(right, expressions, info);
          SQLValueIF rvalue2 = new SQLColumns(table, vcinfo.getFieldInfoByName("element").getValueColumns());
          expressions.add(new SQLEquals(rvalue1, rvalue2));
          
        } else {
          throw new OntopiaRuntimeException("contains's left field of unknown collection type: '" + left + "'");
        }
        break;
      }
      case JDOValueIF.OBJECT: {
        //! System.out.println("ROOT is object: " + root);
        // TODO: complain when both sides are compile-time collections
        // TODO: use SQLIn
        // Produce field values
        Values lvalues = produceObjectFieldValues((JDOObject)root, field.getPath(), info);        
        if (!(lvalues.finfo.isCollectionField() ||
              (finfo.getValueClassInfo() != null &&
               finfo.getValueClassInfo().getStructure() == ClassInfoIF.STRUCTURE_COLLECTION))) {
          throw new OntopiaRuntimeException("contains's left field is not of collection type: '" + left + "'");
        }
        //! System.out.println("L=> " + lvalues.vcols);
        
        // Expression is false when left field value is empty or null
        if (lvalues.vcols.getType() == SQLValueIF.NULL ||
            (lvalues.vcols.getType() == SQLValueIF.TUPLE && ((SQLTuple)lvalues.vcols).getArity() == 0)) {
          expressions.add(SQLFalse.getInstance());
        } else {
          // Produce right side value
          SQLValueIF rval = produceValue(right, expressions, info);
          //! System.out.println("R=> " + rval);
          
          //! if (lvalues.vcols.getType() == SQLValueIF.TUPLE) {
          if (rval.getArity() == 1 &&
              lvalues.vcols.getType() == SQLValueIF.TUPLE && lvalues.vcols.getArity() > 1) {
            // Loop over field value tuples and compare with right-hand value
            SQLValueIF[] vals = ((SQLTuple)lvalues.vcols).getValues();
            for (int i=0; i < vals.length; i++) {
              expressions.add(new SQLEquals(vals[i], rval));
            }            
          } else {
            expressions.add(new SQLEquals(lvalues.vcols, rval));
          }
        }        
        break;
      }
      default:
        throw new OntopiaRuntimeException("Unsupported contains left value: '" + left + "'");
      }
      return;
    }
    case JDOValueIF.VARIABLE: { // -- left
      // -----------------------------------------------------------------------------
      // EXPRESSION: variable<collection>.contains(...)
      // -----------------------------------------------------------------------------
      
      // throw new OntopiaRuntimeException("variable<collection>.contains(...) not yet supported.");
      
      JDOVariable var = (JDOVariable)left;
      
      FieldInfoIF finfo = getFieldInfo(var, info);
      ClassInfoIF cinfo = finfo.getParentClassInfo();

      if (cinfo.getStructure() == ClassInfoIF.STRUCTURE_COLLECTION) {
        // Get value table
        SQLTable table = info.createNamedValueTable(var, expressions);
        // Produce values
        SQLValueIF lvalue = new SQLColumns(table, cinfo.getFieldInfoByName("element").getValueColumns());
        SQLValueIF rvalue = produceValue(right, expressions, info);
        expressions.add(new SQLEquals(lvalue, rvalue));
        return;
      } else {
        throw new OntopiaRuntimeException("variable.contains(...) expression must be of type with collection structure: " + var);
      }
    }
    case JDOValueIF.PARAMETER: { // -- left

      //! FIXME: Collection values are not yet properly supported.
      
      JDOParameter param = (JDOParameter)left;
      String parname = param.getName();

      // Check right value type
      switch (right.getType()) {
        
      case JDOValueIF.VARIABLE: { // -- right
        JDOVariable var = (JDOVariable)right;

        // -----------------------------------------------------------------------------
        // EXPRESSION: parameter<collection>.contains(variable)
        // -----------------------------------------------------------------------------
        //  - morph into IN expression
        //  : P.contains(T1) -> T1.id in (P1, ..., Pn)

        // Note: parameter must be of the same type as the variable

        // so get identity field of variable
        FieldInfoIF finfo = getFieldInfo(var, info); // Note: identity field
        int arity = finfo.getColumnCount();
        if (arity != 1) {
          throw new OntopiaRuntimeException("parameter<collection>.contains(variable) requires a value arity of exactly 1: " + var);
        }
        
        // Create parameter
        SQLParameter sqlparam = new SQLParameter(parname, arity);
        sqlparam.setValueType(info.jdoquery.getParameterType(parname));
        sqlparam.setFieldHandler(finfo);
        
        expressions.add(new SQLIn(produceValue(var, expressions, info), sqlparam));
        return;
      }
      default:
        throw new OntopiaRuntimeException("Unsupported contains right value: '" + right + "'");
      }
    }
    case JDOValueIF.COLLECTION: { // -- left
      // Add expression
      expressions.add(new SQLIn(produceValue(right, expressions, info), produceCollection((JDOCollection)left, info)));
      return;
    }
    default: // -- left
      throw new OntopiaRuntimeException("Unsupported contains left value: '" + left + "'");
    }
  }
  
  protected void produceIsEmpty(JDOValueIF value, List<SQLExpressionIF> expressions, BuildInfo info) {
    
    // Check value type
    switch (value.getType()) {
    case JDOValueIF.FIELD: {
      JDOField field = (JDOField)value;
      
      // Complain if not collection field
      FieldInfoIF finfo = getFieldInfo(field, info);
      if (!finfo.isCollectionField() &&
          (finfo.getValueClassInfo() != null &&
           finfo.getValueClassInfo().getStructure() != ClassInfoIF.STRUCTURE_COLLECTION)) {
        throw new OntopiaRuntimeException("isEmpty's field must be a collection field: '" + field + "'");
      }

      // -----------------------------------------------------------------------------
      // EXPRESSION: variable.field[OM].isEmpty()
      // EXPRESSION: variable.field[MM].isEmpty()
      // EXPRESSION: variable.field<collection>.isEmpty()
      // -----------------------------------------------------------------------------

      // JDO: T1.topicmap = M1 & T1.roles.isEmpty() & T1.types.isEmpty()
      
      List<SQLExpressionIF> lexpressions = new ArrayList<SQLExpressionIF>();
      // Produce left value
      Values lvalues = produceFieldValues(field, null, lexpressions, info);

      // Need extra join if field value is of collection structure
      if (finfo.getValueClassInfo() != null &&
          finfo.getValueClassInfo().getStructure() == ClassInfoIF.STRUCTURE_COLLECTION) {
        ClassInfoIF vcinfo = finfo.getValueClassInfo();
        SQLTable table = new SQLTable(vcinfo.getMasterTable(), info.createTableAlias("T"));
        SQLColumns rvalue = new SQLColumns(table, vcinfo.getIdentityFieldInfo().getValueColumns());      
        // Add join between field table and collection structure table
        lexpressions.add(new SQLEquals(lvalues.vcols, rvalue));
      }
      
      // Make sure that the local expressions go into the "not exists"
      expressions.add(new SQLNot(new SQLExists(makeAndExpression(lexpressions))));
      return;
    }
    case JDOValueIF.VARIABLE: {
      // -----------------------------------------------------------------------------
      // EXPRESSION: variable<collection>.isEmpty()
      // -----------------------------------------------------------------------------

      throw new OntopiaRuntimeException("variable<collection>.isEmpty() not yet supported.");
    }
    //! case JDOValueIF.COLLECTION: {
    //!  TODO: evaluate Collection.isEmpty().
    //!  TODO: same applies to Object<collection>.isEmpty()
    //! }
    default:
      throw new OntopiaRuntimeException("Unsupported isEmpty value: '" + value + "'");
    }
  }

  protected void produceStartsWith(JDOValueIF left, JDOValueIF right,
                                   List<SQLExpressionIF> expressions, BuildInfo info) {
    produceLikeWithPattern(left, right, false, expressions, true, info);
  }
  
  protected void produceEndsWith(JDOValueIF left, JDOValueIF right,
                                 List<SQLExpressionIF> expressions, BuildInfo info) {
    produceLikeWithPattern(left, right, false, expressions, false, info);
  }

  protected void produceLike(JDOValueIF left, JDOValueIF right, boolean caseSensitive,
                             List<SQLExpressionIF> expressions, BuildInfo info) {
    expressions.add(new SQLLike(produceValue(left, expressions, info),
                                produceValue(right, expressions, info), caseSensitive));
  }
  
  protected void produceLikeWithPattern(JDOValueIF left, JDOValueIF right, boolean caseSensitive,
                                       List<SQLExpressionIF> expressions, boolean starts_not_ends, BuildInfo info) {

    //! // Make sure that the value type is of string type
    //! if (!java.lang.String.class.equals(vtype))
    //!   throw new OntopiaRuntimeException("Invalid value type for startsWith expression: " + vtype);
      
    // Produce SQL value for left JDO value
    SQLValueIF lvalue = produceValue(left, expressions, info);
    
    // Check left value arity:
    int arity = lvalue.getArity();
    if (arity != 1) {
      if (starts_not_ends) {
        throw new OntopiaRuntimeException("Arity of left String.startsWith value is not 1: " + arity);
      } else {
        throw new OntopiaRuntimeException("Arity of left String.endsWith value is not 1: " + arity);
      }
    }

    // Create SQL like expression
    switch (right.getType()) {
    case JDOValueIF.STRING:
    case JDOValueIF.PRIMITIVE:
      String value = ((JDOString)right).getValue();
      if (starts_not_ends) {
        expressions.add(new SQLLike(lvalue, 
                                    new SQLPrimitive(value + "%", Types.VARCHAR), caseSensitive));
      } else {
        expressions.add(new SQLLike(lvalue, 
                                    new SQLPrimitive("%" + value, Types.VARCHAR), caseSensitive));
      }
      return;
    default:
      // FIXME: this doesn't work if value is parameter
      // FIXME: this doesn't work if right produces multiple  (something it shouldn't really do methinks)
      expressions.add(new SQLLike(lvalue, produceValue(right, expressions, info), caseSensitive));
      return;
    }
  }

  // -----------------------------------------------------------------------------
  // SQL values
  // -----------------------------------------------------------------------------

  //! protected int getArity(SQLValueIF[] values) {
  //!   int arity = 0;
  //!   for (int i=0; i < values.length; i++) {
  //!     arity = arity + values[i].getArity();
  //!   }
  //!   return arity;    
  //! }

  protected SQLValueIF[] produceValues(JDOValueIF[] values, List<SQLExpressionIF> expressions, BuildInfo info) {
    SQLValueIF[] retval = new SQLValueIF[values.length];
    for (int i=0; i < values.length; i++) {
      retval[i] = produceValue(values[i], expressions, info);
    }
    return retval;
  }
  
  protected SQLValueIF produceValue(JDOValueIF value, List<SQLExpressionIF> expressions, BuildInfo info) {
    // FIXME: Sometimes need both left and right values in orcer to
    // correctly assert value type. This is especially applies to
    // parameters and aggregate variables.
    
    // Check value type
    switch (value.getType()) {
      
    case JDOValueIF.FIELD:
      return produceField((JDOField)value, null, expressions, info);
    case JDOValueIF.VARIABLE:
      return produceVariable((JDOVariable)value, expressions, info);
    case JDOValueIF.PARAMETER:
      return produceParameter((JDOParameter)value, expressions, info);
    case JDOValueIF.PRIMITIVE:
      return producePrimitive((JDOPrimitive)value, info);
    case JDOValueIF.OBJECT:
      return produceObject((JDOObject)value, info);
    case JDOValueIF.STRING:
      return new SQLPrimitive(((JDOString)value).getValue(), Types.VARCHAR);
    case JDOValueIF.COLLECTION:
      return produceCollection((JDOCollection)value, info);
    case JDOValueIF.NULL:
      return new SQLNull();      
    case JDOValueIF.NATIVE_VALUE:
      return produceNativeValue((JDONativeValue)value, expressions, info);
    case JDOValueIF.FUNCTION:
      return produceFunction((JDOFunction)value, expressions, info);
    default:
      throw new OntopiaRuntimeException("Unsupported value: '" + value + "'");
    }
  }
  
  protected SQLValueIF produceField(JDOField field, SQLTable endtable, List<SQLExpressionIF> expressions, BuildInfo info) {
    return produceFieldValues(field, endtable, expressions, info).vcols;
  }
  
  static class Values {
    // [vcols, jcols]
    private Values prev;
    private SQLValueIF vcols; // value columns
    private SQLValueIF jcols; // join columns
    private Class vtype;
    private FieldInfoIF finfo;
    public Values getFirst() {
      if (prev != null) {
        return prev.getFirst();
      } else {
        return this;
      }
    }
    @Override
    public String toString() {
      return "[" + vcols + ", " + jcols + "]";
    }
  }

  protected Values produceFieldValues(JDOField field, SQLTable endtable, List<SQLExpressionIF> expressions, BuildInfo info) {

    // TODO: Add flag indicating that only key value or inline values
    // should be used as vcols. Note that this may only make sense for
    // 1:M and M:M fields?
    
    // FIXME: Do not join tables for navigated field if they have
    // already been navigated.

    // FIXME: We may have to maintain list of values produced for each
    // navigation step, since we don't want F.foo.bar to produce an
    // extra join when F.foo.bar.baz has already been traversed and
    // joined.
    //
    // 1. check F.foo.bar.baz
    // 2. then F.foo.bar
    // 3. then F.foo
    // 4. build new field values for F.foo
    // 5. build new field values for F.foo.bar
    // 6. build new field values for F.foo.bar.baz
      
    JDOValueIF root = field.getRoot();    
    
    // If the root is an object we are able to figure out the field
    // value by traversing the object's field and embedding the actual
    // value(s) in the generated query.
    switch (root.getType()) {
    case JDOValueIF.VARIABLE:
    case JDOValueIF.PARAMETER:
      return produceVariableFieldValues(root, field.getPath(), endtable, expressions, info);
    case JDOValueIF.OBJECT:
      return produceObjectFieldValues((JDOObject)root, field.getPath(), info);
    default:
      throw new OntopiaRuntimeException("Only variables are supported field roots at this time: '" + root + "'");
    }    
  }

  protected Values produceVariableFieldValues(JDOValueIF root, String[] path, SQLTable endtable,
                                              List<SQLExpressionIF> expressions, BuildInfo info) {

    // FIXME: Reuse the same Values instance for every loop.
    Values pvalues = null;  // parent values
    Values cvalues = null;  // child values

    // Get parent cols for variable
    pvalues = new Values();
    pvalues.vcols = produceValue(root, expressions, info);
    pvalues.vtype = getValueType(root, info);
    pvalues.finfo = null;

    // Traverse navigation path one step at a time; join tables and
    // produce field values.
    for (int i=0; i < path.length; i++) {
      String fname = path[i];

      // Complain if field does not have cardinality of 1:1
      // Note: this will only occur when 1:M and M:M fields are followed by another field.
      if (pvalues.finfo != null && pvalues.finfo.getCardinality() != FieldInfoIF.ONE_TO_ONE) {
        throw new OntopiaRuntimeException("Field navigation can only be used with single value fields.");
      }
        
      // Get field info
      ClassInfoIF pcinfo = mapping.getClassInfo(pvalues.vtype);
      FieldInfoIF finfo = pcinfo.getFieldInfoByName(fname);
      if (finfo == null) {
        throw new OntopiaRuntimeException("'" + pvalues.vtype + "' does not have a field called '" + fname + "'.");
      }
          
      // Only set cvalues if not set (applies on first iteration only)
      cvalues = new Values();
      cvalues.prev = pvalues;
      cvalues.vtype = finfo.getValueClass();
      cvalues.finfo = finfo;

      // Keep previous vcols
      SQLValueIF pvalues_vcols = pvalues.vcols;
        
      switch (finfo.getCardinality()) {
      case FieldInfoIF.ONE_TO_ONE: {

        String tblname = finfo.getTable();
        if (tblname == null || (pvalues_vcols instanceof SQLColumns &&
                                tblname.equals(((SQLColumns)pvalues_vcols).getTable().getName()))) {
          // ----------------------------------------------------------------------
          // SAME TABLE
            
          cvalues.vcols = new SQLColumns(((SQLColumns)pvalues_vcols).getTable(), finfo.getValueColumns());
          cvalues.jcols = null; // ain't joining here, since the tables are the same.
        }
        else {
          // ----------------------------------------------------------------------
          // DIFFERENT TABLE

          // FIXME: May want to share table aliases with other external fields
          // Create new table alias.
          SQLTable table = new SQLTable(tblname, info.createTableAlias("T"));
            
          if (!mapping.isDeclared(cvalues.vtype)) {
            // ----------------------------------------------------------------------
            // Primitive field value
            // ----------------------------------------------------------------------

            cvalues.vcols = new SQLColumns(table, finfo.getValueColumns());
            cvalues.jcols = pvalues_vcols;
            // FIXME: Primitive fields can also be external to the master table.
          }
          else {              
            // Field is of declared type              
            ClassInfoIF vcinfo = mapping.getClassInfo(cvalues.vtype);

            // ----------------------------------------------------------------------
            // Aggregate field value
            // ----------------------------------------------------------------------
            if (vcinfo.isAggregate()) {                
              cvalues.vcols = new SQLColumns(table, finfo.getValueColumns());
              cvalues.jcols = new SQLColumns(table, pcinfo.getIdentityFieldInfo().getValueColumns());
            }
            // ----------------------------------------------------------------------
            // Identifiable field value
            // ----------------------------------------------------------------------
            else {
              // Update id cols [FIXME: id colnames not neccessarily the same in external table]
              cvalues.vcols = new SQLColumns(table, finfo.getValueColumns());
              cvalues.jcols = pvalues_vcols;
            }
          }
            
          // Join 1:1 field navigation tables (different tables only)
          //! System.out.println("Joining: OO " + pvalues_vcols + " <-> " + cvalues.jcols);
          //! expressions.add(new SQLJoin(pvalues_vcols, cvalues.jcols));

          //! if (!pvalues_vcols.equals(cvalues.jcols))
          expressions.add(new SQLEquals(pvalues_vcols, cvalues.vcols));
        }
        break;
      }
      case FieldInfoIF.ONE_TO_MANY: {
        SQLTable table;
        String tblname = finfo.getJoinTable();
        // The table is guaranteed to always be the last.
        if (endtable != null) {
          table = endtable;
          if (!tblname.equals(endtable.getName())) {
            throw new OntopiaRuntimeException("Incompatible tables: '" +
                                              tblname + "' <-> '" + endtable.getName() + "'.");
          }
                
        } else {
          table = new SQLTable(tblname, info.createTableAlias("O"));
        }

        // join and value columns
        ClassInfoIF _cinfo = finfo.getValueClassInfo();
        cvalues.jcols = new SQLColumns(table, finfo.getJoinKeys());
        if (_cinfo.isAggregate()) {
          // Aggregate
          cvalues.vcols = new SQLColumns(table, getInlineColumns(finfo));
        } else {
          // Identifiable
          cvalues.vcols = new SQLColumns(table, _cinfo.getIdentityFieldInfo().getValueColumns());
        }
          
        // Join 1:M PARENT-TABLE and JOIN-TABLE
        //! System.out.println("Joining: OM " + pvalues_vcols + " <-> " + cvalues.jcols);
        //! expressions.add(new SQLJoin(pvalues_vcols, cvalues.jcols));
        expressions.add(new SQLEquals(pvalues_vcols, cvalues.jcols));
        break;
      }
      case FieldInfoIF.MANY_TO_MANY: {

        // FIXME: End table is not used.
        ClassInfoIF _cinfo = finfo.getValueClassInfo();
        String tblname = _cinfo.getMasterTable();
        // The table is guaranteed to always be the last.
        if (endtable != null) {
          if (!tblname.equals(endtable.getName())) {
            throw new OntopiaRuntimeException("Incompatible tables: '" +
                                              tblname + "' <-> '" + endtable.getName() + "'.");
          }
        }
        // Join and value columns
        SQLTable j_table = new SQLTable(finfo.getJoinTable(), info.createTableAlias("M"));          
        cvalues.vcols = new SQLColumns(j_table, finfo.getManyKeys());
        cvalues.jcols = new SQLColumns(j_table, finfo.getJoinKeys());

        // Join M:M JOIN-TABLE and VALUE-TABLE
        //! System.out.println("Joining: MM1 " + pvalues_vcols + " <-> " + cvalues.jcols);
        //!expressions.add(new SQLJoin(pvalues_vcols, cvalues.jcols));
        expressions.add(new SQLEquals(pvalues_vcols, cvalues.jcols));
        break;
      }
      default:
        throw new OntopiaRuntimeException("Invalid field cardinality: '" + finfo.getCardinality() + "'");
      }
      
      // Make child values the parent values of the next iteration.
      pvalues = cvalues;

    } // -- end traverse navigation path
      
      // Return the last SQL value created
    return cvalues;
  }

  protected Values produceObjectFieldValues(JDOObject obj, String[] path, BuildInfo info) {
    // TODO: Only 1:1 fields supported.

    // Get object field value
    Object value = obj.getValue();
    Class ctype = value.getClass();
    FieldInfoIF finfo = null;

    for (int i=0; i < path.length; i++) {
      // Make sure that field parent is of declared type
      if (mapping.isDeclared(ctype)) {
        ClassInfoIF cinfo = mapping.getClassInfo(ctype);
        finfo = cinfo.getFieldInfoByName(path[i]);
        if (finfo == null) {
          throw new OntopiaRuntimeException("Parent '" + ctype + "' do not have field called '" +
                                            path[i] + "'");
        }

        if (cinfo.isIdentifiable()) {
          value = info.oaccess.getValue(value, finfo);
        } else {
          try {
            value = finfo.getValue(value);
          } catch (Exception e) {
            throw new OntopiaRuntimeException(e);
          }
        }
        ctype = finfo.getValueClass();
      }
      else {
        throw new OntopiaRuntimeException("Parent of field  '" + path[i] +
                                          "' of undeclared type: '" + ctype + "'");
      }
    }

    Values values = new Values();
    values.finfo = finfo;
    
    if (value != null) {
      if (finfo.isCollectionField()||
          (finfo.getValueClassInfo() != null &&
           finfo.getValueClassInfo().getStructure() == ClassInfoIF.STRUCTURE_COLLECTION)) {
        Collection cvalue = (Collection)value;
        List<SQLValueIF> tuples = new ArrayList<SQLValueIF>(cvalue.size());
        Iterator iter = cvalue.iterator();
        while (iter.hasNext()) {
          List<SQLValueIF> list = new ArrayList<SQLValueIF>(finfo.getColumnCount());
          finfo.retrieveSQLValues(iter.next(), list);
          tuples.add((list.size() == 1 ? list.get(0) : new SQLTuple(list)));
        }
        values.vcols = (tuples.size() == 1 ? tuples.get(0) : new SQLTuple(tuples));
      } else {
        List<SQLValueIF> list = new ArrayList<SQLValueIF>();
        finfo.retrieveSQLValues(value, list);
        values.vcols = (list.size() == 1 ? list.get(0) : new SQLTuple(list));
      }
    } else {
      // FIXME: Arity might not be 1. Use FieldHandlerIF.getColumnCount()?
      values.vcols = new SQLNull();
    }
    return values;
  }
  
  protected SQLValueIF produceVariable(JDOVariable var, List<SQLExpressionIF> expressions, BuildInfo info) {
    String varname = var.getName();
    
    // FIXME: Handle aggregate or alias type variables properly.
    // FIXME: Handle primitive variables properly
    
    // Identifiable type
    if (isIdentifiableVariable(varname, info.jdoquery)) {      
      // Get variable table
      FieldInfoIF finfo = getFieldInfo(var, info); // Note: identity field
      SQLTable table = info.createNamedValueTable(var, expressions);      
      // Produce values
      return new SQLColumns(table, finfo.getValueColumns());
    }
    // Aggregate type or Primitive type
    else if (isAggregateVariable(varname, info.jdoquery) ||
             isPrimitiveVariable(varname, info.jdoquery)) {
      // Get variable table
      FieldInfoIF finfo = info.nfvals.get(var);
      SQLTable table = info.createNamedValueTable(var, expressions);
      // Produce values
      return new SQLColumns(table, finfo.getValueColumns());
    }
    // Unknown type
    else {
      throw new OntopiaRuntimeException("Variable '" + varname + "' of unknown type." + info.jdoquery.getVariableNames() + " " + info.jdoquery.getVariableType(varname));
    }
  }
  
  protected SQLValueIF produceParameter(JDOParameter par, List<SQLExpressionIF> expressions, BuildInfo info) {
    String parname = par.getName();
    
    // FIXME: Handle aggregate or alias type parameters properly.
    // FIXME: Handle primitive parameters properly
    
    // Identifiable type
    if (isIdentifiableParameter(parname, info.jdoquery)) {      
      // Get parameter table
      FieldInfoIF finfo = getFieldInfo(par, info); // Note: identity field
      SQLTable table = info.createNamedValueTable(par, expressions);      
      // Produce values
      return new SQLColumns(table, finfo.getValueColumns());
    }
    // Aggregate type or Primitive type
    else if (isAggregateParameter(parname, info.jdoquery) ||
             isPrimitiveParameter(parname, info.jdoquery)) {
      // Get parameter table
      FieldInfoIF finfo = info.nfvals.get(par);
      SQLTable table = info.createNamedValueTable(par, expressions);
      // Produce values
      return new SQLColumns(table, finfo.getValueColumns());
    }
    // Unknown type
    else {
      throw new OntopiaRuntimeException("Parameter '" + parname + "' of unknown type. " + info.jdoquery.getParameterNames() + " " + info.jdoquery.getParameterType(parname));
    }
  }

  protected SQLValueIF producePrimitive(JDOPrimitive primitive, BuildInfo info) {
    
    // Map primitive type correctly
    switch (primitive.getPrimitiveType()) {
    case JDOPrimitive.INTEGER:
      return new SQLPrimitive(primitive.getValue(), Types.INTEGER);
    case JDOPrimitive.LONG:
      return new SQLPrimitive(primitive.getValue(), Types.BIGINT);
    case JDOPrimitive.SHORT:
      return new SQLPrimitive(primitive.getValue(), Types.SMALLINT);
    case JDOPrimitive.FLOAT:
      return new SQLPrimitive(primitive.getValue(), Types.REAL);
    case JDOPrimitive.DOUBLE:
      return new SQLPrimitive(primitive.getValue(), Types.DOUBLE);
    case JDOPrimitive.BOOLEAN:
      return new SQLPrimitive(primitive.getValue(), Types.BIT);
    case JDOPrimitive.BYTE:
      return new SQLPrimitive(primitive.getValue(), Types.TINYINT);
    case JDOPrimitive.BIGDECIMAL:
      return new SQLPrimitive(primitive.getValue(), Types.DECIMAL);
    case JDOPrimitive.BIGINTEGER:
      return new SQLPrimitive(primitive.getValue(), Types.NUMERIC);
    default:
      throw new OntopiaRuntimeException("Unsupported primitive type: '" + primitive.getPrimitiveType() + "'");
    }
  }

  protected SQLValueIF produceNativeValue(JDONativeValue field, List<SQLExpressionIF> expressions, BuildInfo info) {
    // TODO: Add support for other things than column references
    JDOVariable var = field.getRoot();
    SQLColumns varcols = (SQLColumns)produceVariable(var, expressions, info);
    return new SQLColumns(varcols.getTable(), field.getArguments());
  }
  
  protected SQLValueIF produceFunction(JDOFunction func, List<SQLExpressionIF> expressions, BuildInfo info) {
    return new SQLFunction(func.getName(), produceValues(func.getArguments(), expressions, info));
  }

  protected SQLValueIF produceObject(JDOObject object, BuildInfo info) {
    // retrieve field values
    List<SQLValueIF> values = new ArrayList<SQLValueIF>();    
    FieldInfoIF id_finfo = getFieldInfo(object, info);
    id_finfo.retrieveSQLValues(object.getValue(), values);
    if (values.size() == 1) {
      return values.get(0);
    } else {
      return new SQLTuple(values);
    }
  }

  protected SQLValueIF produceCollection(JDOCollection coll, BuildInfo info) {
    // loop over collection elements and retrieve field values
    List<SQLValueIF> values = new ArrayList<SQLValueIF>();
    FieldInfoIF id_finfo = getFieldInfo(coll, info);
    Collection _coll = coll.getValue();
    Iterator iter = _coll.iterator();
    while (iter.hasNext()) {
      id_finfo.retrieveSQLValues(iter.next(), values);
    }
    if (values.size() == 1) {
      return values.get(0);
    } else {
      return new SQLTuple(values);
    }
    
  }
  
  // -----------------------------------------------------------------------------
  // Field declarations (FieldInfoIFs)
  // -----------------------------------------------------------------------------

  protected FieldInfoIF getFieldInfo(JDOValueIF jdovalue, BuildInfo info) {
    switch (jdovalue.getType()) {
    case JDOValueIF.FIELD:
      return getFieldInfo((JDOField)jdovalue, info);
    case JDOValueIF.VARIABLE:
      return getFieldInfo((JDOVariable)jdovalue, info);
    case JDOValueIF.PARAMETER:
      return getFieldInfo((JDOParameter)jdovalue, info);
    case JDOValueIF.OBJECT:
      return getFieldInfo((JDOObject)jdovalue, info);
    case JDOValueIF.COLLECTION:
      return getFieldInfo((JDOCollection)jdovalue, info);
    default:
      return null;
    }
  }
  
  protected FieldInfoIF getFieldInfo(JDOVariable var, BuildInfo info) {
    // TODO: Need to check if primitive or aggregate type
    //! if (info.nfvals.containsKey(var))
    //!   return (FieldInfoIF)info.nfvals.get(var);
    //! else {
    Class vtype = info.jdoquery.getVariableType(var.getName());
    if (mapping.isDeclared(vtype)) {
      ClassInfoIF cinfo = mapping.getClassInfo(vtype);
      return cinfo.getIdentityFieldInfo();
    } else {
      return null;
    }
    //! }
  }
  
  protected FieldInfoIF getFieldInfo(JDOParameter param, BuildInfo info) {
    Class ptype = info.jdoquery.getParameterType(param.getName());
    ClassInfoIF cinfo = mapping.getClassInfo(ptype);
    return cinfo.getIdentityFieldInfo();
  }
  
  protected FieldInfoIF getFieldInfo(JDOObject object, BuildInfo info) {
    Class otype = object.getValueType();
    ClassInfoIF cinfo = mapping.getClassInfo(otype);
    return cinfo.getIdentityFieldInfo();
  }
    
  protected FieldInfoIF getFieldInfo(JDOCollection coll, BuildInfo info) {
    Class eltype = coll.getElementType();
    if (isPrimitiveType(eltype)) {
      return null;
    }
    ClassInfoIF cinfo = mapping.getClassInfo(eltype);
    return cinfo.getIdentityFieldInfo();
  }

  protected FieldInfoIF getFieldInfo(JDOField field, BuildInfo info) {
    // Note: returns the field info of the outer most field, not the field info of the root    
    // Note: primitive and aggregate fields should not get this far

    // Get value type of field root
    Class ctype = getValueType(field.getRoot(), info);

    // Get the field info of the outermost field    
    String[] path = field.getPath();
    FieldInfoIF finfo = null;
    
    for (int i=0; i < path.length; i++) {
      // Make sure that field parent is of declared type
      if (mapping.isDeclared(ctype)) {
        ClassInfoIF cinfo = mapping.getClassInfo(ctype);
        finfo = cinfo.getFieldInfoByName(path[i]);
        if (finfo == null) {
          throw new OntopiaRuntimeException("Parent '" + ctype + "' do not have field called '" + path[i] + "'");
        }
        ctype = finfo.getValueClass();
      }
      else {
        throw new OntopiaRuntimeException("Parent of field  '" + path[i] + "' of undeclared type: '" + ctype + "'");
      }
    }
    return finfo;
  }

  // -----------------------------------------------------------------------------
  // JDOValueIF compatiblity
  // -----------------------------------------------------------------------------
  
  protected Class checkCompatibility(JDOValueIF value1, JDOValueIF value2, BuildInfo info) {
    // TODO: Make sure assignable types are considered compatible (use
    // interface declarations from object relational mapping)
    
    // Get types for both values
    Class type1 = getValueType(value1, info);    
    Class type2 = getValueType(value2, info);

    // FIXME: Complain if one is primitive and the other is null?
    
    // FIXME: If one is null they're compatible
    if (type1 == null) {
      return type2;
    }
    if (type2 == null) {
      return type1;
    }
    
    // Check that types are compatible
    //! if (!(type1.isAssignableFrom(type2) || type2.isAssignableFrom(type1)))
    if (!type1.equals(type2)) {
			// HACK: string/reader compatibility hack
			if (type1 == String.class && type2 == java.io.Reader.class) {
        return String.class;
      } else if (type1 == java.io.Reader.class && type2 == String.class) {
        return String.class;
      } else {
				throw new OntopiaRuntimeException("Values '" +
																					value1 + "' (" + type1 + ") and '" +
																					value2 + "' (" + type2 + ") are not compatible.");
      }
		}

    // Return the [first [matching]] value type
    return type1;
  }

  protected Class checkCompatibility(JDOValueIF value, Class type, BuildInfo info) {
    Class vtype = getValueType(value, info);
    if (!type.isAssignableFrom(vtype)) {
      throw new OntopiaRuntimeException("Value '" + value + "' (" + vtype +
                                        ") is not compatible with type " + type + ".");
    }
    return vtype;
  }
  
  // -----------------------------------------------------------------------------
  // Declared JDOValueIF class types
  // -----------------------------------------------------------------------------
  
  protected Class getValueType(JDOValueIF value, BuildInfo info) {
    // TODO: Figure out the type of the value.
    switch (value.getType()) {
    case JDOValueIF.FIELD:
      return getValueType((JDOField)value, info);
    case JDOValueIF.VARIABLE:
      return info.jdoquery.getVariableType(((JDOVariable)value).getName());
    case JDOValueIF.PARAMETER:
      return info.jdoquery.getParameterType(((JDOParameter)value).getName());
    case JDOValueIF.PRIMITIVE:
      return ((JDOPrimitive)value).getValueType();
    case JDOValueIF.OBJECT:
      return ((JDOObject)value).getValueType();
    case JDOValueIF.STRING:
      return String.class;
    case JDOValueIF.COLLECTION:
      return ((JDOCollection)value).getValueType();
    case JDOValueIF.NATIVE_VALUE:
      return ((JDONativeValue)value).getValueType();
    case JDOValueIF.NULL:
      return null;
    case JDOValueIF.FUNCTION:
      return ((JDOFunction)value).getValueType();
    default:
      throw new OntopiaRuntimeException("Invalid value: '" + value + "'");
    }
  }
  
  protected Class getValueType(JDOField field, BuildInfo info) {
    FieldInfoIF finfo = getFieldInfo(field, info);

    // Check to see if field exists
    if (finfo == null) {
      throw new OntopiaRuntimeException("Unknown field: '" + field + "'");
    }

    // Return the field value class
    if (finfo.isCollectionField()) {
      return Collection.class;
    } else {       
      Class klass = finfo.getValueClass();
			// HACK: string/reader compatibility hack
			return (klass == java.io.Reader.class ? String.class : klass);
		}
  }

  protected boolean isIdentifiableValueType(JDOValueIF jdovalue, BuildInfo info) {
    return (getIdentifiableValueType(jdovalue, info) != null);
  }
  
  protected Class getIdentifiableValueType(JDOValueIF jdovalue, BuildInfo info) {
    // Returns a Class that is identifiable. This is not neccessarily
    // the actual type of "jdovalue", but can be any of its parent
    // field infos if jdovalue is a field.
    Class ctype = null;
    switch (jdovalue.getType()) {
    case JDOValueIF.FIELD:
      return getIdentifiableValueType((JDOField)jdovalue, info);
    case JDOValueIF.VARIABLE:
      ctype = getValueType((JDOVariable)jdovalue, info);
      break;
    case JDOValueIF.PARAMETER:
      ctype = getValueType((JDOParameter)jdovalue, info);
      break;
    case JDOValueIF.OBJECT:
      ctype = getValueType((JDOObject)jdovalue, info);
      break;
    case JDOValueIF.COLLECTION:
      ctype = getValueType((JDOCollection)jdovalue, info);
      break;
    default:
      return null;
    }
    return (isIdentifiableType(ctype) ? ctype : null);
  }

  protected Class getIdentifiableValueType(JDOField field, BuildInfo info) {
    // Note: returns the field info of the outer most field, not the field info of the root    
    // Note: primitive and aggregate fields should not get this far

    // Get value type of field root
    Class ctype = getValueType(field.getRoot(), info);

    // Get the field info of the outermost field    
    String[] path = field.getPath();
    FieldInfoIF finfo = null;
    
    for (int i=0; i < path.length; i++) {
      // Make sure that field parent is of declared type
      if (mapping.isDeclared(ctype)) {
        ClassInfoIF cinfo = mapping.getClassInfo(ctype);
        finfo = cinfo.getFieldInfoByName(path[i]);
        if (finfo == null) {
          throw new OntopiaRuntimeException("Parent '" + ctype + "' do not have field called '" + path[i] + "'");
        }
        Class _ctype = finfo.getValueClass();
        // Stop when next path item is non-identifiable
        //! System.out.println("ID: " + path[i] + " " + isIdentifiableType(_ctype) + " " + _ctype);
        if (isIdentifiableType(_ctype)) {
          ctype = _ctype;
        } else {
          break;
        }
      }
      else {
        throw new OntopiaRuntimeException("Parent of field  '" + path[i] + "' of undeclared type: '" + ctype + "'");
      }
    }
    return (isIdentifiableType(ctype) ? ctype : null);
  }

  // -----------------------------------------------------------------------------
  // Identifiable, aggregate or primtive class types
  // -----------------------------------------------------------------------------

  // FIXME: Add isAliasType(Class) and rename BuildInfo.nfvals to alias_fields
  
  protected boolean isIdentifiableVariable(String var, JDOQuery jdoquery) {
    // Get variable class type and check if it's identifiable
    return isIdentifiableType(jdoquery.getVariableType(var));
  }
  
  protected boolean isIdentifiableParameter(String param, JDOQuery jdoquery) {
    // Get parameter class type and check if it's identifiable
    return isIdentifiableType(jdoquery.getParameterType(param));
  }
  
  protected boolean isIdentifiableType(Class type) {
    // Lookup variable type class info
    if (mapping.isDeclared(type)) {
      ClassInfoIF cinfo = mapping.getClassInfo(type);
      return cinfo.isIdentifiable();
    } else {
      return false;
    }        
  }
  
  protected boolean isAggregateVariable(String var, JDOQuery jdoquery) {
    // Get variable class type and check if it's aggregate
    return isAggregateType(jdoquery.getVariableType(var));
  }
  
  protected boolean isAggregateParameter(String param, JDOQuery jdoquery) {
    // Get parameter class type and check if it's aggregate
    return isAggregateType(jdoquery.getParameterType(param));
  }
    
  protected boolean isAggregateType(Class type) {
    // Lookup variable type class info
    if (mapping.isDeclared(type)) {
      ClassInfoIF cinfo = mapping.getClassInfo(type);
      //if (cinfo == null) return false;
      // Type is aggregate if class descriptor is aggregate
      return cinfo.isAggregate();
    } else {
      return false;
    }
  }
  
  protected boolean isPrimitiveVariable(String var, JDOQuery jdoquery) {
    // Get variable class type and check if it's primitive
    return isPrimitiveType(jdoquery.getVariableType(var));
  }
  
  protected boolean isPrimitiveParameter(String param, JDOQuery jdoquery) {
    // Get parameter class type and check if it's primitive
    return isPrimitiveType(jdoquery.getParameterType(param));
  }
  
  protected boolean isPrimitiveType(Class type) {
    // TODO: Fix this code!
    // Should probably delegate to JDOPrimitive or ObjectRelationalMappingIF?
    if (type == String.class ||
        type == java.io.Reader.class ||
        type == Integer.class ||
        type == Float.class ||
        type == Long.class) {
      return true;
    }
    return false;
  }
  
}
