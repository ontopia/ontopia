
package net.ontopia.topicmaps.impl.rdbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.StatisticsIndexIF;

/**
 * INTERNAL: An internal utility class that generates statistics based on 
 * the data that exists in the topic map.
 *
 * @since 2.2.3
 */
public class Stats {
        
  public static Map<String, Integer> getStatistics(TopicMapIF topicmap)
    throws SQLException {
    
    StatisticsIndexIF index = (StatisticsIndexIF) topicmap.getIndex(StatisticsIndexIF.class.getName());
    
    Map<String, Integer> stats = new HashMap<String, Integer>();

    // number of topics in the topic map
    stats.put("topics", index.getTopicCount());
    
    // number of typed topics and number of topic types
    stats.put("topic.types", index.getTopicTypeCount());
    
    // number of topics that have a type
    stats.put("topics.typed", index.getTypedTopicCount());
    
    // number of associations in the topic map
    stats.put("associations", index.getAssociationCount());
    
    // number of typed associations and number of association types
    stats.put("associations.typed", index.getAssociationCount());
    stats.put("association.types", index.getAssociationTypeCount());
    
    // number of association roles in the topic map
    stats.put("roles", index.getRoleCount());
    
    // number of typed association roles and number of association role types
    stats.put("roles.typed", index.getRoleCount());
    stats.put("role.types", index.getRoleTypeCount());
    
    stats.put("occurrences", index.getOccurrenceCount());
    
    stats.put("occurrences.typed", index.getOccurrenceCount());
    stats.put("occurrence.types", index.getOccurrenceTypeCount());
    
    stats.put("names", index.getTopicNameCount());
    
    //! stats.put("names.typed", cs[0]);
    stats.put("name.types", index.getTopicNameTypeCount());
    
    stats.put("variants", index.getVariantCount());
    
    //! c = getCount("select count(*) from (select distinct r.type_id, a.type_id from TM_ASSOCIATION_ROLE r, TM_ASSOCIATION a where r.topicmap_id = ? and r.assoc_id = a.id) X", store);
    //! stats.put("(r.type_id, a_type_id)", c);
    
    RDBMSTopicMapStore store = (RDBMSTopicMapStore)topicmap.getStore();
    Integer c;
    Integer[] cs;
    
    c = getCount("Stats.topicTypePairs", store);
    stats.put("topics.type.pairs", c);
    
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
}
