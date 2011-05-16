// $Id: RDBMSSearcher.java,v 1.2 2006/08/31 09:38:24 grove Exp $

package net.ontopia.topicmaps.query.spi;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.utils.*;

/**
 * EXPERIMENTAL: RDBMS searcher implementation that executes a SQL
 * query in the same database as the topic map is stored in. The query
 * must return two columns, the first the object id of the result
 * topic map  object (a string), the second the score (a float).<p>
 *
 * The name of the predicate is used as the key to look up the query
 * itself. The queries are read from a properties file called
 * RDBMSSearcher.props from the CLASSPATH.<p>
 *
 * The sql query can be specified directly via the 'sql' URI parameter
 * on the searcher class. Just make sure that the query is URI
 * encoded. The query file name can be overriden with the 'queryFile'
 * parameter.
 */

public class RDBMSSearcher extends AbstractSearcher {

  /**
   * PUBLIC: The mandatory default constructor.
   */
  public RDBMSSearcher() {
  }
  
  public int getValueType() {
    return SearcherIF.OBJECT_ID;
  }

  public SearchResultIF getResult(String query) {
    return new SearchResult(query);
  }

  private class SearchResult extends AbstractSearchResult {

    PreparedStatement pstm;
    ResultSet rs;
    
    SearchResult(String query) {
      TopicMapStoreIF store = topicmap.getStore();
      if (store instanceof RDBMSTopicMapStore) {
        try {
          Connection conn = ((RDBMSTopicMapStore)store).getConnection();
          pstm = conn.prepareStatement(getSQL());
          pstm.setString(1, query);
          rs = pstm.executeQuery();
        } catch (SQLException e) {
          throw new OntopiaRuntimeException(e);
        }
      }
    }

    private String getSQL() {
      String sql = (String)parameters.get("sql");
      if (sql == null) {
        String queryName = (String)parameters.get("queryName");
        if (queryName == null)
          queryName = predicateName;

        String queryFile = (String)parameters.get("queryFile");
        if (queryFile == null)
          queryFile = "RDBMSSearcher.props";
        try {
          Properties props = PropertyUtils.loadPropertiesFromClassPath(queryFile);
          sql = props.getProperty(queryName);
        } catch (IOException e) {
          throw new OntopiaRuntimeException(e);
        }
        if (sql == null)
          throw new OntopiaRuntimeException("Query with name '" + queryName + "' not found in " + queryFile + ".");
      }

      sql = StringUtils.replace(sql, "${topicmap}", topicmap.getObjectId().substring(1));
      sql = StringUtils.replace(sql, "${term}", "?");
      return sql;
    }
    
    public boolean next() {
      if (rs == null) return false;
      try {
        return rs.next();
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    public Object getValue() {
      try {
        return rs.getString(1);
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    public float getScore() {
      try {
        return rs.getFloat(2);
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    public void close() {
      try {
        if (rs != null) rs.close();
        if (pstm != null) pstm.close();
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  };

}
