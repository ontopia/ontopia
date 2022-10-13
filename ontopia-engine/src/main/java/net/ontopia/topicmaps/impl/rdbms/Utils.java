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

package net.ontopia.topicmaps.impl.rdbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;

/**
 * INTERNAL: An internal utility class that contains various utility
 * operations on the topic map implementation from this package.
 *
 * @since 2.0
 */

public class Utils {

  /**
   * INTERNAL: An internal utility class that let one delete a topic
   * map physically from the database. This method will delete a topic
   * map conforming to the database schema in OKS 1.x and 2.x.
   *
   * @since 2.1.1
   */
  protected static void clearTopicMap(TopicMapIF topicmap) throws SQLException {
    truncateTopicMap(topicmap, true);    
  }

  /**
   * INTERNAL: An internal utility class that let one delete a topic
   * map physically from the database. This method will delete a topic
   * map conforming to the database schema in OKS 1.x and 2.x.
   *
   * @since 2.0
   */
  protected static void deleteTopicMap(TopicMapIF topicmap) throws SQLException {
    truncateTopicMap(topicmap, false);
  }

  /**
   * INTERNAL: Helper function that clears or deletes a topic map.
   */
  private static void truncateTopicMap(TopicMapIF topicmap, boolean clear_only)
    throws SQLException {
    RDBMSTopicMapStore store = (RDBMSTopicMapStore)topicmap.getStore();
    // Get database connection
    Connection conn = store.getConnection();

    // Get string representation of topic map primary key
    String id = Long.toString(store.getLongId());

    // Delete all database rows for this topic map
    Statement stm = conn.createStatement();
      
    if (!clear_only)
      stm.executeUpdate("delete from TM_TOPIC_MAP where id = " + id);

    stm.executeUpdate("delete from TM_TOPIC_TYPES where type_id in (select id from TM_TOPIC where topicmap_id = " + id + ")");
    stm.executeUpdate("delete from TM_TOPIC_TYPES where topic_id in (select id from TM_TOPIC where topicmap_id = " + id + ")");

    stm.executeUpdate("delete from TM_TOPIC_NAME_SCOPE where theme_id in (select id from TM_TOPIC where topicmap_id = " + id + ")");
    stm.executeUpdate("delete from TM_TOPIC_NAME_SCOPE where scoped_id in (select id from TM_TOPIC_NAME where topicmap_id = " + id + ")");

    stm.executeUpdate("delete from TM_VARIANT_NAME_SCOPE where theme_id in (select id from TM_TOPIC where topicmap_id = " + id + ")");
    stm.executeUpdate("delete from TM_VARIANT_NAME_SCOPE where scoped_id in (select id from TM_VARIANT_NAME where topicmap_id = " + id + ")");

    stm.executeUpdate("delete from TM_OCCURRENCE_SCOPE where theme_id in (select id from TM_TOPIC where topicmap_id = " + id + ")");
    stm.executeUpdate("delete from TM_OCCURRENCE_SCOPE where scoped_id in (select id from TM_OCCURRENCE where topicmap_id = " + id + ")");

    stm.executeUpdate("delete from TM_ASSOCIATION_SCOPE where theme_id in (select id from TM_TOPIC where topicmap_id = " + id + ")");
    stm.executeUpdate("delete from TM_ASSOCIATION_SCOPE where scoped_id in (select id from TM_ASSOCIATION where topicmap_id = " + id + ")");
      
    stm.executeUpdate("delete from TM_SUBJECT_IDENTIFIERS where topic_id in (select id from TM_TOPIC where topicmap_id = " + id + ")");
    stm.executeUpdate("delete from TM_SUBJECT_LOCATORS where topic_id in (select id from TM_TOPIC where topicmap_id = " + id + ")");
    stm.executeUpdate("delete from TM_ITEM_IDENTIFIERS where topicmap_id = " + id);

    stm.executeUpdate("delete from TM_TOPIC where topicmap_id = " + id);
    stm.executeUpdate("delete from TM_TOPIC_NAME where topicmap_id = " + id);
    stm.executeUpdate("delete from TM_VARIANT_NAME where topicmap_id = " + id);
    stm.executeUpdate("delete from TM_OCCURRENCE where topicmap_id = " + id);
    stm.executeUpdate("delete from TM_ASSOCIATION where topicmap_id = " + id);
    stm.executeUpdate("delete from TM_ASSOCIATION_ROLE where topicmap_id = " + id);
  }

  /**
   * INTERNAL: Helper function that effectively finds duplicate
   * characteristics and suppresses them.
   */
  public static void removeDuplicates(TopicMapIF topicmap) throws SQLException {
    RDBMSTopicMapStore store = (RDBMSTopicMapStore)topicmap.getStore();

    // flush local changes
    store.flush();

    // get database connection
    Connection conn = store.getConnection();

    // get string representation of topic map primary key
    long id = store.getLongId();

    // find duplicate names 
    String sql_dupl_names = "select b1.topic_id, b1.id, b2.id from TM_TOPIC_NAME b1, TM_TOPIC_NAME b2 where b1.topicmap_id = ? and b1.topicmap_id = b2.topicmap_id and b1.id != b2.id and b1.topic_id = b2.topic_id and ((b1.content is null and b2.content is null) or (b1.content = b2.content)) and ((b1.type_id is null and b2.type_id is null) or (b1.type_id = b2.type_id)) order by b1.topic_id";

    PreparedStatement stm_names = conn.prepareStatement(sql_dupl_names);
    try {
      stm_names.setLong(1, id);
      ResultSet rs = stm_names.executeQuery();

      long prev_topic_id = -1;
      Collection duplicates = new HashSet();

      while (rs.next()) {
        long topic_id = rs.getLong(1);

        TMObjectIF o1 = topicmap.getObjectById('B' + Long.toString(rs.getLong(2)));
        if (o1 != null) duplicates.add(o1);
        TMObjectIF o2 = topicmap.getObjectById('B' + Long.toString(rs.getLong(3)));
        if (o2 != null) duplicates.add(o2);
        
        if (topic_id != prev_topic_id && prev_topic_id != -1) {
          DuplicateSuppressionUtils.removeDuplicateTopicNames(duplicates);
          duplicates = new HashSet();
        }
      }
      if (!duplicates.isEmpty())
        DuplicateSuppressionUtils.removeDuplicateTopicNames(duplicates);

      rs.close();
      
    } finally {
      if (stm_names != null) stm_names.close();
    }

    // find duplicate occurrences 
    String sql_dupl_occurs = "select o1.id, o1.topic_id, o2.id from TM_OCCURRENCE o1, TM_OCCURRENCE o2 where o1.topicmap_id = ? and o1.topicmap_id = o2.topicmap_id and o1.id != o2.id and o1.topic_id = o2.topic_id and ((o1.content is null and o2.content is null) or (o1.content = o2.content)) and ((o1.content is null and o2.content is null) or (o1.content = o2.content)) and ((o1.datatype_address is null and o2.datatype_address is null) or (o1.datatype_address = o2.datatype_address)) and ((o1.type_id is null and o2.type_id is null) or (o1.type_id = o2.type_id))";

    PreparedStatement stm_occurs = conn.prepareStatement(sql_dupl_occurs);
    try {
      stm_occurs.setLong(1, id);
      ResultSet rs = stm_occurs.executeQuery();

      long prev_topic_id = -1;
      Collection duplicates = new HashSet();

      while (rs.next()) {
        long topic_id = rs.getLong(1);

        TMObjectIF o1 = topicmap.getObjectById('O' + Long.toString(rs.getLong(2)));
        if (o1 != null) duplicates.add(o1);
        TMObjectIF o2 = topicmap.getObjectById('O' + Long.toString(rs.getLong(3)));
        if (o2 != null) duplicates.add(o2);
        
        if (topic_id != prev_topic_id && prev_topic_id != -1) {
          DuplicateSuppressionUtils.removeDuplicateOccurrences(duplicates);
          duplicates = new HashSet();
        }
      }
      if (!duplicates.isEmpty())
        DuplicateSuppressionUtils.removeDuplicateOccurrences(duplicates);

      rs.close();
      
    } finally {
      if (stm_occurs != null) stm_occurs.close();
    }
    
    // find duplicate associations 
    //! too slow: String sql_dupl_assocs = "select distinct r1.assoc_id, r2.assoc_id from TM_ASSOCIATION_ROLE r1, TM_ASSOCIATION_ROLE r2, TM_ASSOCIATION a1, TM_ASSOCIATION a2 where r1.topicmap_id = ? and r1.topicmap_id = r2.topicmap_id and r1.id != r2.id and r1.assoc_id != r2.assoc_id and r1.assoc_id = a1.id and r2.assoc_id = a2.id and a1.id != a2.id and ((a1.type_id is null and a2.type_id is null) or (a1.type_id = a2.type_id)) and ((r1.type_id is null and r2.type_id is null) or (r1.type_id = r2.type_id)) and ((r1.player_id is null and r2.player_id is null) or (r1.player_id = r2.player_id)) and not exists (select 1 from TM_ASSOCIATION_ROLE r3, TM_ASSOCIATION_ROLE r4 where r3.assoc_id = r1.assoc_id and r4.assoc_id = r2.assoc_id and r3.id != r1.id and r4.id != r2.id and not (((r3.type_id is null and r4.type_id is null) or (r3.type_id = r4.type_id)) and ((r3.player_id is null and r4.player_id is null) or (r3.player_id = r3.player_id))))";

    // the following query does not restrict on association type as
    // joining in two more tables would make the query is extremely
    // slow
    //! String sql_dupl_assocs = "select distinct r1.assoc_id, r2.assoc_id from TM_ASSOCIATION_ROLE r1, TM_ASSOCIATION_ROLE r2 where r1.topicmap_id = ? and r1.topicmap_id = r2.topicmap_id and r1.id != r2.id and r1.assoc_id != r2.assoc_id and ((r1.type_id is null and r2.type_id is null) or (r1.type_id = r2.type_id)) and ((r1.player_id is null and r2.player_id is null) or (r1.player_id = r2.player_id)) and not exists (select 1 from TM_ASSOCIATION_ROLE r3, TM_ASSOCIATION_ROLE r4 where r3.assoc_id = r1.assoc_id and r4.assoc_id = r2.assoc_id and r3.id != r1.id and r4.id != r2.id and not (((r3.type_id is null and r4.type_id is null) or (r3.type_id = r4.type_id)) and ((r3.player_id is null and r4.player_id is null) or (r3.player_id = r3.player_id))))";

    String sql_dupl_assocs = "select distinct r1.assoc_id from TM_ASSOCIATION_ROLE r1, TM_ASSOCIATION_ROLE r2, TM_ASSOCIATION_ROLE r3, TM_ASSOCIATION_ROLE r4 where r1.topicmap_id = ? and r1.topicmap_id = r2.topicmap_id and r1.topicmap_id = r3.topicmap_id and r1.topicmap_id = r4.topicmap_id and r1.assoc_id = r3.assoc_id and r2.assoc_id = r4.assoc_id and r1.player_id = r2.player_id and r1.type_id = r2.type_id and not (r3.player_id = r4.player_id and r3.type_id = r4.type_id)";

    PreparedStatement stm_assocs = conn.prepareStatement(sql_dupl_assocs);
    try {
      stm_assocs.setLong(1, id);
      ResultSet rs = stm_assocs.executeQuery();

      Collection duplicates = new HashSet();

      while (rs.next()) {
        TMObjectIF o1 = topicmap.getObjectById('A' + Long.toString(rs.getLong(1)));
        if (o1 != null) duplicates.add(o1);
        //! TMObjectIF o2 = topicmap.getObjectById('A' + Long.toString(rs.getLong(2)));
        //! if (o2 != null) duplicates.add(o2);
      }

      if (!duplicates.isEmpty())
        DuplicateSuppressionUtils.removeDuplicateAssociations(duplicates);

      rs.close();
      
    } finally {
      if (stm_assocs != null) stm_assocs.close();
    }

  }

}
