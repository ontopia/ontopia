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

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Arrays;
import java.util.Collection;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Prefetching utility
 */

public class Prefetcher {

  // class type constants
  public static final int AssociationIF = 0;
  public static final int AssociationRoleIF = 1;
  public static final int TopicNameIF = 2;
  public static final int OccurrenceIF = 3;
  public static final int TopicIF = 4;
  public static final int TopicMapIF = 5;
  public static final int VariantNameIF = 6;

  // AssociationIF
  public static final int AssociationIF_sources = 0;
  public static final int AssociationIF_topicmap = 1;
  public static final int AssociationIF_scope = 2;
  public static final int AssociationIF_type = 3;
  public static final int AssociationIF_roles = 4;

  // AssociationRoleIF
  public static final int AssociationRoleIF_sources = 0;
  public static final int AssociationRoleIF_topicmap = 1;
  public static final int AssociationRoleIF_association = 2;
  public static final int AssociationRoleIF_type = 3;
  public static final int AssociationRoleIF_player = 4;

  // TopicNameIF
  public static final int TopicNameIF_sources = 0;
  public static final int TopicNameIF_topicmap = 1;
  public static final int TopicNameIF_topic = 2;
  public static final int TopicNameIF_scope = 3;
  public static final int TopicNameIF_type = 4;
  public static final int TopicNameIF_value = 5;
  public static final int TopicNameIF_variants = 6;

  // OccurrenceIF
  public static final int OccurrenceIF_sources = 0;
  public static final int OccurrenceIF_topicmap = 1;
  public static final int OccurrenceIF_topic = 2;
  public static final int OccurrenceIF_scope = 3;
  public static final int OccurrenceIF_type = 4;
  public static final int OccurrenceIF_value = 5;
  public static final int OccurrenceIF_locator = 6;

  // TopicIF
  public static final int TopicIF_sources = 0;
  public static final int TopicIF_topicmap = 1;
  public static final int TopicIF_subject = 2;
  public static final int TopicIF_indicators = 3;
  public static final int TopicIF_scope = 4;
  public static final int TopicIF_types = 5;
  public static final int TopicIF_names = 6;
  public static final int TopicIF_occurrences = 7;
  public static final int TopicIF_roles = 8;

  // TopicMapIF
  public static final int TopicMapIF_sources = 0;
  public static final int TopicMapIF_scope = 1;

  // VariantNameIF
  public static final int VariantNameIF_sources = 0;
  public static final int VariantNameIF_topicmap = 1;
  public static final int VariantNameIF_name = 2;
  public static final int VariantNameIF_scope = 3;
  public static final int VariantNameIF_value = 4;
  public static final int VariantNameIF_locator = 5;
  
  // -----------------------------------------------------------------------------
  // Object[] prefetching
  // -----------------------------------------------------------------------------

  public static boolean prefetch(TopicMapIF tm, Object[] objects, 
                             int type, int field, boolean traverse) {
    if (!doPrefetch(tm)) {
      return false;
    }

    // no prefetching if no hits
    if (objects == null || objects.length == 0) {
      return false;
    }

    // prefetch fields in rdbms implementation
    return ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore)tm.getStore())
      .prefetch(type, field, traverse, Arrays.asList(objects));
  }

  public static boolean prefetch(TopicMapIF tm, Object[] objects, 
                             int type, int[] fields, boolean[] traverse) {
    if (!doPrefetch(tm)) {
      return false;
    }

    // no prefetching if no hits
    if (objects == null || objects.length == 0) {
      return false;
    }

    // prefetch fields in rdbms implementation
    return ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore)tm.getStore())
      .prefetch(type, fields, traverse, Arrays.asList(objects));
  }

  // -----------------------------------------------------------------------------
  // Collection prefetching
  // -----------------------------------------------------------------------------

  public static boolean prefetch(TopicMapIF tm, Collection objects, 
                             int type, int field, boolean traverse) {
    if (!doPrefetch(tm)) {
      return false;
    }

    // no prefetching if no hits
    if (objects.isEmpty()) {
      return false;
    }

    // prefetch fields in rdbms implementation
    return ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore)tm.getStore())
      .prefetch(type, field, traverse, objects);
  }

  public static boolean prefetch(TopicMapIF tm, Collection objects, 
                             int type, int[] fields, boolean[] traverse) {
    if (!doPrefetch(tm)) {
      return false;
    }

    // no prefetching if no hits
    if (objects.isEmpty()) {
      return false;
    }

    // prefetch fields in rdbms implementation
    return ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore)tm.getStore())
      .prefetch(type, fields, traverse, objects);
  }

  // -----------------------------------------------------------------------------
  // QueryMatches prefetching
  // -----------------------------------------------------------------------------

  public static boolean prefetch(TopicMapIF tm, QueryMatches matches, int qmidx, 
                             int type, int field, boolean traverse) {
    if (!doPrefetch(tm)) {
      return false;
    }

    // no prefetching if no hits
    if (matches.last < 0) {
      return false;
    }

    // prefetch fields in rdbms implementation
    return ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore)tm.getStore())
      .prefetch(type, field, traverse, new QueryMatchesCollection(matches, qmidx));
  }

  public static boolean prefetch(TopicMapIF tm, QueryMatches matches, int qmidx, 
                             int type, int[] fields, boolean[] traverse) {
    if (!doPrefetch(tm)) {
      return false;
    }

    // no prefetching if no hits
    if (matches.last < 0) {
      return false;
    }

    // prefetch fields in rdbms implementation
    return ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore)tm.getStore())
      .prefetch(type, fields, traverse, new QueryMatchesCollection(matches, qmidx));
  }

  // -----------------------------------------------------------------------------
  // SearchResultIF prefetch
  // -----------------------------------------------------------------------------

  public static boolean prefetch(TopicMapIF tm, SearchResultIF result, 
                             String idfname) {
    if (!doPrefetch(tm)) {
      return false;
    }

    // no prefetching if no hits
    try {
      int size = result.hits();
      if (size <= 1) {
        return false;
      }
      
      // extract object ids
      Collection oids = new CompactHashSet(size);
      for (int i=0; i < size; i++) {
       DocumentIF doc = result.getDocument(i);
       
       FieldIF idfield = doc.getField(idfname);
       if (idfield == null) {
         continue;
       }
       String idval = idfield.getValue();
       if (idval == null) {
         continue;
       }

       oids.add(idval);
      }
      
      // prefetch objects in rdbms implementation
      return ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore)tm.getStore())
       .prefetchObjectsById(oids);
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // -----------------------------------------------------------------------------
  // Specialized prefetch hacks
  // -----------------------------------------------------------------------------

  public static boolean prefetchRolesByType(TopicMapIF tm, QueryMatches matches, int qmidx, 
                                       TopicIF rtype, int[] fields, boolean[] traverse) {
    if (!doPrefetch(tm)) {
      return false;
    }

    // no prefetching if no hits
    if (matches.last < 0) {
      return false;
    }

    // NOTE: taking advantage of the fact that the
    // TopicIF.getRolesByType results are being cached

    // collect roles by type
    Collection roles = new CompactHashSet();
    for (int row = 0; row <= matches.last; row++) {
      TopicIF topic = (TopicIF) matches.data[row][qmidx];
      roles.addAll(topic.getRolesByType(rtype));
    }
    
    // prefetch starting with roles
    return Prefetcher.prefetch(tm, roles, 
                            Prefetcher.AssociationRoleIF, fields, traverse);
    
  }

  public static boolean prefetchRolesByType(TopicMapIF tm, QueryMatches matches, int qmidx, 
                                       TopicIF rtype, TopicIF atype, int[] fields, boolean[] traverse) {
    if (!doPrefetch(tm)) {
      return false;
    }

    // no prefetching if no hits
    if (matches.last < 0) {
      return false;
    }

    Collection players = new QueryMatchesCollection(matches, qmidx);

    ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore)tm.getStore())
      .prefetchRolesByType(players, rtype, atype);
    return true;
  }

  // -----------------------------------------------------------------------------
  // Internal helper methods
  // -----------------------------------------------------------------------------

  private static boolean doPrefetch(TopicMapIF tm) {
    if (tm.getStore().getImplementation() != TopicMapStoreIF.RDBMS_IMPLEMENTATION) {
      return false;
    }

    RDBMSTopicMapStore store = (RDBMSTopicMapStore) tm.getStore();
    // no prefetching if shared cache is disabled
    if (!PropertyUtils.isTrue(store.getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.shared"), true)) {
      return false;
    }
    // default is true, but check prefetch property
    String value = store.getProperty("net.ontopia.topicmaps.query.core.prefetch");
    if (value == null) {
      return true; // on by default    
    }

    value = value.trim().toLowerCase();
    return "true".equals(value);
  }

}
