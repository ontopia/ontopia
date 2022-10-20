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

package net.ontopia.topicmaps.query.spi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

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

  @Override
  public int getValueType() {
    return SearcherIF.OBJECT_ID;
  }

  @Override
  public SearchResultIF getResult(String query) {
    return new SearchResult(query);
  }

  private class SearchResult extends AbstractSearchResult {

    private PreparedStatement pstm;
    private ResultSet rs;
    
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
        if (queryName == null) {
          queryName = predicateName;
        }

        String queryFile = (String)parameters.get("queryFile");
        if (queryFile == null) {
          queryFile = "RDBMSSearcher.props";
        }
        try {
          Properties props = PropertyUtils.loadPropertiesFromClassPath(queryFile);
          sql = props.getProperty(queryName);
        } catch (IOException e) {
          throw new OntopiaRuntimeException(e);
        }
        if (sql == null) {
          throw new OntopiaRuntimeException("Query with name '" + queryName + "' not found in " + queryFile + ".");
        }
      }

      sql = StringUtils.replace(sql, "${topicmap}", topicmap.getObjectId().substring(1));
      sql = StringUtils.replace(sql, "${term}", "?");
      return sql;
    }
    
    @Override
    public boolean next() {
      if (rs == null) {
        return false;
      }
      try {
        return rs.next();
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    @Override
    public Object getValue() {
      try {
        return rs.getString(1);
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    @Override
    public float getScore() {
      try {
        return rs.getFloat(2);
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    @Override
    public void close() {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstm != null) {
          pstm.close();
        }
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }

}
