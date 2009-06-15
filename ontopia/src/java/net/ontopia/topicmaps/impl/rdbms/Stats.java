
// $Id: Stats.java,v 1.7 2006/07/05 08:50:18 grove Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapIF;


/**
 * INTERNAL: An internal utility class that generates statistics based on 
 * the data that exists in the topic map.
 *
 * @since: 2.2.3
 */

public class Stats {
        
  public static Map getStatistics(TopicMapIF topicmap) throws SQLException {
    RDBMSTopicMapStore store = (RDBMSTopicMapStore)topicmap.getStore();
    
    Map stats = new HashMap();

    Integer c;
    Integer[] cs;
    
    // number of topics in the topic map
    c = getCount("Stats.topics", store);
    stats.put("topics", c);
    
    // number of typed topics and number of topic types
    cs = getCounts("Stats.topicTypes", store);
    stats.put("topics.type.pairs", cs[0]);
    stats.put("topic.types", cs[1]);
    
    // number of topics that have a type
    c = getCount("Stats.topicTyped", store);
    stats.put("topics.typed", c);
    
      // number of associations in the topic map
    c = getCount("Stats.associations", store);
    stats.put("associations", c);
    
    // number of typed associations and number of association types
    cs = getCounts("Stats.associationTypes", store);
    stats.put("associations.typed", cs[0]);
    stats.put("association.types", cs[1]);
    
    // number of association roles in the topic map
    c = getCount("Stats.roles", store);
    stats.put("roles", c);
    
    // number of typed association roles and number of association role types
    cs = getCounts("Stats.roleTypes", store);
    stats.put("roles.typed", cs[0]);
    stats.put("role.types", cs[1]);
    
    c = getCount("Stats.occurrences", store);
    stats.put("occurrences", c);
    
    cs = getCounts("Stats.occurrenceTypes", store);
    stats.put("occurrences.typed", cs[0]);
    stats.put("occurrence.types", cs[1]);
    
    c = getCount("Stats.names", store);
    stats.put("names", c);
    
    //! cs = getCounts("Stats.nameTypes", store);
    //! stats.put("names.typed", cs[0]);
    //! stats.put("name.types", cs[1]);
    
    c = getCount("Stats.variants", store);
    stats.put("variants", c);
    
    //! c = getCount("select count(*) from (select distinct r.type_id, a.type_id from TM_ASSOCIATION_ROLE r, TM_ASSOCIATION a where r.topicmap_id = ? and r.assoc_id = a.id) X", store);
    //! stats.put("(r.type_id, a_type_id)", c);
    
    c = getCount("Stats.rolesByType1", store);
    stats.put("rolesbytype1.keys", c);
    
    c = getCount("Stats.rolesByType2Players", store);
    stats.put("rolesbytype2.players",c );
    
    c = getCount("Stats.rolesByType2Keys", store);
    stats.put("rolesbytype2.keys", c);
    
    return stats;
  }
  
  private static Integer getCount(String sqlid, RDBMSTopicMapStore store) throws SQLException {
    Connection conn = store.getConnection();
    String sql = store.getQueryString(sqlid);
    PreparedStatement pstm = conn.prepareStatement(sql);
    try {
      pstm.setLong(1, store.getLongId());
      ResultSet rs = pstm.executeQuery();
      rs.next();
      return  new Integer(rs.getInt(1));
    } finally {
      pstm.close();
    }
  }

  private static Integer[] getCounts(String sqlid, RDBMSTopicMapStore store) throws SQLException {    
    Connection conn = store.getConnection();
    String sql = store.getQueryString(sqlid);
    PreparedStatement pstm = conn.prepareStatement(sql);
    try {
      pstm.setLong(1, store.getLongId());
      ResultSet rs = pstm.executeQuery();
      rs.next();
      int cols = rs.getMetaData().getColumnCount();
      Integer[] result = new Integer[cols];
      for (int i=0; i < cols; i++) {
        result[i] = new Integer(rs.getInt(i+1));
      }
      return result;
    } finally {
      pstm.close();
    }
  }
  
}
