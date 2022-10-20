/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.ontopia.persistence.proxy.ConnectionFactoryIF;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Data source that reads tables via JDBC.
 */
public class JDBCDataSource implements DataSourceIF {

  // --- define a logging category.
  private static Logger log = LoggerFactory.getLogger(JDBCDataSource.class);

  protected final RelationMapping mapping;
  protected String propfile;

  protected String catalog;
  protected String schemaPattern;
  protected String tableNamePattern;

  protected Connection conn;

  public JDBCDataSource(RelationMapping mapping) {
    this.mapping = mapping;
  }

  public JDBCDataSource(RelationMapping mapping, Connection conn) {
    this.mapping = mapping;
    this.conn = conn;
  }

  protected void setPropertyFile(String propfile) {
    this.propfile = propfile;
  }

  protected Connection getConnection() {
    if (conn == null) {
      try {
        ConnectionFactoryIF cf = 
          new DefaultConnectionFactory(loadProperties(), false);
        conn = cf.requestConnection();
      } catch (Exception t) {
        throw new OntopiaRuntimeException(t);
      }
    }
    return conn;
  }

  @Override
  public void close() {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException t) {
        throw new OntopiaRuntimeException(t);
      }
      conn = null;
    }
  }
  
  @Override
  public Collection<Relation> getRelations() {
    Collection<Relation> relations = new ArrayList<Relation>();
    Connection c = getConnection();
    try {
      DatabaseMetaData dbm = c.getMetaData();
      ResultSet rs = dbm.getTables(catalog, schemaPattern, tableNamePattern,
                                   new String[] { "TABLE", "VIEW",
                                                  "SYSTEM TABLE"});
      while (rs.next()) {
        String schema_name = rs.getString(2);
        String table_name = rs.getString(3);

        Relation relation = null;
        if (schema_name != null) {
          relation = mapping.getRelation(schema_name + "." + table_name);
        }
        if (relation == null) {
          relation = mapping.getRelation(table_name);
        }
        if (relation == null) {
          relation = mapping.getRelation(table_name.toLowerCase());
        }

        if (relation != null) {
          relations.add(relation);
        } else {
          log.debug("No mapping found for table '{}' in schema '{}'.", table_name, schema_name);
        }
      }
      rs.close();
    } catch (Throwable t) {
      throw new OntopiaRuntimeException(t);
    }
    return relations;
  }

  @Override
  public TupleReaderIF getReader(String relation) {
    Relation rel = mapping.getRelation(relation);
    if (rel == null) {
      throw new DB2TMException("Unknown relation: " + relation);
    }
    return new TupleReader(rel);
  }

  @Override
  public ChangelogReaderIF getChangelogReader(Changelog changelog, String startOrder) {
    try {
      return new ChangelogReader(changelog, startOrder);
    } catch (SQLException e) {
      throw new OntopiaRuntimeException("Error creating ChangelogReader", e);
    }
  }
  
  @Override
  public String getMaxOrderValue(Changelog changelog) {
    try {
      // get datatypes
      Map<String, Integer> cdatatypes = getColumnTypes(changelog.getTable(), conn);
      Integer ocoltypeobj = cdatatypes.get(changelog.getOrderColumn());
      if (ocoltypeobj == null) {
        ocoltypeobj = cdatatypes.get(changelog.getOrderColumn().toUpperCase());
      }
      int ocoltype;
      if (ocoltypeobj != null) {
        ocoltype = ocoltypeobj.intValue();
      } else {
        ocoltype = 12; // varchar
        throw new DB2TMException("Couldn't find data type of order column '" +
                 changelog.getOrderColumn() + "'");
      }
      
      // prepare, bind and execute statement
      StringBuilder sb = new StringBuilder();
      sb.append("select max(");
      sb.append(changelog.getOrderColumn());
      sb.append(") from ");
      sb.append(changelog.getTable());
      String sql = sb.toString();
      log.debug("max order value query: {}", sql);

      PreparedStatement pstm = null;
      ResultSet rs = null;
      try {
        pstm = conn.prepareStatement(sql);
        rs = pstm.executeQuery();
        if(rs.next()) {
          return JDBCUtils.getHighPrecisionString(rs, 1, ocoltype);
        }
      } finally {
        if (rs != null) {
          rs.close();
        }
        if (pstm != null) {
          pstm.close();
        }
      }
      return null;
    } catch (Throwable t) {
      throw new OntopiaRuntimeException(t);
    }
  }    

  /**
   * Returns a map from column names to Integer objects representing
   * the SQL types of the columns.
   */
  private Map<String, Integer> getColumnTypes(String table, Connection conn)
    throws SQLException {
    Map<String, Integer> ctypes = getColumnTypes(null, table, conn);

    // workaround/hack for Oracle issue. this is safe, but we may want to
    // sanitize it somewhat.
    if (ctypes.isEmpty() && table.indexOf('.') != -1) {
      int pos = table.indexOf('.');
      ctypes = getColumnTypes(table.substring(0, pos),
                              table.substring(pos + 1),
                              conn);
    }
    return ctypes;
  }

  private Map<String, Integer> getColumnTypes(String schema,
                                              String table, Connection conn)
    throws SQLException {
    Map<String, Integer> datatypes = new HashMap<String, Integer>();
    ResultSet rs = conn.getMetaData().getColumns(null, null, table, null);
    try {
      while(rs.next()) {
        // 4: COLUMN_NAME
        // 5: DATA_TYPE
        datatypes.put(rs.getString(4), rs.getInt(5));
      }
    } finally {
      rs.close();
    }

    // sometimes the above doesn't produce any results, because some
    // implementations require uppercase table names here
    if (datatypes.isEmpty()) {
      // try with uppercase
      rs = conn.getMetaData().getColumns(null, null, table.toUpperCase(), null);
      try {
        while(rs.next()) {
          datatypes.put(rs.getString(4), rs.getInt(5));
        }
      } finally {
        rs.close();
      }
    }
    
    return datatypes;
  }
  
  @Override
  public String toString() {
    return "JDBCDataSource[propfile=" + propfile + "]";
  }

  private Properties loadProperties() throws IOException {
    File basedir = mapping.getBaseDirectory();
    InputStream is = StreamUtils.getInputStream(basedir, propfile);
    if (is == null) {
      throw new DB2TMException("Couldn't find properties file '" + propfile +
                               "'");
    }
    
    Properties props = new Properties();
    props.load(is);
    is.close();
    return props;
  }

  private class TupleReader implements TupleReaderIF {

    private PreparedStatement stm;
    private ResultSet rs;
    private int[] coltypes;
    
    private TupleReader(Relation relation) {
      // build sql statement from relation definition
      StringBuilder sb = new StringBuilder();
      sb.append("select r.");
      String[] rcols = relation.getColumns();
      sb.append(StringUtils.join(rcols, ", r."));
      sb.append(" from ");
      sb.append(relation.getName());
      sb.append(" r");

      // add condtion if specified
      String condition = relation.getCondition();
      if (condition != null) {
        sb.append(" where ");
        sb.append(condition);
      }

      String sql = sb.toString();
      log.debug("tuple query: {}", sql);

      // prepare query
      Connection conn = getConnection();
      try {
        stm = conn.prepareStatement(sql);
        stm.setFetchSize(1000);
        rs = stm.executeQuery();

        // get column datatypes
        coltypes = new int[rcols.length];
        ResultSetMetaData md = rs.getMetaData();
        for (int i=0; i < coltypes.length; i++) {
          coltypes[i] = md.getColumnType(i+1);
        }

      } catch (SQLException e) {
        throw new OntopiaRuntimeException("Error in query: " + sql, e);
      }
      
    }

    @Override
    public String[] readNext() {
      try {
        if (rs.next()) {
          String[] result = new String[coltypes.length];
          for (int i=0; i < result.length; i++) {
            result[i] = JDBCUtils.getString(rs, i+1, coltypes[i]);
          }
          return result;
        } else {
          return null;
        }
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }

    @Override
    public void close() {
      try {
        if (rs != null) {
          rs.close();
        }
        if (stm != null) {
          stm.close();
        }
      } catch (SQLException t) {
        throw new OntopiaRuntimeException(t);
      }
    }

  }

  private class ChangelogReader implements ChangelogReaderIF {
    private PreparedStatement stm;
    private ResultSet rs;
    private int[] coltypes;
    private int ocoltype;
    
    private int tcix; // tuple start index
    private int ocix;
    
    private ChangelogReader(Changelog changelog, String orderValue)
      throws SQLException {
      // FIXME: require primary key to be specified on both tables
      // add test case for it
      
      // build sql statement from relation and changelog definitions
      Relation relation = changelog.getRelation();
      String[] cpkey = changelog.getPrimaryKey();
      String[] rpkey = relation.getPrimaryKey();
      if (cpkey.length == 0 && rpkey.length > 0) {
        cpkey = rpkey;
      } else if(rpkey.length == 0 && cpkey.length > 0) {
        rpkey = cpkey;
      } else if(rpkey.length == 0 && cpkey.length == 0) {
        throw new DB2TMConfigException("Please specify the primary-key on the relation and/or on the changelog table '" + changelog.getTable() + ".");
      }
      
      String[] rcols = relation.getColumns();

      // use ordering column if no local ordering column
      String localOrderColumn = changelog.getLocalOrderColumn();
      if (localOrderColumn == null) {
        localOrderColumn = changelog.getOrderColumn();
      }
      
      StringBuilder sb = new StringBuilder();
      sb.append("select distinct");
      
      // list primary key of main relation
      for (int i=0; i < rpkey.length; i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(" r.");
        sb.append(rpkey[i]);
      }
      sb.append(", ");
      
      // list relation columns
      sb.append(replacePrimaryKey(rcols[0], rpkey, cpkey));
      for (int i= 1; i < rcols.length; i++) {
        sb.append(", ");
        sb.append(replacePrimaryKey(rcols[i], rpkey, cpkey));
      }
      // list changelog columns
      sb.append(", c.");
      sb.append(changelog.getOrderColumn());

      this.tcix = rpkey.length;
      this.ocix = tcix+rcols.length+1;

      // nested query to find latest changes
      sb.append(" from");

      sb.append(" (select ");

      String[] cols = new String[cpkey.length];
      for (int i=0; i < cpkey.length; i++) {
        if (changelog.isExpressionColumn(cpkey[i])) {
          cols[i] = changelog.getColumnExpression(cpkey[i]) + " as " + cpkey[i];
        } else {
          cols[i] = " m1." + cpkey[i];
        }
      }
      sb.append(StringUtils.join(cols, ", "));

      sb.append(", m1.");
      sb.append(changelog.getOrderColumn());
      sb.append(" from ");
      sb.append(changelog.getTable());
      sb.append(" m1 ");

      // add changelog table condition
      if (changelog.getCondition() != null) {
        sb.append(" where ");
        sb.append(changelog.getCondition());
      }
      
      
      sb.append(" order by m1." + changelog.getOrderColumn());
      sb.append(") c");

      // then left outer join with data table
      sb.append(" left outer join ");
      sb.append(relation.getName());
      sb.append(" r on (");

      String[] clauses = new String[cpkey.length];
      for (int i=0; i < cpkey.length; i++) {
        clauses[i] = "c." + cpkey[i] + " = r." + rpkey[i];
      }

      sb.append(StringUtils.join(clauses, " and "));

      // add condtion if specified
      String condition = relation.getCondition();
      if (condition != null) {
        sb.append(" and ");
        sb.append(condition);
      }
      
      sb.append(")");
      
      // add order condition
      if (orderValue != null) {
        sb.append(" where");
        sb.append(" c.");
        sb.append(changelog.getOrderColumn());
        sb.append(" > ?");
      }

      // order by clause
      sb.append(" order by");
      for (int i=0; i < cpkey.length; i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(" c.");
        sb.append(cpkey[i]);
      }
      sb.append(", c.");
      sb.append(changelog.getOrderColumn());

      String sql = sb.toString();
      log.debug("changelog query: {}", sql);

      Connection conn = getConnection();

        // get hold of column data types
        Map<String, Integer> rdatatypes = getColumnTypes(relation.getName(), conn);
        if (rdatatypes.isEmpty()) {
          throw new DB2TMInputException("Relation '" + relation.getName() + "' does not exist.");
      }
        coltypes = new int[rcols.length];
        for (int i=0; i < rcols.length; i++) {
          if (rdatatypes.containsKey(rcols[i])) {
            coltypes[i] = rdatatypes.get(rcols[i]).intValue();
          } else if (rdatatypes.containsKey(rcols[i].toUpperCase())) {
            coltypes[i] = rdatatypes.get(rcols[i].toUpperCase()).intValue();
          } else {
            throw new DB2TMInputException("Column '" + rcols[i] + "' in relation '" + relation.getName() + "' does not exist.");
          }
        }
        Map<String, Integer> cdatatypes = getColumnTypes(changelog.getTable(), conn);
        if (cdatatypes.isEmpty()) {
          throw new DB2TMInputException("Relation '" + changelog.getTable() + "' does not exist.");
      }
        Integer oct = cdatatypes.get(changelog.getOrderColumn());
        if (oct == null) {
          oct = cdatatypes.get(changelog.getOrderColumn().toUpperCase());
      }
        if (oct == null) {
          throw new DB2TMInputException("Order column '" +
                                        changelog.getOrderColumn() +
                                        "' does not exist");
      }
        ocoltype = oct.intValue();
        
      // FIXME: consider locking strategy. lock table?

      // prepare, bind and execute statement
      this.stm = conn.prepareStatement(sql);

      // order value
      if (orderValue != null) {
        int cix = 1;
        log.debug("changelog order value: {}", orderValue);
        JDBCUtils.setHighPrecisionObject(this.stm, cix, orderValue, ocoltype);
      }
      this.rs = stm.executeQuery();        
    }
    
    private String replacePrimaryKey(String col, String[] rpkey, String[] cpkey) {
      for (int i=0; i < rpkey.length; i++) {
        if (col.equals(rpkey[i])) {
          return "c." + cpkey[i];
        }
      }
      return "r." + col;
    }
    
    @Override
    public String[] readNext() {
      try {
        if (rs.next()) {
          // produce tuple
          String[] result = new String[coltypes.length];
          for (int i=tcix; i < tcix+result.length; i++) {
            result[i-tcix] = JDBCUtils.getString(rs, i+1, coltypes[i-tcix]);
          }
          return result;
        } else {
          return null;
        }
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }

    @Override
    public ChangeType getChangeType() {
      try {
        // if the primary key is null, then obviously the row has been
        // deleted
        if (rs.getObject(1) == null) {
          return ChangeType.DELETE;
        }
        
        // otherwise it's an update
        return ChangeType.UPDATE;
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    @Override
    public String getOrderValue() {
      try {
        return JDBCUtils.getHighPrecisionString(rs, ocix, ocoltype);
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }
    
    @Override
    public void close() {
      try {
        if (rs != null) {
          rs.close();
        }
        if (stm != null) {
          stm.close();
        }
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }
  }
}