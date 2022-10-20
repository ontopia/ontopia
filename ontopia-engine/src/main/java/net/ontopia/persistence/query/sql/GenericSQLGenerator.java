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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.persistence.proxy.DefaultFieldHandler;
import net.ontopia.persistence.proxy.FieldHandlerIF;
import net.ontopia.persistence.proxy.SQLTypes;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: Generic SQL statement generator.
 */

public class GenericSQLGenerator implements SQLGeneratorIF {
  protected static final String AND = " and ";

  // FIXME: May have to tweak these based on empirical values
  protected static final int INIT_WIDTH_SELECT = 64;
  protected static final int INIT_WIDTH_FROM = 64;
  protected static final int INIT_WIDTH_WHERE = 256;
  protected static final int INIT_WIDTH_GROUP_BY = 16;
  protected static final int INIT_WIDTH_ORDER_BY = 32;

  protected static final int INIT_WIDTH_SQL =
    INIT_WIDTH_SELECT +
    INIT_WIDTH_FROM +
    INIT_WIDTH_WHERE +
    INIT_WIDTH_GROUP_BY +
    INIT_WIDTH_ORDER_BY;

  protected int MAX_ELEMENTS_IN = 0;

  protected GenericSQLGenerator(Map properties) {
    if (properties != null) {
      this.MAX_ELEMENTS_IN = PropertyUtils.getInt((String)properties.get("net.ontopia.persistence.query.sql.InMaxElements"), MAX_ELEMENTS_IN);
    }
  }

  /**
   * INTERNAL: Class used to hold information collected after having
   * analyzed the SQL filter.
   */
  class BuildInfo {

    protected BuildInfo parent;
    // FIXME: Replace Collection instances with arrays.
    protected boolean is_setop_query = false;

    protected int tlevel = 1;
    protected Map tlevels = new HashMap();
    
    // selects
    protected List stypes = new ArrayList();
    protected List sfhandlers = new ArrayList();

    // parameters
    protected List pnames = new ArrayList();
    protected List ptypes = new ArrayList();
    protected List pfhandlers = new ArrayList();

    protected int where_offset;
    protected List poffsets = new ArrayList();

    // aggregate selects
    protected boolean hasaggs = false;
    protected List nonaggregate = new ArrayList();
    
    // Note that these iterators are intended to be reused
    protected ColumnValueIterator viter1 = new ColumnValueIterator();
    protected ColumnValueIterator viter2 = new ColumnValueIterator();

    // Flag used to indicate whether SQLColumns tables should be registered.
    protected boolean register_tables = false;
    
    // Tables referenced
    protected Set rtables = new HashSet();
    // Tables joined
    protected Set jtables = new HashSet();
    // Tables selected
    protected Set stables = new HashSet();

    // Table joins
    protected Set joins = new HashSet();    

    // FROM fragments (strings that later get joined)
    protected List fg_from = new ArrayList();

    BuildInfo() {
    }
    
    BuildInfo(BuildInfo parent) {
      this.parent = parent;
    }
    
    protected void addSelect(Class stype, FieldHandlerIF sfhandler) {
      // FIXME: 1. Result column(s) -> Object value
      // FIXME: 2. Object value -> Instance field value
      //!  if (!sfhandlers.contains(sfhandler)) { // this to avoid duplicate selects(!)
        sfhandlers.add(sfhandler);
        stypes.add(stype);
      //! } else new RuntimeException().printStackTrace();
    }

    protected void addNonAggregateSelect(StringBuilder select) {
      nonaggregate.add(select);
    }
    
    protected void addParameter(SQLParameter param, int offset) {
      // FIXME: Should perhaps keep track of SQLParameter instead each
      // of its components individually?
      pnames.add(param.getName());
      ptypes.add(param.getValueType());
      pfhandlers.add(param.getFieldHandler());
      poffsets.add(offset);
      // FIXME: If ptype is instance of Collection, we know it is of
      // variable length.
    }

    protected void embedInfoFrom(BuildInfo info, int sql_offset) {
      // copy parameter names
      pnames.addAll(info.pnames);
      // copy parameter types
      ptypes.addAll(info.ptypes);
      // copy parameter field handlers
      pfhandlers.addAll(info.pfhandlers);
      // adjust and copy parameter offsets
      if (poffsets.size() > 0) {
        Iterator iter = info.poffsets.iterator();
        while (iter.hasNext()) {
          Integer old_po = (Integer)iter.next();
          Integer new_po = old_po + sql_offset;
          poffsets.add(new_po);
        }
      }
    }

    protected Class[] getSelectTypes() {
      Class[] _types = new Class[stypes.size()];
      stypes.toArray(_types);
      return _types;
    }

    protected Class[] getParameterTypes() {
      Class[] _types = new Class[ptypes.size()];
      ptypes.toArray(_types);
      return _types;
    }
    
    protected FieldHandlerIF[] getSelectHandlers() {
      FieldHandlerIF[] _sfhandlers = new FieldHandlerIF[sfhandlers.size()];
      sfhandlers.toArray(_sfhandlers);
      return _sfhandlers;
    }

    protected String[] getParameterNames() {
      String[] _pnames = new String[pnames.size()];
      pnames.toArray(_pnames);
      return _pnames;
    }

    protected FieldHandlerIF[] getParameterHandlers() {
      FieldHandlerIF[] _pfhandlers = new FieldHandlerIF[pfhandlers.size()];
      pfhandlers.toArray(_pfhandlers);
      return _pfhandlers;
    }

    protected int[] getParameterOffsets() {
      int length = poffsets.size();
      int[] _poffsets = new int[length];
      for (int i=0; i < length; i++) {
        _poffsets[i] = ((Integer)poffsets.get(i)).intValue() + where_offset;
        //! System.out.println("P: " + _poffsets[i]);
      }
      return _poffsets;
    }

    protected int[] getCollectionIndexes() {
      int length = ptypes.size();
      int[] indexes = null;
      int coll_count = 0;
      for (int i=0; i < length; i++) {
        if (Collection.class.isAssignableFrom((Class)ptypes.get(i))) {
          if (indexes == null ||
              indexes.length <= coll_count) {
            indexes = new int[coll_count + 1];
          }
          indexes[coll_count] = i;
          coll_count++;
        }
      }
      return indexes;
    }
  }

  class ColumnValueIterator {
    
    protected SQLValueIF current;
    protected SQLValueIF[] list;

    protected int pindex;
    protected int cindex;
    
    private void resetValue(SQLValueIF value) {      
      pindex = 0;
      cindex = 0;
      
      if (value.getType() == SQLValueIF.TUPLE) {
        SQLTuple tuple = (SQLTuple)value;
        list = tuple.getValues();
        // If it's a nested tuple we need to flatten it.
        int varity = tuple.getValueArity();
        if (varity > list.length) {
          list = new SQLValueIF[varity];
          flattenSQLValueIF(tuple.getValues(), list, 0);
        }
        current = list[0];
      } else {
        list = null;
        current = value;
      }      

    }
    
    private void nextReference(StringBuilder sql, BuildInfo info) {
      // FIXME: why don't we just delegate to atomicSQLValueIF?

      switch (current.getType()) {
      case SQLValueIF.COLUMNS: {
        SQLColumns columns = (SQLColumns)current;
        SQLTable table = columns.getTable();
        
        // If table registration flag is set and this is the first column,
        // register table with build info
        if (cindex == 0 && info.register_tables) {
          info.rtables.add(table);
        }        
        referenceSQLColumnsColumn(table, columns.getColumns()[cindex], sql, info);        
        break;
      }
      case SQLValueIF.PARAMETER: {
        // FIXME: Make sure that this method is not called outside the
        // where clause and only once per parameter - and in the right
        // order.
    
        // Register parameter type+info
        SQLParameter param = (SQLParameter)current;
        //! System.out.println("===1> Parameter " + param.getName() + " (index: " + sql.length() + ")");
        if (cindex == 0) {
          info.addParameter(param, sql.length());
        }
        sql.append('?');
        break;
      }
      case SQLValueIF.PRIMITIVE: {
        // Note: arity is always 1
        referenceSQLPrimitive((SQLPrimitive)current, sql, info);
        break;
      }
      case SQLValueIF.NULL:
        // Note: arity is always 1
        sql.append("null");
        break;
      case SQLValueIF.VERBATIM:
        sql.append(((SQLVerbatim)current).getValue());
        break;
      case SQLValueIF.FUNCTION:
        referenceSQLFunction((SQLFunction)current, sql, info);
        return;
      default:
        throw new OntopiaRuntimeException("Unsupported SELECT SQLValueIF: '" +
                                          current + "' type: " + current.getType());
      }
      // Skip to next value
      cindex++;
      if (cindex >= current.getArity()) {
        if (list != null) {
          pindex++;
          if (pindex < list.length) {
            current = list[pindex];
            cindex = 0;
          } else {
            // we're done
          }
        } else {
          // we're done
        }          
      }
    }
  }
  
  @Override
  public SQLStatementIF createSQLStatement(SQLQuery query) {

    // Create SQL query from query components
    BuildInfo info = new BuildInfo();
    String sql = createStatement(query, info);

    //! // Debug: output parameter offsets
    //! System.out.println("===1> Parameters: " + info.poffsets);
    //! int[] offs = info.getParameterOffsets();
    //! List offs_ = new ArrayList();
    //! for (int i=0; i < offs.length; i++)
    //!   offs_.add(new Integer(offs[i]));
    //! System.out.println("===x> Parameters: " + offs_);

    // Wrap it all in a SQL statement object
    ParameterProcessorIF proc = null;
    int[] coll_indexes = info.getCollectionIndexes();
    if (coll_indexes != null) {
      proc = new CollectionParameterProcessor(info.getParameterHandlers(), info.getParameterNames(),
              coll_indexes, info.getParameterOffsets());
    } else {
      proc = new DefaultParameterProcessor(info.getParameterHandlers(), info.getParameterNames());
    }
    
    return new SQLStatement(sql, info.getSelectHandlers(), proc);
  }

  protected String createStatement(SQLQuery query, BuildInfo info) {
    // Analyze query by tracking the topmost level at which a table is
    // referenced. This information is later being used to figure out
    // in which from clause the table should be referenced.
    analyzeQuery(query, info.tlevels, 1);
    //! System.out.println("MAP: " + info.tlevels);
    
    return createStatement(query.getFilter(), query.getSelect(),
                           query.getDistinct(), query.getOffset(), query.getLimit(),
                           query.getOrderBy(), query.isSetQuery(), info);
  }
  
  protected String createStatement(SQLExpressionIF filter, List selects, boolean distinct, int offset, int limit, 
                                   List orderby, boolean issetquery, BuildInfo info) {

    // Analyze query
    
    // WHERE and SELECT clauses must be created first.
    StringBuilder sql_select = createSelectClause(selects, distinct, info);
    if (issetquery) {
      info.rtables.clear();
    }
    StringBuilder sql_where = createWhereClause(filter, info);
    StringBuilder sql_group_by = createGroupByClause(info);
    StringBuilder sql_order_by = createOrderByClause(orderby, info);
    // FROM clause must be created at last.
    StringBuilder sql_from;
    if (issetquery) {
      sql_from = sql_where;
      sql_from.insert(0, '(');
      sql_from.append(')');
      fromSubSelectAlias(sql_from, info);
      sql_where = null;
    } else {
      sql_from = createFromClause(filter, info);
    }

    StringBuilder sql_offset_limit = createOffsetLimitClause(offset, limit, info);

    // Construct the full statement.
    return createStatement(sql_select, sql_where, sql_from, sql_group_by, sql_order_by, sql_offset_limit, info);
  }
  
  protected StringBuilder createSelectClause(List selects, boolean distinct, BuildInfo info) {
    // SELECT clause
    info.register_tables = true;
    StringBuilder sql_select = new StringBuilder(INIT_WIDTH_SELECT);
    produceSelect(selects, distinct, sql_select, info);
    info.register_tables = false;
    return sql_select;
  }

  protected StringBuilder createWhereClause(SQLExpressionIF filter, BuildInfo info) {
    // WHERE clause
    info.register_tables = true;
    StringBuilder sql_where = new StringBuilder(INIT_WIDTH_WHERE);    
    produceWhere(filter, sql_where, info);
    info.register_tables = false;
    return sql_where;
  }

  protected StringBuilder createFromClause(SQLExpressionIF filter, BuildInfo info) {
    // FROM clause
    StringBuilder sql_from = new StringBuilder(INIT_WIDTH_FROM);
    produceFrom(sql_from, info);
    return sql_from;
  }

  protected StringBuilder createGroupByClause(BuildInfo info) {
    // GROUP BY clause
    StringBuilder sql_group_by = new StringBuilder(INIT_WIDTH_GROUP_BY);    
    produceGroupBy(sql_group_by, info);
    return sql_group_by;
  }

  protected StringBuilder createOrderByClause(List orderby, BuildInfo info) {
    // ORDER BY clause
    info.register_tables = true;
    StringBuilder sql_order_by = new StringBuilder(INIT_WIDTH_ORDER_BY);
    produceOrderBy(orderby, sql_order_by, info);
    info.register_tables = false;
    return sql_order_by;
  }

  protected StringBuilder createOffsetLimitClause(int offset, int limit, BuildInfo info) {    
    // ISSUE: does not work with Oracle
    
    // LIMIT x OFFSET y clause
    if (limit > 0 && offset > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append(" limit ").append(limit).append(" offset ").append(offset);
      return sb;
    } else if (limit > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append(" limit ").append(limit);
      return sb;
    } else if (offset > 0) {
      // ISSUE: does not work with MySQL
      StringBuilder sb = new StringBuilder();
      sb.append(" offset ").append(offset);
      return sb;
    } else {
      return null;
    }
  }

  protected String createStatement(StringBuilder sql_select, StringBuilder sql_where,
                                   StringBuilder sql_from, StringBuilder sql_group_by,
                                   StringBuilder sql_order_by, StringBuilder sql_offset_limit, BuildInfo info) {
    
    // FIXME: Return StringBuilder instead?
    // NOTE: WHERE clause and FROM clause has already been created at this point.

    // Append clauses
    StringBuilder sql = new StringBuilder(INIT_WIDTH_SQL);

    // SELECT clause
    sql.append("select ").append(sql_select)

    // FROM clause
    .append(" from ").append(sql_from);
    
    // WHERE clause
    if (sql_where != null && sql_where.length() != 0) {
      sql.append(" where ");
      info.where_offset = sql.length(); // set where-offset member
      sql.append(sql_where);
    }

    // GROUP BY clause
    if (sql_group_by != null && sql_group_by.length() != 0) {
      sql.append(" group by ").append(sql_group_by);
    }
    
    // ORDER BY clause
    if (sql_order_by != null && sql_order_by.length() != 0) {
      sql.append(" order by ").append(sql_order_by);
    }

    // LIMIT x OFFSET y clause
    if (sql_offset_limit != null && sql_offset_limit.length() != 0) {
      sql.append(sql_offset_limit);
    }
    
    // Create statement object
    return sql.toString();
  }

  // -----------------------------------------------------------------------------
  // Pre-build SQL query analysis
  // -----------------------------------------------------------------------------

  protected void analyzeQuery(SQLQuery sqlquery, Map tlevels, Integer level) {
    analyzeSelect(sqlquery.getSelect(), tlevels, level);
    analyzeOrderBy(sqlquery.getOrderBy(), tlevels, level);
    analyzeExpression(sqlquery.getFilter(), tlevels, level);
  }

  protected void analyzeSelect(List selects, Map tlevels, Integer level) {
    Iterator iter = selects.iterator();
    while (iter.hasNext()) {
      Object selected = iter.next();
      if (selected instanceof SQLAggregateIF) {
        analyzeValue(((SQLAggregateIF)selected).getValue(), tlevels, level);
      } else {
        analyzeValue((SQLValueIF)selected, tlevels, level);
      }
    }
  }

  protected void analyzeOrderBy(List orderby, Map tlevels, Integer level) {
    Iterator iter = orderby.iterator();
    while (iter.hasNext()) {
      SQLOrderBy order = (SQLOrderBy)iter.next();
      
      if (order.isAggregate()) {
        analyzeValue(order.getAggregate().getValue(), tlevels, level);
      } else {
        analyzeValue(order.getValue(), tlevels, level);
      }
    }
  }

  protected void analyzeExpression(SQLExpressionIF expr, Map tlevels, Integer level) {
    // Skip if expression is null
    if (expr == null) {
      return;
    }
    // Check expression type
    switch (expr.getType()) {
      
      // LOGICAL EXPRESSIONS
    case SQLExpressionIF.AND: {
      analyzeExpressions(((SQLAnd)expr).getExpressions(), tlevels, level);
      return;
    }
    case SQLExpressionIF.OR: {
      analyzeExpressions(((SQLOr)expr).getExpressions(), tlevels, level);
      return;
    }
    case SQLExpressionIF.NOT: {
      analyzeExpression(((SQLNot)expr).getExpression(), tlevels, level);
      return;
    }
      // SIMPLE EXPRESSIONS
    case SQLExpressionIF.FALSE: {
      // Nothing to analyze
      return;
    }
    case SQLExpressionIF.EQUALS: {
      SQLEquals exp = (SQLEquals)expr;
      analyzeValue(exp.getLeft(), tlevels, level);
      analyzeValue(exp.getRight(), tlevels, level);
      return;
    }
    case SQLExpressionIF.NOT_EQUALS: {
      SQLNotEquals exp = (SQLNotEquals)expr;
      analyzeValue(exp.getLeft(), tlevels, level);
      analyzeValue(exp.getRight(), tlevels, level);
      return;
    }
    case SQLExpressionIF.IS_NULL: {
      analyzeValue(((SQLIsNull)expr).getValue(), tlevels, level);
      return;
    }
    case SQLExpressionIF.LIKE: {
      SQLLike exp = (SQLLike)expr;
      analyzeValue(exp.getLeft(), tlevels, level);
      analyzeValue(exp.getRight(), tlevels, level);
      return;
    }
    case SQLExpressionIF.VERBATIM: {
      return;
    }
    case SQLExpressionIF.VALUE_EXPRESSION:
      analyzeValue(((SQLValueExpression)expr).getValue(), tlevels, level);
      return;
    case SQLExpressionIF.EXISTS:
      analyzeExpression(((SQLExists)expr).getExpression(), tlevels, level + 1);
      return;
    case SQLExpressionIF.IN: {
      SQLIn exp = (SQLIn)expr;
      analyzeValue(exp.getLeft(), tlevels, level);
      analyzeValue(exp.getRight(), tlevels, level);
      return;
    }
    case SQLExpressionIF.JOIN: {
      SQLJoin exp = (SQLJoin)expr;
      analyzeValue(exp.getLeft(), tlevels, level);
      analyzeValue(exp.getRight(), tlevels, level);
      return;
    }
      // SET OPERATIONS
    case SQLExpressionIF.SET_OPERATION: {
      //! whereSQLSetOperation((SQLSetOperation)expr, sql, info);
      return;
    }
    default:
      throw new OntopiaRuntimeException("Unsupported WHERE SQLExpressionIF: '" + expr + "' (type: + " + expr.getType() + ")");
    }
  }
  
  protected void analyzeExpressions(SQLExpressionIF[] exprs, Map tlevels, Integer level) {
    // Loop over SQL expressions and analyze them individually
    for (int i=0; i < exprs.length; i++) {
      analyzeExpression(exprs[i], tlevels, level);
    }
  }

  protected void analyzeValue(SQLValueIF value, Map tlevels, Integer level) {
    switch (value.getType()) {
    case SQLValueIF.COLUMNS: {
      //! System.out.println("=> " + level + ":" + value);
      SQLTable tbl = ((SQLColumns)value).getTable();
      
      Integer _level = (Integer)tlevels.get(tbl);
      if (_level == null || _level.intValue() > level.intValue()) {
        tlevels.put(tbl, level);
      }
      
      return;        
    }
    case SQLValueIF.TUPLE: {
      SQLValueIF[] values = ((SQLTuple)value).getValues();
      for (int i=0; i < values.length; i++) {
        analyzeValue(values[i], tlevels, level);
      }
      return;
    }
    case SQLValueIF.FUNCTION: {
      SQLFunction func = (SQLFunction)value;
      SQLValueIF[] args = func.getArguments();
      for (int i=0; i < args.length; i++) {
        analyzeValue(args[i], tlevels, level);
      }
      return;
    }
    }
  }
  
  // -----------------------------------------------------------------------------
  // SELECT clause
  // -----------------------------------------------------------------------------

  protected void produceSelect(List selects, boolean distinct, StringBuilder sql, BuildInfo info) {
    // Output distinct if specified
    if (distinct) {
      sql.append("distinct ");
    }

    // FIXME: What if select list is empty? Use '*'?

    // Make sure selected fields are registered (field handlers and aggregate functions)
    boolean register = true;
    
    // Loop over selected values and produce fragment
    if (selects.isEmpty()) {
      // 'select *'
      sql.append('*');
    } else {
      Iterator iter = selects.iterator();
      while (iter.hasNext()) {
        Object selected = iter.next();
        if (selected instanceof SQLAggregateIF) {
          // aggregate
          selectSQLAggregateIF((SQLAggregateIF)selected, register, sql, info);
        } else {
          // selected value
          selectSQLValueIF((SQLValueIF)selected, register, sql, info);
        }
        
        if (iter.hasNext()) {
          sql.append(", ");
        }
      }
    }
  }

  // -----------------------------------------------------------------------------
  // GROUP BY clause
  // -----------------------------------------------------------------------------

  protected void produceGroupBy(StringBuilder sql, BuildInfo info) {

    if (!info.hasaggs) {
      return;
    }

    // Ignore if no aggregate values are selected
    List nonagg = info.nonaggregate;    
    if (nonagg.isEmpty()) {
      return;
    }
    
    // Loop over non aggregate columns
    Iterator iter = nonagg.iterator();
    while (iter.hasNext()) {
      sql.append(iter.next());
      if (iter.hasNext()) {
        sql.append(", ");
      }
    }
  }
  
  // -----------------------------------------------------------------------------
  // ORDER BY clause
  // -----------------------------------------------------------------------------

  protected void produceOrderBy(List orderby, StringBuilder sql, BuildInfo info) {
    if (orderby == null || orderby.isEmpty()) {
      return;
    }
    // Do not register field handlers and aggregate functions in order by clause
    boolean register = false;

    // Loop over order by columns
    Iterator iter = orderby.iterator();
    while (iter.hasNext()) {
      SQLOrderBy ob = (SQLOrderBy)iter.next();
      // Same output as SELECT fragments, so we delegate to the select method.
      if (ob.isAggregate()) {
        selectSQLAggregateIF(ob.getAggregate(), register, sql, info);
      } else {
        selectSQLValueIF(ob.getValue(), register, sql, info);
      }

      if (ob.getOrder() == SQLOrderBy.ASCENDING) {        
        sql.append(" ASC");
      } else {
        sql.append(" DESC");
      }

      if (iter.hasNext()) {
        sql.append(", ");
      }
    }
  }
  
  // -----------------------------------------------------------------------------
  // FROM clause
  // -----------------------------------------------------------------------------

  protected boolean isFromLevel(SQLTable tbl, BuildInfo info) {
    Integer tlevel = (Integer)info.tlevels.get(tbl);
    //! System.out.println("TBL: " + tbl + ":" + info.tlevel + " " + (tlevel.intValue() == info.tlevel));
    if (tlevel.intValue() == info.tlevel) {
      return true;
    } else {
      return false;
    }
  }
  
  protected void produceFrom(StringBuilder sql, BuildInfo info) {
    // Loop over referenced tables and FROM those that aren't joined.
    Iterator iter = info.rtables.iterator();
    while (iter.hasNext()) {
      // FROM referenced table unless joined
      SQLTable rtable = (SQLTable)iter.next();
      if (!info.jtables.contains(rtable)) {
        // Ignore if parent statement references table
        if (info.parent == null || isFromLevel(rtable, info)) {
          // FROM referenced table
          fromSQLTable(rtable, sql, info);
        }
      }
    }

    // Register table joins with FROM clause (e.g. Oracle 8i)
    iter = info.joins.iterator();
    while (iter.hasNext()) {
      // Register join with FROM clause
      SQLJoin join = (SQLJoin)iter.next();

      // Join directly if no parent statement
      if (info.parent == null) {
        fromSQLJoin(join, sql, info);
      }
      // Otherwise filter out tables referenced from parent.
      else {
        SQLTable ltable = join.getLeft().getTable();
        SQLTable rtable = join.getRight().getTable();
        boolean llevel = isFromLevel(ltable, info);
        boolean rlevel = isFromLevel(rtable, info);

        if (llevel) {
          if (rlevel) {
            // Join if both tables are not referenced by the parent.
            fromSQLJoin(join, sql, info);
          } else {
            // FROM left table
            fromSQLTable(ltable, sql, info);
          }
        } else {          
          if (rlevel) {
            // FROM right table
            fromSQLTable(rtable, sql, info);
          } else {
            // Do nothing since both tables are referenced by parent.
          }
        }
      }
    }
    
    // Join tables using a comma separator
    sql.append(StringUtils.join(info.fg_from, ", "));
  }
  
  //! protected void produceFrom(StringBuilder sql, BuildInfo info) {
  //!   Set ptables = null;
  //!   if (info.parent != null) {
  //!     ptables = new HashSet();
  //!     accumulateParentTables(info.parent, ptables);
  //!   }
  //!   
  //!   // Loop over referenced tables and FROM those that aren't joined.
  //!   Iterator iter = info.rtables.iterator();
  //!   while (iter.hasNext()) {
  //!     // FROM referenced table unless joined
  //!     SQLTable rtable = (SQLTable)iter.next();
  //!     if (!info.jtables.contains(rtable)) {
  //!       // Ignore if parent statement references table
  //!       if (info.parent == null || !ptables.contains(rtable)) {
  //!         // FROM referenced table
  //!         fromSQLTable(rtable, sql, info);
  //!       }
  //!     }
  //!   }
  //! 
  //!   // Register table joins with FROM clause (e.g. Oracle 8i)
  //!   iter = info.joins.iterator();
  //!   while (iter.hasNext()) {
  //!     // Register join with FROM clause
  //!     SQLJoin join = (SQLJoin)iter.next();
  //! 
  //!     // Join directly if no parent statement
  //!     if (info.parent == null) {
  //!       fromSQLJoin(join, sql, info);
  //!     }
  //!     // Otherwise filter out tables referenced from parent.
  //!     else {
  //!       SQLTable ltable = join.getLeft().getTable();
  //!       SQLTable rtable = join.getRight().getTable();
  //!       boolean lparent = ptables.contains(ltable);
  //!       boolean rparent = ptables.contains(rtable);
  //! 
  //!       if (lparent) {
  //!         if (rparent) {
  //!           // Do nothing since both tables are referenced by parent.
  //!         } else {
  //!           // FROM right table
  //!           fromSQLTable(rtable, sql, info);
  //!         }
  //!       } else {          
  //!         if (rparent) {
  //!           // FROM left table
  //!           fromSQLTable(ltable, sql, info);
  //!         } else {
  //!           // Join if both tables are not referenced by the parent.
  //!           fromSQLJoin(join, sql, info);
  //!         }
  //!       }
  //!     }
  //!   }
  //!   
  //!   // Join tables using a comma separator
  //!   StringUtils.join(info.fg_from, ", ", sql);
  //! }
  //! 
  //! protected void accumulateParentTables(BuildInfo info, Set tables) {
  //!   tables.addAll(info.rtables);
  //!   tables.addAll(info.jtables);
  //!   if (info.parent != null)
  //!     accumulateParentTables(info.parent, tables);
  //! }

  protected void fromSubSelectAlias(StringBuilder sql, BuildInfo info) {
    // No need for an alias.
  }
  
  // -----------------------------------------------------------------------------
  // WHERE clause
  // -----------------------------------------------------------------------------

  protected void produceWhere(SQLExpressionIF filter, StringBuilder sql, BuildInfo info) {
    whereSQLExpressionIF(filter, sql, info);

    //! System.out.println("SQL: " + expression);
    //! System.out.println("RTABLES: " + StringUtils.join(info.rtables, ", "));
    //! System.out.println("JTABLES: " + StringUtils.join(info.jtables, ", "));
    //! System.out.println("JOINS: " + info.joins);
  }
  
  // -----------------------------------------------------------------------------
  // SELECT - fragments
  // -----------------------------------------------------------------------------

  protected void selectSQLAggregateIF(SQLAggregateIF aggregate, boolean register, StringBuilder sql, BuildInfo info) {
    switch (aggregate.getType()) {      
    case SQLAggregateIF.COUNT:
      // Note: aggregate value field will not be registered, but
      // instead a field of the aggregate result type is used.
      if (register) {
        // Register select type+info
        info.addSelect(Integer.class, new DefaultFieldHandler(SQLTypes.getType(Integer.class)));
        info.hasaggs = true;
      }
      sql.append("count(");
      if (aggregate.isReference()) {
        sql.append(aggregate.getReference().getValue().getAlias());
    } else {
        selectSQLValueIF(aggregate.getValue(), false, sql, info); // Note: register is false
    }
      sql.append(')');
      if (register) {
        selectColumnAlias(aggregate, sql);
    }
      return;
    default:
      throw new OntopiaRuntimeException("Invalid aggregate function: '" + aggregate + "'");
    }    
  }

  protected void selectColumnAlias(SQLAggregateIF aggregate, StringBuilder sql) {
    if (aggregate == null) {
      return;
    }
    String alias = aggregate.getAlias();
    if (alias != null) {
      sql.append(" as ");
      sql.append(alias);
    }
  }

  protected void selectColumnAlias(SQLValueIF value, StringBuilder sql) {
    if (value == null) {
      return;
    }
    String alias = value.getAlias();
    if (alias != null) {
      sql.append(" as ");
      sql.append(alias);
    }
  }
  
  protected void selectSQLValueIF(SQLValueIF value, boolean register, StringBuilder sql, BuildInfo info) {
    SQLValueIF sqlvalue;
    SQLValueIF refvalue;
    if (value.isReference()) {
      sqlvalue = value.getReference();
      refvalue = value;
    } else {
      sqlvalue = value;
      refvalue = null;
    }        
      
    // FIXME: Only columns can be selected at this time.
    switch (sqlvalue.getType()) {
    case SQLValueIF.COLUMNS:
      if (register) {
        StringBuilder sb = new StringBuilder();
        selectSQLColumns((SQLColumns)sqlvalue, refvalue, register, sb, info, false);
        // WARNING: last boolean used to prevent [column] aliases to be included in group by clause
        StringBuilder sb_groupby = new StringBuilder();
        selectSQLColumns((SQLColumns)sqlvalue, refvalue, false, sb_groupby, info, true);
        info.addNonAggregateSelect(sb_groupby);
        // ISSUE: the next line is problematic if compiled with java 1.4
        sql.append(sb);
      } else {
        selectSQLColumns((SQLColumns)sqlvalue, refvalue, register, sql, info, false);        
      }
      return;
    case SQLValueIF.PRIMITIVE:
      selectSQLPrimitive((SQLPrimitive)sqlvalue, refvalue, register, sql, info);
      return;
      //! // FIXME: Selects of parameters not currently allowed.
      //! case SQLValueIF.PARAMETER:
      //!   return selectSQLParameter((SQLParameter)sqlvalue, info);
    case SQLValueIF.NULL:
      selectSQLNull((SQLNull)sqlvalue, refvalue, register, sql, info);
      return;
    case SQLValueIF.VERBATIM:
      if (register) {
        StringBuilder sb = new StringBuilder();
        selectSQLVerbatim((SQLVerbatim)sqlvalue, refvalue, register, sql, info);
        info.addNonAggregateSelect(sb);
        sql.append(sb);
      } else {
        selectSQLVerbatim((SQLVerbatim)sqlvalue, refvalue, register, sql, info);
      }
      return;
    case SQLValueIF.FUNCTION:
      if (register) {
        StringBuilder sb = new StringBuilder();
        selectSQLFunction((SQLFunction)sqlvalue, refvalue, register, sql, info);
        info.addNonAggregateSelect(sb);
        sql.append(sb);
      } else {
        selectSQLFunction((SQLFunction)sqlvalue, refvalue, register, sql, info);
      }
      return;
    default:
      throw new OntopiaRuntimeException("Unsupported SELECT SQLValueIF: '" + sqlvalue + "'");
    }
  }
  
  protected void selectSQLColumns(SQLColumns columns, SQLValueIF refvalue, boolean register,
                                  StringBuilder sql, BuildInfo info, boolean nonagg) {

    // Register select type+info
    if (register) {
      // NOTE:Value type and field handler defaults to java.lang.String type                              
      info.addSelect(columns.getValueType(), columns.getFieldHandler());
    }

    // Extract columns info
    SQLTable table = columns.getTable();
    String[] cols = columns.getColumns();
        
    // If table registration flag is set, register table with build info
    if (info.register_tables) {
      info.rtables.add(table);
    }            

    // Loop over columns and insert references
    if (refvalue == null) {
      for (int i=0; i < cols.length; i++) {
        if (i > 0) {
          sql.append(", ");
        }      
        referenceSQLColumnsColumn(table, cols[i], sql, info);
        // TODO: support for " as <calias>"
        if (register && !nonagg) {
          selectColumnAlias(columns, sql);
        }
      }
    } else {
      for (int i=0; i < cols.length; i++) {
        if (i > 0) {
          sql.append(", ");
        }      
        sql.append(columns.getAlias());
        // TODO: Add suffix if width > 1.
        //! if (i > 0) sql.append(i);
        // TODO: support for " as <calias>"
        if (register) {
          selectColumnAlias(refvalue, sql);
        }
      }
    }
  }
  
  protected void selectSQLPrimitive(SQLPrimitive value, SQLValueIF refvalue, 
                                    boolean register, StringBuilder sql, BuildInfo info) {
    // Register select type+info
    if (register) {
      info.addSelect(value.getValueType(), value.getFieldHandler());
    }
    
    if (refvalue == null) {
      referenceSQLPrimitive(value, sql, info);
      if (register) {
        selectColumnAlias(value, sql);
      }
    } else {
      // NOTE: This may not really be neccesary for NULLs      
      sql.append(value.getAlias()); 
      if (register) {
        selectColumnAlias(refvalue, sql);
      }
    }
  }
  
  //! protected String selectSQLParameter(SQLParameter parameter, boolean register, BuildInfo info) {
  //!   return StringUtils.join(referenceSQLParameter(parameter, info), ", ");
  //! }
  
  protected void selectSQLNull(SQLNull value, SQLValueIF refvalue, 
                               boolean register, StringBuilder sql, BuildInfo info) {
    // Register select type+info
    if (register) {
      info.addSelect(value.getValueType(), value.getFieldHandler());
    }
    
    if (refvalue == null) {
      sql.append("null");
      if (register) {
        selectColumnAlias(value, sql);
      }
    } else {
      // NOTE: This may not really be neccesary for NULLs      
      sql.append(value.getAlias()); 
      if (register) {
        selectColumnAlias(refvalue, sql);
      }
    }
  }
  
  protected void selectSQLVerbatim(SQLVerbatim value, SQLValueIF refvalue, 
                                   boolean register, StringBuilder sql, BuildInfo info) {
    // Register select type+info
    if (register) {
      info.addSelect(value.getValueType(), value.getFieldHandler());
    }
    
    if (refvalue == null) {
      // Output verbatime value directly
      sql.append(value.getValue());
      if (register) {
        selectColumnAlias(value, sql);
      }
    } else {
      sql.append(value.getAlias()); 
      if (register) {
        selectColumnAlias(refvalue, sql);
      }
    }
  }
  
  protected void selectSQLFunction(SQLFunction value, SQLValueIF refvalue, 
                                   boolean register, StringBuilder sql, BuildInfo info) {
    // Register select type+info
    if (register) {
      info.addSelect(value.getValueType(), value.getFieldHandler());
    }
    
    if (refvalue == null) {
      // Output verbatime value directly
      referenceSQLFunction(value, sql, info);
      if (register) {
        selectColumnAlias(value, sql);
      }
    } else {
      sql.append(value.getAlias()); 
      if (register) {
        selectColumnAlias(refvalue, sql);
      }
    }
  }
  
  // -----------------------------------------------------------------------------
  // FROM - fragments
  // -----------------------------------------------------------------------------
  
  protected void fromSQLTable(SQLTable table, StringBuilder sql, BuildInfo info) {
    // Ignore if already FROMed
    if (!info.stables.contains(table)) {
      // Register FROM fragment
      StringBuilder sb = new StringBuilder();
      referenceSQLTableAndAlias(table, sb, info);
      info.fg_from.add(sb);
      // Register right table as selected
      info.stables.add(table);
    }
  }
  
  protected void fromSQLJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    // Check join type
    switch (join.getJoinType()) {
    case SQLJoin.CROSS:
      // Register cross join with FROM clause
      fromSQLCrossJoin(join, sql, info);
      break;
    case SQLJoin.LEFT_OUTER:
      // Register left outer join with FROM clause
      fromSQLLeftOuterJoin(join, sql, info);
      break;
    case SQLJoin.RIGHT_OUTER:
      // Register left outer join with FROM clause
      fromSQLRightOuterJoin(join, sql, info);
      break;
    default:
      throw new OntopiaRuntimeException("Unsupported FROM SQLJoin join type: '" + join.getJoinType() + "'");      
    }
    
  }
  
  protected void fromSQLCrossJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    // FROM left table
    fromSQLTable(join.getLeft().getTable(), sql, info);
    // FROM right table
    fromSQLTable(join.getRight().getTable(), sql, info);
  }
  
  protected void fromSQLLeftOuterJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    fromSQLJoin_GENERIC(join, " LEFT OUTER JOIN ", sql, info);    
  }
  
  protected void fromSQLRightOuterJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    fromSQLJoin_GENERIC(join, " RIGHT OUTER JOIN ", sql, info);    
  }
  
  protected void fromSQLJoin_GENERIC(SQLJoin join, String jointype, StringBuilder sql, BuildInfo info) {
    SQLColumns lcols = join.getLeft();
    SQLColumns rcols = join.getRight();
        
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    // FROM left table
    referenceSQLTableAndAlias(lcols.getTable(), sb, info);
    // join type, e.g. " LEFT OUTER JOIN "
    sb.append(jointype);
    // FROM right table
    referenceSQLTableAndAlias(rcols.getTable(), sb, info);
    // ON expression
    sb.append(" on ");
    whereSQLCrossJoin_GENERIC(join, sb, info);
    sb.append(')');

    // Register FROM fragment
    info.fg_from.add(sb);
    // Register tables as selected
    info.stables.add(lcols.getTable());
    info.stables.add(rcols.getTable());
  }
  
  // -----------------------------------------------------------------------------
  // SQLExpressionIF
  // -----------------------------------------------------------------------------
  
  protected void whereSQLExpressionIF(SQLExpressionIF expr, StringBuilder sql, BuildInfo info) {
    // Skip if expression is null
    if (expr == null) {
      return;
    }
    // Check expression type
    switch (expr.getType()) {

      // LOGICAL EXPRESSIONS
    case SQLExpressionIF.AND:
      whereSQLAnd((SQLAnd)expr, sql, info);
      return;
    case SQLExpressionIF.OR:
      whereSQLOr((SQLOr)expr, sql, info);
      return;
    case SQLExpressionIF.NOT:
      whereSQLNot((SQLNot)expr, sql, info);
      return;
      // SIMPLE EXPRESSIONS
    case SQLExpressionIF.FALSE: {
      whereSQLFalse((SQLFalse)expr, sql, info);
      return;
    }
    case SQLExpressionIF.EQUALS:
      whereSQLEquals((SQLEquals)expr, sql, info);
      return;
    case SQLExpressionIF.NOT_EQUALS:
      whereSQLNotEquals((SQLNotEquals)expr, sql, info);
      return;
    case SQLExpressionIF.IS_NULL:
      whereSQLIsNull((SQLIsNull)expr, sql, info);
      return;
    case SQLExpressionIF.LIKE:
      whereSQLLike((SQLLike)expr, sql, info);
      return;
    case SQLExpressionIF.VERBATIM:
      whereSQLVerbatimExpression((SQLVerbatimExpression)expr, sql, info);
      return;
    case SQLExpressionIF.VALUE_EXPRESSION:
      whereSQLValueExpression((SQLValueExpression)expr, sql, info);
      return;
    case SQLExpressionIF.EXISTS:
      whereSQLExists((SQLExists)expr, sql, info);
      return;
    case SQLExpressionIF.IN:
      whereSQLIn((SQLIn)expr, sql, info);
      return;
    case SQLExpressionIF.JOIN:
      whereSQLJoin((SQLJoin)expr, sql, info);
      return;
      // SET OPERATIONS
    case SQLExpressionIF.SET_OPERATION:
      whereSQLSetOperation((SQLSetOperation)expr, sql, info);
      return;
    default:
      throw new OntopiaRuntimeException("Unsupported WHERE SQLExpressionIF: '" + expr + "' (type: + " + expr.getType() + ")");
    }
  }
  
  protected void whereSQLExpressionIF(SQLExpressionIF[] nexprs, String separator, StringBuilder sql, BuildInfo info) {
    // Returns true if any nested expression contributed to the where clause
    if (nexprs == null || nexprs.length == 0) {
      return;
    }

    // Process expression
    //! whereSQLExpressionIF(nexprs[0], sql, info);    
    int length = nexprs.length;
    int c = 0;
    for (int i=0; i < length; i++) {
      if (nexprs[i] == null) {
        continue;
      }
      if (c > 0) {
        sql.append(separator);
      }      
      whereSQLExpressionIF(nexprs[i], sql, info);
      c++;
    }
  }
  
  // -----------------------------------------------------------------------------
  // SQLLogicalIF
  // -----------------------------------------------------------------------------

  protected void whereSQLAnd(SQLAnd and, StringBuilder sql, BuildInfo info) {
    sql.append('(');
    whereSQLExpressionIF(and.getExpressions(), AND, sql, info);
    sql.append(')');
  }
  
  protected void whereSQLOr(SQLOr or, StringBuilder sql, BuildInfo info) {    
    sql.append('(');
    whereSQLExpressionIF(or.getExpressions(), " or ", sql, info);
    sql.append(')');
  }

  protected void whereSQLNot(SQLNot not, StringBuilder sql, BuildInfo info) {
    // FIXME: May have to special-case if nested expression is empty.
    sql.append("not (");
    whereSQLExpressionIF(not.getExpression(), sql, info);
    sql.append(')');
  }
  
  // -----------------------------------------------------------------------------
  // Operators
  // -----------------------------------------------------------------------------

  protected void whereSQLFalse(SQLFalse expr, StringBuilder sql, BuildInfo info) {
    sql.append("false");
  }
  
  protected void whereSQLEquals(SQLEquals equals, StringBuilder sql, BuildInfo info) {
    // Rewrite null values to an "is null" expression    
    if (equals.getLeft().getType() == SQLValueIF.NULL) {
      whereSQLValueEqualsNull(equals.getRight(), sql, info);
    } else if (equals.getRight().getType() == SQLValueIF.NULL) {
      whereSQLValueEqualsNull(equals.getLeft(), sql, info);
    } else {
      referenceSQLValueIFOpBinary(equals.getLeft(), "=", equals.getRight(), sql, info);
    }    
  }
  
  protected void whereSQLNotEquals(SQLNotEquals nequals, StringBuilder sql, BuildInfo info) {
    // Rewrite null values to an "is not null" expression
    if (nequals.getLeft().getType() == SQLValueIF.NULL) {
      whereSQLValueNotEqualsNull(nequals.getRight(), sql, info);
    } else if (nequals.getRight().getType() == SQLValueIF.NULL) {
      whereSQLValueNotEqualsNull(nequals.getLeft(), sql, info);
    } else {
      referenceSQLValueIFOpBinary(nequals.getLeft(), "!=", nequals.getRight(), sql, info);
    }
  }
    
  protected void whereSQLValueEqualsNull(SQLValueIF value, StringBuilder sql, BuildInfo info) {
    referenceSQLValueIFOpUnary(value, "is null", sql, info);
  }
  
  protected void whereSQLValueNotEqualsNull(SQLValueIF value, StringBuilder sql, BuildInfo info) {
    referenceSQLValueIFOpUnary(value, "is not null", sql, info);
  }
  
  protected void whereSQLIsNull(SQLIsNull is_null, StringBuilder sql, BuildInfo info) {
    referenceSQLValueIFOpUnary(is_null.getValue(), "is null", sql, info);
  }
  
  protected void whereSQLLike(SQLLike like, StringBuilder sql, BuildInfo info) {
    //! referenceSQLValueIFOpBinary(like.getLeft(), "like", like.getRight(), sql, info);

    if (like.getCaseSensitive()) {
      // SQL: A like B
      atomicSQLValueIF(like.getLeft(), sql, info);
      sql.append(" like ");
      atomicSQLValueIF(like.getRight(), sql, info);   
    } else {
      // SQL: lower(A) like lower(B)
      sql.append("lower(");
      atomicSQLValueIF(like.getLeft(), sql, info);
      sql.append(") like lower(");
      atomicSQLValueIF(like.getRight(), sql, info);   
      sql.append(')');
    }
  }

  protected boolean isPatternFunction(SQLFunction func) {
    // SQL function is a pattern function if it contains at least one $ character
    return func.getName().indexOf('$') != -1;
  }

  protected void referenceSQLFunction(SQLFunction func, StringBuilder sql, BuildInfo info) {
    SQLValueIF[] args = func.getArguments();
    String fname = func.getName();

    if (isPatternFunction(func)) {
      int pix = 0;
      while (true) {
        int ix = fname.indexOf('$', pix);
        if (ix == -1 || ix >= fname.length() - 1) { 
          sql.append(fname.substring(pix));
          break;
        } else {
          // FIXME: only 10 arguments supported
          char c = fname.charAt(ix+1);
          if (Character.isDigit(c)) {
            int cix = Character.digit(c, 10) - 1;
            sql.append(fname.substring(pix, ix));
            atomicSQLValueIF(args[cix], sql, info);
            ix = ix + 2;
          } else {
            sql.append(fname.substring(pix, ix));
            ix = ix + 1;
          }
          pix = ix;
        }
      }
    } else {
      if (">".equals(fname) || ">=".equals(fname) || "<=".equals(fname) || "<".equals(fname)) {
        referenceSQLValueIFOpBinary(args[0], fname, args[1], sql, info);
      } else {
        sql.append(func.getName()).append('(');
        for (int i=0; i < args.length; i++) {
          if (i > 0) {
            sql.append(", ");
          }
          atomicSQLValueIF(args[i], sql, info);
        }
        sql.append(')');
      }
    }
  }
  
  protected void whereSQLVerbatimExpression(SQLVerbatimExpression expr, StringBuilder sql, BuildInfo info) {
    sql.append(expr.getValue());
  }

  protected void whereSQLValueExpression(SQLValueExpression expr, StringBuilder sql, BuildInfo info) {
    atomicSQLValueIF(expr.getValue(), sql, info);
  }
  
  protected void whereSQLExists(SQLExists exists, StringBuilder sql, BuildInfo pinfo) {

    // FIXME: Only use exists expression when new variables are
    // introduced, since the from-clause will be empty otherwise.    
    
    BuildInfo info = new BuildInfo(pinfo);
    info.tlevel = pinfo.tlevel + 1;
    info.tlevels = pinfo.tlevels;

    SQLExpressionIF filter = exists.getExpression();

    StringBuilder sql_where = createWhereClause(filter, info);
    StringBuilder sql_from = createFromClause(filter, info);

    if (sql_from.length() > 0) {
      // Produce full subquery if FROM clause is not empty

      // WARN: Selecting a null might not always work?
      List selects = Collections.singletonList(new SQLNull());
      boolean distinct = false;

      StringBuilder sql_select = createSelectClause(selects, distinct, info);
      StringBuilder sql_group_by = null; // createGroupByClause(info);
      StringBuilder sql_order_by = null; // createOrderByClause(orderby, info);
      StringBuilder sql_offset_limit = null; // createOffsetLimitClause(offset, limit, info);
      
      // Embed sub query
      sql.append("exists (");
      String subsql = createStatement(sql_select, sql_where, sql_from,
                                      sql_group_by, sql_order_by, sql_offset_limit, info);      
      // Make sure parameters etc. are registered with parent build info.
      pinfo.embedInfoFrom(info, sql.length());
      sql.append(subsql);
      sql.append(')');
    } else {
      // Make sure parameters etc. are registered with parent build info.
      pinfo.embedInfoFrom(info, sql.length());
      // Embed WHERE clause when FROM clause is empty.
      sql.append(sql_where);
    }
    //! System.out.println("===2> Parameters: " + info.poffsets);

    // FIXME: Should copy parameter types, parameter field handlers
    // and parameter offsets to parent build info when done.
  }
  
  protected void whereSQLIn(SQLIn in, StringBuilder sql, BuildInfo info) {
    SQLValueIF left = in.getLeft();
    SQLValueIF right = in.getRight();
    if (left.getArity() != 1) {
      throw new OntopiaRuntimeException("Arity of left value is not 1: " + left);
    }

    // FIXME: Some databases has a limit on the number of elements in
    // the in clause, e.g. 256.
    int rarity = right.getArity();
    if (rarity > 1) {
      //! joinSQLValueIF(right, ", ", sql, info);

      // Use first value iterator
      ColumnValueIterator viter = info.viter1;
      viter.resetValue(right);

      // Loop over all elements and split into appropriate number of 
      int max_elements_in = (MAX_ELEMENTS_IN > 0 ? MAX_ELEMENTS_IN : Integer.MAX_VALUE);
      for (int i=0; i < rarity; i++) {
        if (i % max_elements_in == 0) {
          if (i > 0) {
            sql.append(") or ");
          }
          atomicSQLValueIF(left, sql, info);
          sql.append(" in (");
        }
        else {
          sql.append(", ");
        }
        
        viter.nextReference(sql, info);
      }
      sql.append(')');
      
    }
    else if (rarity == 1) {
      atomicSQLValueIF(left, sql, info);
      sql.append(" in (");
      atomicSQLValueIF(right, sql, info);      
      sql.append(')');
    } else {
      throw new OntopiaRuntimeException("Arity of right value is less than 1 (it is " + rarity + ").");
    }
  }
  
  // -----------------------------------------------------------------------------
  // WHERE - set operations
  // -----------------------------------------------------------------------------

  protected String getSetOperator(int operator) {
    switch (operator) {
    case SQLSetOperation.UNION:
      return "union";
    case SQLSetOperation.UNION_ALL:
      return "union all";
    case SQLSetOperation.INTERSECT:
      return "intersect";
    case SQLSetOperation.INTERSECT_ALL:
      return "intersect all";
    case SQLSetOperation.EXCEPT:
      return "except";
    case SQLSetOperation.EXCEPT_ALL:
      return "except all";
    default:
      throw new OntopiaRuntimeException("Unsupported set operator: '" + operator + "'");
    }
  }
  
  protected void whereSQLSetOperation(SQLSetOperation setop, StringBuilder sql, BuildInfo info) {    
    String op = getSetOperator(setop.getOperator());

    // TODO: Verify "embed build info" code!
    
    List sets = setop.getSets();
    int length = sets.size();
    for (int i=0; i < length; i++) {
      // WARN: sapdb and mckoi does not accept the additional
      // parenthesis they way they are currently constructed.
      //! sql.append('(');
      BuildInfo cinfo = new BuildInfo(info);

      Object set = sets.get(i);
      if (set instanceof SQLQuery) {
        String subsql = createStatement((SQLQuery)set, cinfo);
        // Make sure parameters etc. are registered with parent build info.
        info.embedInfoFrom(cinfo, sql.length());
        sql.append(subsql);
      } else {
        whereSQLSetOperation((SQLSetOperation)set, sql, info);
      }
      //! sql.append(')');
      if (i < length - 1) {
        sql.append(' ');
        sql.append(op);
        sql.append(' ');
      }
    }
    info.is_setop_query = true;
  }
  
  // -----------------------------------------------------------------------------
  // WHERE - joins
  // -----------------------------------------------------------------------------

  protected void whereSQLJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    // Register join with build info
    info.joins.add(join);
    // Register join tables with build info
    info.jtables.add(join.getLeft().getTable());
    info.jtables.add(join.getRight().getTable());

    // Check join type
    switch (join.getJoinType()) {
    case SQLJoin.CROSS:
      whereSQLCrossJoin(join, sql, info);      
      return;
    case SQLJoin.LEFT_OUTER:
      whereSQLLeftOuterJoin(join, sql, info);      
      return;
    case SQLJoin.RIGHT_OUTER:
      whereSQLRightOuterJoin(join, sql,info);
      return;
    default:
      throw new OntopiaRuntimeException("Unsupported WHERE SQLJoin join type: '" + join.getJoinType() + "'");      
    }
  }
  
  protected void whereSQLCrossJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    whereSQLCrossJoin_GENERIC(join, sql, info);
  }
  
  protected void whereSQLCrossJoin_GENERIC(SQLJoin join, StringBuilder sql, BuildInfo info) {
    // TASK: Cross joins inlined
    // : T1.col1 = T2.col2
    
    // Get join tables
    String lalias = join.getLeft().getTable().getAlias();
    String ralias = join.getRight().getTable().getAlias();
    // Get join columns
    String[] lcols = join.getLeft().getColumns();
    String[] rcols = join.getRight().getColumns();
    
    int length = lcols.length;
    if (length > 1) {
      sql.append('(');
    }
    for (int i=0; i < length; i++) {
      sql.append(lalias);
      sql.append('.');
      sql.append(lcols[i]);
      sql.append(" = ");
      sql.append(ralias);
      sql.append('.');
      sql.append(rcols[i]);
      if (i != length - 1) {
        sql.append(AND);
      }
    }
    if (length > 1) {
      sql.append(')');
    }
  }
  
  protected void whereSQLLeftOuterJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    whereSQLLeftOuterJoin_GENERIC(join, sql, info);
  }
  
  protected void whereSQLLeftOuterJoin_GENERIC(SQLJoin join, StringBuilder sql, BuildInfo info) {
    // no-op
  }
  
  protected void whereSQLLeftOuterJoin_ORACLE(SQLJoin join, StringBuilder sql, BuildInfo info) {
    // TASK: Left outer joins inlined
    // : T1.col1 = T2.col2(+)
    
    // Get join tables
    String lalias = join.getLeft().getTable().getAlias();
    String ralias = join.getRight().getTable().getAlias();
    // Get join columns
    String[] lcols = join.getLeft().getColumns();
    String[] rcols = join.getRight().getColumns();
    
    int length = lcols.length;
    if (length > 1) {
      sql.append('(');
    }
    for (int i=0; i < length; i++) {
      sql.append(lalias);
      sql.append('.');
      sql.append(lcols[i]);
      sql.append(" = ");
      sql.append(ralias);
      sql.append('.');
      sql.append(rcols[i]);
      sql.append("(+)");
      if (i != length - 1) {
        sql.append(AND);
      }
    }
    if (length > 1) {
      sql.append(')');
    }
  }
  
  protected void whereSQLRightOuterJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    whereSQLRightOuterJoin_GENERIC(join, sql, info);
  }
  
  protected void whereSQLRightOuterJoin_GENERIC(SQLJoin join, StringBuilder sql, BuildInfo info) {
    // no-op
  }
  
  protected void whereSQLRightOuterJoin_ORACLE(SQLJoin join, StringBuilder sql, BuildInfo info) {
    // TASK: Right outer joins inlined
    // : T1.col1(+) = T2.col2

    // Get join tables
    String lalias = join.getLeft().getTable().getAlias();
    String ralias = join.getRight().getTable().getAlias();
    // Get join columns
    String[] lcols = join.getLeft().getColumns();
    String[] rcols = join.getRight().getColumns();
    
    int length = lcols.length;
    if (length > 1) {
      sql.append('(');
    }
    for (int i=0; i < length; i++) {
      sql.append(lalias);
      sql.append('.');
      sql.append(lcols[i]);
      sql.append("(+)");
      sql.append(" = ");
      sql.append(ralias);
      sql.append('.');
      sql.append(rcols[i]);
      if (i != length - 1) {
        sql.append(AND);
      }
    }
    if (length > 1) {
      sql.append(')');
    }
  }
  
  // -----------------------------------------------------------------------------
  // WHERE - fragments
  // -----------------------------------------------------------------------------
  
  //! protected String whereSQLValueIF(SQLValueIF value, BuildInfo info) {
  //!   // FIXME: nary SQLValueIFs not supported. See referenceSQLValueIFOperations!
  //!   // FIXME: This method might be superflous. See referenceSQLValueIF!
  //!   switch (value.getType()) {
  //!   case SQLValueIF.COLUMNS:
  //!     return whereSQLColumns((SQLColumns)value, info);
  //!   case SQLValueIF.PRIMITIVE:
  //!     return whereSQLPrimitive((SQLPrimitive)value, info);
  //!   case SQLValueIF.PARAMETER:
  //!     return whereSQLParameter((SQLParameter)value, info);
  //!   case SQLValueIF.NULL:
  //!     return null;
  //!   default:
  //!     throw new OntopiaRuntimeException("Unsupported WHERE SQLValueIF: '" + value + "'");
  //!   }
  //! }
  //! 
  //! protected String whereSQLColumns(SQLColumns columns, BuildInfo info) {
  //!   SQLTable table = columns.getTable();
  //!   info.rtables.add(table);
  //!   String[] columns = columns.getColumns();
  //!   if (columns.length != 1)
  //!     throw new OntopiaRuntimeException("Unsupported WHERE SQLColumns - arity not 1: '" + columns.length + "'");
  //!   return referenceSQLColumnsColumn(table, columns[0], info);
  //! }
  //! 
  //! protected String whereSQLPrimitive(SQLPrimitive primitive, BuildInfo info) {
  //!   return referenceSQLPrimitive(primitive, info);
  //! }
  //! 
  //! protected String whereSQLParameter(SQLParameter parameter, BuildInfo info) {
  //!   return StringUtils.join(referenceSQLParameter(parameter, info), ", ");
  //! }
  
  //! // -----------------------------------------------------------------------------
  //! // ORDER BY - fragments
  //! // -----------------------------------------------------------------------------
  //! 
  //! protected void orderBySQLValueIF(SQLValueIF value, StringBuilder sql, BuildInfo info) {
  //!   // Do not register tables in order by clause
  //!   boolean register = false;
  //!   // Same output as SELECT fragments, so we delegate to the select method.
  //!   selectSQLValueIF(value, register, sql, info);
  //! }
  
  // -----------------------------------------------------------------------------
  // SQLValueIF references
  // -----------------------------------------------------------------------------

  protected void referenceSQLValueIFOpUnary(SQLValueIF value, String operator,
                                            StringBuilder sql, BuildInfo info) {
    // Unary operations: <value> <operator>
    
    // If arity is 1 there is no need to create an array.
    int arity = value.getArity();
    if (arity == 1) {
      atomicSQLValueIF(value, sql, info);
      sql.append(' ').append(operator);
    } else {
      // Use first value iterator
      ColumnValueIterator viter = info.viter1;
      viter.resetValue(value);

      // Loop over value columns and output result
      viter.nextReference(sql, info);      
      sql.append(' ').append(operator);
      for (int i=1; i < arity; i++) {
        sql.append(AND);
        viter.nextReference(sql, info);
        sql.append(operator);
      }
      
      //! String[] refs = arraySQLValueIF(value, info);
      //! 
      //! sql.append(refs[0]);
      //! sql.append(operator);
      //! // Loop over remaining values
      //! int arity = value.getArity();
      //! for (int i=1; i < arity; i++) {
      //!   sql.append(" and ");
      //!   sql.append(refs[i]);
      //!   sql.append(operator);
      //! }
    }
  }
  
  protected void referenceSQLValueIFOpBinary(SQLValueIF value1, String operator, SQLValueIF value2,
                                             StringBuilder sql, BuildInfo info) {
    // Binary operations: <value1> <operator> <value2>
    int arity1 = value1.getArity();
    int arity2 = value2.getArity();
    if (arity1 != arity2) {
      throw new OntopiaRuntimeException("Arity of values is not compatible: First: " + value1 + " (arity: " + arity1 + ") Second: " + value2 + " (arity: " + arity2 + ")");
    }

    // If arity is 1 there is no need to create an array.
    if (arity1 == 1) {
      atomicSQLValueIF(value1, sql, info);
      sql.append(' ').append(operator).append(' ');
      atomicSQLValueIF(value2, sql, info);
    } else {
      // Use first and second value iterators
      ColumnValueIterator viter1 = info.viter1;
      viter1.resetValue(value1);
      ColumnValueIterator viter2 = info.viter2;
      viter2.resetValue(value2);

      // Loop over value columns and output result
      viter1.nextReference(sql, info);      
      sql.append(' ').append(operator).append(' ');
      viter2.nextReference(sql, info);
      
      for (int i=1; i < arity1; i++) {
        sql.append(AND);
        viter1.nextReference(sql, info);      
        sql.append(' ').append(operator).append(' ');
        viter2.nextReference(sql, info);      
      }
    }
  }

  protected void atomicSQLValueIF(SQLValueIF value, StringBuilder sql, BuildInfo info) {
    // Note: SQLValueIF must have an arity of 1.
    switch (value.getType()) {
    case SQLValueIF.COLUMNS: {
      SQLColumns cols = (SQLColumns)value;
      SQLTable table = cols.getTable();
      // If table registration flag is set, register table with build info
      if (info.register_tables) {
        info.rtables.add(table);
      }
      referenceSQLColumnsColumn(table, cols.getColumns()[0], sql, info);
      return;
    }
    case SQLValueIF.TUPLE:
      atomicSQLValueIF(((SQLTuple)value).getValues()[0], sql, info);
      return;
    case SQLValueIF.PARAMETER: {
      SQLParameter param = (SQLParameter)value;
      // Register parameter type+info
      //! System.out.println("===2> Parameter " + param.getName() + " (index: " + sql.length() + ")");
      info.addParameter(param, sql.length());
      sql.append('?');
      return;
    }
    case SQLValueIF.PRIMITIVE:
      referenceSQLPrimitive((SQLPrimitive)value, sql, info);
      return;
    case SQLValueIF.NULL:
      sql.append("null");
      return;
    case SQLValueIF.VERBATIM:
      sql.append(((SQLVerbatim)value).getValue());
      return;
    case SQLValueIF.FUNCTION:
      referenceSQLFunction((SQLFunction)value, sql, info);
      return;
    default:
      throw new OntopiaRuntimeException("Unsupported SELECT SQLValueIF: '" + value + "' type: " + value.getType());
    }        
  }
  
  protected void escapeString(String value, StringBuilder sql) {
    // TODO: optimize this code
    char[] chars = value.toCharArray();
    for (int i=0; i < chars.length; i++) {
      // escape ' and \ characters
      if (chars[i] == '\'') {
        sql.append('\'');
      }
      if (chars[i] == '\\') {
        sql.append('\\');
      }
      sql.append(chars[i]);
    }
  }

  protected void joinSQLValueIF(SQLValueIF value, String separator, StringBuilder sql, BuildInfo info) {
    int arity = value.getArity();
    // Use first value iterator
    ColumnValueIterator viter = info.viter1;
    viter.resetValue(value);
    
    viter.nextReference(sql, info);
    for (int i=1; i < arity; i++) {
      sql.append(separator);
      viter.nextReference(sql, info);
    }
  }

  protected int flattenSQLValueIF(SQLValueIF[] values, SQLValueIF[] flatlist, int pos) {
    for (int i=0; i < values.length; i++) {
      pos = flattenSQLValueIF(values[i], flatlist, pos);
    }
    return pos;
  }
  
  protected int flattenSQLValueIF(SQLValueIF value, SQLValueIF[] flatlist, int pos) {
    if (value.getType() == SQLValueIF.TUPLE) {
      SQLValueIF[] values = ((SQLTuple)value).getValues();
      for (int i=0; i < values.length; i++) {
        pos = flattenSQLValueIF(values[i], flatlist, pos);
      }
    } else {
      flatlist[pos] = value;
      pos++;
    }
    return pos;
  }
  
  //! protected String[] arraySQLValueIF(SQLValueIF value, BuildInfo info) {
  //!   switch (value.getType()) {
  //!   case SQLValueIF.COLUMNS:
  //!     return arraySQLColumns((SQLColumns)value, info);
  //!   case SQLValueIF.TUPLE:
  //!     String[] allrefs = new String[value.getArity()];
  //!     int pos = 0;
  //!     SQLValueIF[] values = ((SQLTuple)value).getValues();
  //!     for (int i=0; i < values.length; i++) {
  //!       SQLValueIF _val = values[i];
  //!       //! if (_val.getArity() == 1) {
  //!       //!   allrefs[pos] = atomicSQLValueIF(_val, info);
  //!       //!   pos++;
  //!       //! } else {
  //!         String[] refs = arraySQLValueIF(values[i], info);
  //!         System.arraycopy(refs, 0, allrefs, pos, refs.length);
  //!         pos += refs.length;
  //!         //!}
  //!     }
  //!     return allrefs;
  //!   case SQLValueIF.PARAMETER:
  //!     return arraySQLParameter((SQLParameter)value, info);
  //!   case SQLValueIF.PRIMITIVE:
  //!     return new String[] { stringSQLPrimitive((SQLPrimitive)value, info) };
  //!   case SQLValueIF.NULL:
  //!     return new String[] { null };
  //!   default:
  //!     throw new OntopiaRuntimeException("Unsupported SELECT SQLValueIF: '" + value + "' type: " + value.getType());
  //!   }        
  //! }
  
  //! protected String[] arraySQLColumns(SQLColumns columns, BuildInfo info) {
  //!   SQLTable table = columns.getTable();
  //!   String[] cols = columns.getColumns();
  //! 
  //!   String[] strings = new String[cols.length];
  //!   for (int i=0; i < cols.length ; i++)
  //!     strings[i] = stringSQLColumnsColumn(table, cols[i], info);
  //! 
  //!   // If table registration flag is set, register table with build info
  //!   if (info.register_tables)
  //!     info.rtables.add(table);
  //!   
  //!   return strings;
  //! }
  
  //! protected void referenceSQLColumns(SQLColumns columns, StringBuilder sql, BuildInfo info) {
  //!   SQLTable table = columns.getTable();
  //!   String[] cols = columns.getColumns();
  //!   
  //!   String[] strings = new String[cols.length];
  //!   for (int i=0; i < cols.length ; i++)
  //!     referenceSQLColumnsColumn(table, cols[i], sql, info);
  //! 
  //!   // If table registration flag is set, register table with build info
  //!   if (info.register_tables)
  //!     info.rtables.add(table);
  //! }

  //! protected String[] arraySQLParameter(SQLParameter param, BuildInfo info) {
  //!   // FIXME: Make sure that this method is not called outside the
  //!   // where clause and only once per parameter - and in the right
  //!   // order.
  //!   
  //!   // Register parameter type+info
  //!   info.addParameter(param);
  //!   
  //!   int arity = param.getArity();
  //!   String[] strings = new String[arity];
  //!   for (int i=0; i < arity ; i++)
  //!     strings[i] = "?";
  //!   return strings;
  //! }
  
  //! protected String stringSQLPrimitive(SQLPrimitive primitive, BuildInfo info) {
  //!   switch (primitive.getSQLType()) {
  //!   case Types.VARCHAR:
  //!     return "'" + primitive.getValue() + "'";
  //!   default:
  //!     Object value = primitive.getValue();
  //!     return (value == null) ? null : value.toString();
  //!   }
  //! }

  // -----------------------------------------------------------------------------
  // Primitive values
  // -----------------------------------------------------------------------------

  protected void referenceSQLPrimitive(SQLPrimitive primitive, StringBuilder sql, BuildInfo info) {
    switch (primitive.getSQLType()) {
    case Types.VARCHAR:
    case Types.LONGVARCHAR:
    case Types.CLOB:
      sql.append('\'');
      escapeString(primitive.getValue().toString(), sql);
      sql.append('\'');
      return;
    default:
      sql.append(primitive.getValue());
      //! Object value = primitive.getValue();
      //! return (value == null) ? null : value.toString();
    }
  }

  // -----------------------------------------------------------------------------
  // Tables and columns
  // -----------------------------------------------------------------------------

  protected void referenceSQLTableAndAlias(SQLTable table, StringBuilder sql, BuildInfo info) {
    sql.append(table.getName());
    sql.append(' ');
    sql.append(table.getAlias());
  }
  
  protected void referenceSQLColumnsColumn(SQLTable table, String column, StringBuilder sql, BuildInfo info) {
    sql.append(table.getAlias());
    sql.append('.');
    sql.append(column);
  }
  
  //! protected String referenceSQLTable(SQLTable table, BuildInfo info) {
  //!   return table.getName();
  //! }
  
  //! protected String stringSQLColumnsColumn(SQLTable table, String column, BuildInfo info) {
  //!   return table.getAlias() + "." +column;
  //! }

  // -----------------------------------------------------------------------------
  // Features supported
  // -----------------------------------------------------------------------------

  @Override
  public boolean supportsLimitOffset() {
    return true;
  }

  // -----------------------------------------------------------------------------
  // Utility methods
  // -----------------------------------------------------------------------------

  public static SQLGeneratorIF getSQLGenerator(String[] platforms, Map properties) {
    // Get the first SQL generator that matches a platform in the list.
    for (int i=0; i < platforms.length; i++) {
      SQLGeneratorIF sqlgen = getSQLGenerator(platforms[i], properties);
      if (sqlgen != null) {
        return sqlgen;
      }
    }
    throw new OntopiaRuntimeException("No SQL generator could be found for the platforms: " +
                                      Arrays.asList(platforms));
  }

  public static SQLGeneratorIF getSQLGenerator(String platform, Map properties) {
    // ADD: sapdb, firebird
    if ("generic".equals(platform)) {
      return new GenericSQLGenerator(properties);
    } else if (platform.startsWith("oracle")) {
      return new OracleSQLGenerator(properties);
    } else if ("postgresql".equals(platform)) {
      return new PostgreSQLGenerator(properties);
    } else if ("sqlserver".equals(platform)) {
      return new SQLServerSQLGenerator(properties);
    } else if ("mysql".equals(platform)) {
      return new MySQLGenerator(properties);
    } else {
      return null;
    }
  }
  
}
