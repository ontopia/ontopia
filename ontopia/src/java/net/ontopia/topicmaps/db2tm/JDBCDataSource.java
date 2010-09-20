
// $Id: JDBCDataSource.java,v 1.32 2009/01/23 13:13:13 lars.garshol Exp $

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
import java.util.HashSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.persistence.proxy.*;
import net.ontopia.utils.StreamUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Data source that reads tables via JDBC.
 */
public class JDBCDataSource implements DataSourceIF {

  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(JDBCDataSource.class.getName());

  protected RelationMapping mapping;
  protected File propfile;

  protected String catalog;
  protected String schemaPattern;
  protected String tableNamePattern;

  protected Connection conn;
  private InputStream propstream;

  public JDBCDataSource(RelationMapping mapping) {
    this.mapping = mapping;
  }

  public JDBCDataSource(RelationMapping mapping, Connection conn) {
    this.mapping = mapping;
    this.conn = conn;
  }

  void setPropertyFile(String _propfile) {
    File baseDirectory = mapping.getBaseDirectory();
    File propfile = new File(_propfile);
    if (baseDirectory != null && !propfile.isAbsolute())
      this.propfile = new File(baseDirectory, _propfile);
    else
      this.propfile = propfile;
    if (!this.propfile.exists())
      throw new DB2TMException("JDBC data source property file " + propfile + " does not exist.");
  }

  void setPropertyStream(String _props) {
    try {
      this.propstream = StreamUtils.getInputStream(_props);
      if (this.propstream == null) {
        throw new DB2TMException("JDBC data source property file " + _props + " does not exist.");
      }
    } catch (IOException ioe) {
      throw new DB2TMException("Could not open data source property file at " + _props + ": " + ioe.getMessage(), ioe);
    }
  }

  protected Connection getConnection() {
    if (conn == null) {
      try {
        ConnectionFactoryIF cf;
        if (propstream != null) cf = new DefaultConnectionFactory(PropertyUtils.loadProperties(propstream), false);
        else cf = new DefaultConnectionFactory(PropertyUtils.loadProperties(propfile), false);
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
    return new ChangelogReader(changelog, startOrder);
  }
  
  public String getMaxOrderValue(Changelog changelog) {
    try {
      // get datatypes
      Map cdatatypes = getColumnTypes(changelog.getTable(), conn);
      int ocoltype = ((Integer)cdatatypes.get(changelog.getOrderColumn())).intValue();
      
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
    
  private Map getColumnTypes(String table, Connection conn) throws SQLException {
    Map datatypes = new HashMap();
    ResultSet rs = conn.getMetaData().getColumns(null, null, table, null);
    try {
      while(rs.next()) {
        datatypes.put(rs.getString(4), new Integer(rs.getInt(5)));
      }
    } finally {
      rs.close();
    }
    return datatypes;
  }
  
  public String toString() {
    return "JDBCDataSource[propfile=" + propfile + "]";
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
    
    private ChangelogReader(Changelog changelog, String orderValue) {
      this.changelog = changelog;
      this.orderValue = orderValue;

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
      // primary keys
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
      sb.append("m1.");
      sb.append(cpkey[0]);
      for (int i=1; i < cpkey.length; i++) {
        sb.append(", m1.");
        sb.append(cpkey[i]);
      }
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
      
      sb.append(" order by m1." + changelog.getOrderColumn());
      sb.append(") c");

      // then left outer join with data table
      sb.append(" left outer join ");
      sb.append(relation.getName());
      sb.append(" r on (");
      sb.append("c.");
      sb.append(cpkey[0]);
      sb.append(" = r.");
      sb.append(rpkey[0]);
      for (int i=1; i < cpkey.length; i++) {
        sb.append(" and c.");
        sb.append(cpkey[i]);
        sb.append(" = r.");
        sb.append(rpkey[i]);
      }

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

      try {
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
        
      } catch (Throwable t) {
        throw new OntopiaRuntimeException("Problems occurred when reading changes from table " + changelog.getTable(), t);
      }
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
