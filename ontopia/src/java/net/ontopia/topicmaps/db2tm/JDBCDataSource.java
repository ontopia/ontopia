
package net.ontopia.topicmaps.db2tm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Properties;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.persistence.proxy.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Data source that reads tables via JDBC.
 */
public class JDBCDataSource implements DataSourceIF {

  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(JDBCDataSource.class.getName());

  protected RelationMapping mapping;
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

  void setPropertyFile(String propfile) {
    this.propfile = propfile;
  }

  protected Connection getConnection() {
    if (conn == null) {
      try {
        ConnectionFactoryIF cf = 
          new DefaultConnectionFactory(loadProperties(), false);
        conn = cf.requestConnection();
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }
    return conn;
  }

  public void close() {
    if (conn != null) {
      try {
        conn.close();
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
      conn = null;
    }
  }
  
  public Collection getRelations() {
    Collection relations = new ArrayList();
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
        if (schema_name != null)
          relation = mapping.getRelation(schema_name + "." + table_name);
        if (relation == null)
          relation = mapping.getRelation(table_name);

        if (relation != null)
          relations.add(relation);
        else
          log.debug("No mapping found for table '" + table_name + "' in schema '" + schema_name + "'.");
      }
      rs.close();
    } catch (Throwable t) {
      throw new OntopiaRuntimeException(t);
    }
    return relations;
  }

  public TupleReaderIF getReader(String relation) {
    Relation rel = mapping.getRelation(relation);
    if (rel == null) throw new DB2TMException("Unknown relation: " + relation);
    return new TupleReader(rel);
  }

  public ChangelogReaderIF getChangelogReader(Changelog changelog, String startOrder) {
    try {
      return new ChangelogReader(changelog, startOrder);
    } catch (SQLException e) {
      throw new OntopiaRuntimeException("Error creating ChangelogReader", e);
    }
  }
  
  public String getMaxOrderValue(Changelog changelog) {
    try {
      // get datatypes
      Map<String, Integer> cdatatypes = getColumnTypes(changelog.getTable(), conn);
      Integer ocoltypeobj = cdatatypes.get(changelog.getOrderColumn());
      int ocoltype;
      if (ocoltypeobj != null)
        ocoltype = ocoltypeobj.intValue();
      else {
        ocoltype = 12; // varchar
        throw new DB2TMException("Couldn't find data type of order column '" +
                 changelog.getOrderColumn() + "'");
      }
       
      
      // prepare, bind and execute statement
      StringBuffer sb = new StringBuffer();
      sb.append("select max(");
      sb.append(changelog.getOrderColumn());
      sb.append(") from ");
      sb.append(changelog.getTable());
      String sql = sb.toString();
      log.debug("max order value query: " + sql);

      PreparedStatement pstm = null;
      ResultSet rs = null;
      try {
        pstm = conn.prepareStatement(sql);
        rs = pstm.executeQuery();
        if(rs.next())
          return JDBCUtils.getHighPrecisionString(rs, 1, ocoltype);
      } finally {
        if (rs != null) rs.close();
        if (pstm != null) pstm.close();
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
    Map datatypes = new HashMap();
    ResultSet rs = conn.getMetaData().getColumns(null, null, table, null);
    try {
      while(rs.next())
        // 4: COLUMN_NAME
        // 5: DATA_TYPE
        datatypes.put(rs.getString(4), new Integer(rs.getInt(5)));
    } finally {
      rs.close();
    }

    // sometimes the above doesn't produce any results, for reasons
    // that are obscure at the moment. if this is the case we have to
    // try a workaround
    if (datatypes.isEmpty()) {
      Statement stmt = conn.createStatement();
      try {        
        stmt.execute("select * from " + table);
        try {
          rs = stmt.getResultSet();
          ResultSetMetaData md = rs.getMetaData();
          for (int ix = 1; ix <= md.getColumnCount(); ix++)
            datatypes.put(md.getColumnName(ix), new Integer(md.getColumnType(ix)));
        } finally {
          rs.close();
        }
      } finally {
        stmt.close();
      }
    }
    
    return datatypes;
  }
  
  public String toString() {
    return "JDBCDataSource[propfile=" + propfile + "]";
  }

  private Properties loadProperties() throws IOException {
    File basedir = mapping.getBaseDirectory();
    InputStream is = StreamUtils.getInputStream(basedir, propfile);
    if (is == null)
      throw new DB2TMException("Couldn't find properties file '" + propfile +
                               "'");
    
    Properties props = new Properties();
    props.load(is);
    is.close();
    return props;
  }

  private class TupleReader implements TupleReaderIF {

    protected Relation relation;
    PreparedStatement stm;
    ResultSet rs;
    int[] coltypes;
    
    private TupleReader(Relation relation) {
      this.relation = relation;

      // build sql statement from relation definition
      StringBuffer sb = new StringBuffer();
      sb.append("select r.");
      String[] rcols = relation.getColumns();
      StringUtils.join(rcols, ", r.", sb);
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
      log.debug("tuple query: " + sql);

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
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
      
    }

    public String[] readNext() {
      try {
        if (rs.next()) {
          String[] result = new String[coltypes.length];
          for (int i=0; i < result.length; i++) {
            result[i] = JDBCUtils.getString(rs, i+1, coltypes[i]);
          }
          return result;
        } else 
          return null;
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }

    public void close() {
      try {
        if (rs != null) rs.close();
        if (stm != null) stm.close();
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }

  }

  private class ChangelogReader implements ChangelogReaderIF {
    protected Changelog changelog;
    PreparedStatement stm;
    ResultSet rs;
    int[] coltypes;
    int acoltype;
    int ocoltype;
    
    String orderValue;
    int tcix; // tuple start index
    int acix;
    int ocix;
    
    private ChangelogReader(Changelog changelog, String orderValue)
      throws SQLException {
      this.changelog = changelog;
      this.orderValue = orderValue;

      // FIXME: require primary key to be specified on both tables
      // add test case for it
      
      // build sql statement from relation and changelog definitions
      Relation relation = changelog.getRelation();
      String[] cpkey = changelog.getPrimaryKey();
      String[] rpkey = relation.getPrimaryKey();
      if (cpkey.length == 0 && rpkey.length > 0)
        cpkey = rpkey;
      else if(rpkey.length == 0 && cpkey.length > 0)
        rpkey = cpkey;
      else if(rpkey.length == 0 && cpkey.length == 0)
        throw new DB2TMConfigException("Please specify the primary-key on the relation and/or on the changelog table '" + changelog.getTable() + ".");
      
      String[] rcols = relation.getColumns();

      // use ordering column if no local ordering column
      String localOrderColumn = changelog.getLocalOrderColumn();
      if (localOrderColumn == null)
        localOrderColumn = changelog.getOrderColumn();
      
      StringBuffer sb = new StringBuffer();
      sb.append("select distinct");

      // NOTE: ChangelogReaderWrapper relies on the order of the fields produced
      // here, so changes here must take that into account.
      
      // list primary key of main relation
      for (int i=0; i < rpkey.length; i++) {
        if (i > 0) sb.append(", ");
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
      sb.append(changelog.getActionColumn());
      sb.append(", c.");
      sb.append(changelog.getOrderColumn());

      this.tcix = rpkey.length;
      this.acix = tcix+rcols.length+1;
      this.ocix = tcix+rcols.length+2;

      // nested query to find latest changes
      sb.append(" from");

      sb.append(" (select ");

      String[] cols = new String[cpkey.length];
      for (int i=0; i < cpkey.length; i++) {
        if (changelog.isExpressionColumn(cpkey[i]))
          cols[i] = changelog.getColumnExpression(cpkey[i]) + " as " + cpkey[i];
        else
          cols[i] = " m1." + cpkey[i];
      }
      sb.append(StringUtils.join(cols, ", "));

      sb.append(", m1.");
      sb.append(changelog.getActionColumn());
      sb.append(", m1.");
      sb.append(changelog.getOrderColumn());
      sb.append(" from ");
      sb.append(changelog.getTable());
      sb.append(" m1 ");

      // filter out ignored actions
      Collection ignoreActions = changelog.getIgnoreActions();
      if (!ignoreActions.isEmpty()) {
        sb.append("where m1.");
        sb.append(changelog.getActionColumn());
        sb.append(" not in (");        
        Iterator iter = ignoreActions.iterator();
        while (iter.hasNext()) {            
          String value = (String)iter.next();
          sb.append("?");
          if (iter.hasNext())
            sb.append(", ");
        }
        sb.append(")");
      }

      // add changelog table condition
      if (changelog.getCondition() != null) {
        if (ignoreActions.isEmpty())
          sb.append(" where ");
        else
          sb.append(" and ");
        sb.append(changelog.getCondition());
      }
      
      
      sb.append(" order by m1." + changelog.getOrderColumn());
      sb.append(") c");

      // then left outer join with data table
      sb.append(" left outer join ");
      sb.append(relation.getName());
      sb.append(" r on (");

      String[] clauses = new String[cpkey.length];
      for (int i=0; i < cpkey.length; i++)
        clauses[i] = "c." + cpkey[i] + " = r." + rpkey[i];

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
        if (i > 0) sb.append(", ");
        sb.append(" c.");
        sb.append(cpkey[i]);
      }
      sb.append(", c.");
      sb.append(changelog.getOrderColumn());

      String sql = sb.toString();
      log.debug("changelog query: " + sql);

      Connection conn = getConnection();

      // get hold of column data types
      Map rdatatypes = getColumnTypes(relation.getName(), conn);
      if (rdatatypes.isEmpty())
        throw new DB2TMInputException("Relation '" + relation.getName() + "' does not exist.");
      coltypes = new int[rcols.length];
      for (int i=0; i < rcols.length; i++) {
        if (rdatatypes.containsKey(rcols[i]))
          coltypes[i] = ((Integer)rdatatypes.get(rcols[i])).intValue();
        else
          throw new DB2TMInputException("Column '" + rcols[i] + "' in relation '" + relation.getName() + "' does not exist.");
      }
      Map cdatatypes = getColumnTypes(changelog.getTable(), conn);
      if (cdatatypes.isEmpty())
        throw new DB2TMInputException("Relation '" + changelog.getTable() + "' does not exist.");
      acoltype = ((Integer)cdatatypes.get(changelog.getActionColumn())).intValue();
      ocoltype = ((Integer)cdatatypes.get(changelog.getOrderColumn())).intValue();
        
      // FIXME: consider locking strategy. lock table?

      // prepare, bind and execute statement
      this.stm = conn.prepareStatement(sql);

      // ignore actions
      int cix = 1;
      Iterator iter = ignoreActions.iterator();
      while (iter.hasNext()) {            
        String value = (String)iter.next();
        log.debug("ignore action " + cix + ": " + value);
        JDBCUtils.setObject(this.stm, cix++, value, acoltype);
      }
      // order value
      if (orderValue != null) {
        log.debug("changelog order value: " + orderValue);
        JDBCUtils.setHighPrecisionObject(this.stm, cix, orderValue, ocoltype);
      }
      this.rs = stm.executeQuery();        
    }
    
    private String replacePrimaryKey(String col, String[] rpkey, String[] cpkey) {
      for (int i=0; i < rpkey.length; i++) {
        if (col.equals(rpkey[i]))
          return "c." + cpkey[i];
      }
      return "r." + col;
    }
    
    public String[] readNext() {
      try {
        if (rs.next()) {
          // produce tuple
          String[] result = new String[coltypes.length];
          for (int i=tcix; i < tcix+result.length; i++) {
            result[i-tcix] = JDBCUtils.getString(rs, i+1, coltypes[i-tcix]);
          }
          return result;
        } else 
          return null;
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }

    public int getChangeType() {
      try {
        boolean rowExists = (rs.getObject(1) != null);
        int changeType = changelog.getAction(JDBCUtils.getString(rs, acix, acoltype));

        // rewrite change type depending on whether row exists or not
        switch (changeType) {
        case ChangelogReaderIF.CHANGE_TYPE_CREATE:
          if (rowExists)
            return changeType;
          else
            return ChangelogReaderIF.CHANGE_TYPE_DELETE;
        case ChangelogReaderIF.CHANGE_TYPE_UPDATE:
          if (rowExists)
            return changeType;
          else
            return ChangelogReaderIF.CHANGE_TYPE_DELETE;
        case ChangelogReaderIF.CHANGE_TYPE_DELETE:
          if (rowExists)
            return changeType;
          else
            return ChangelogReaderIF.CHANGE_TYPE_DELETE;
        default:
          return changeType;
        }
        
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }

    public String getOrderValue() {
      try {
        return JDBCUtils.getHighPrecisionString(rs, ocix, ocoltype);
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }
    
    public void close() {
      try {
        if (rs != null) rs.close();
        if (stm != null) stm.close();
      } catch (Throwable t) {
        throw new OntopiaRuntimeException(t);
      }
    }
  }
}
