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

package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;

/**
 * PUBLIC: A helper class that can remove duplicate objects from a topic map.
 * 
 * @since 1.2
 */
public class DuplicateSuppressionUtils {

  /**
   * PUBLIC: Remove all duplicates in the entire topic map.
   */
  public static void removeDuplicates(TopicMapIF topicmap) {

    // remove duplicate topic characteristics
    int batchSize = 50;
    Iterator<TopicIF> it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      List<TopicIF> batch = CollectionUtils.nextBatch(it, batchSize);
      prefetchTopics(topicmap, batch);
      Iterator<TopicIF> iter = batch.iterator();
      while (iter.hasNext()) {
        removeDuplicates(iter.next());
      }
    }
    
    // remove duplicate associations (do one association type at a time)
    ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    Collection<AssociationIF> assocs;
    Iterator<TopicIF> atypes = new ArrayList<TopicIF>(cindex.getAssociationTypes()).iterator();
    while (atypes.hasNext()) {
      TopicIF atype = atypes.next();
      assocs = cindex.getAssociations(atype);
      if (!assocs.isEmpty()) {
        removeDuplicateAssociations(assocs);
      }
    }
    // remove duplicate untyped associations
    assocs = cindex.getAssociations(null);
    if (!assocs.isEmpty()) {
      removeDuplicateAssociations(assocs);
    }
  }

  private static void prefetchTopics(TopicMapIF topicmap, Collection<TopicIF> batch) {
    // TopicIF.basenames
    Prefetcher.prefetch(topicmap, batch,
                        Prefetcher.TopicIF, 
                        Prefetcher.TopicIF_names, false);

    // TopicIF.occurrences
    Prefetcher.prefetch(topicmap, batch,
                        Prefetcher.TopicIF, 
                        Prefetcher.TopicIF_occurrences, false);

    List<TopicNameIF> basenames = new ArrayList<TopicNameIF>();
    List<OccurrenceIF> occurrences = new ArrayList<OccurrenceIF>();
                                     
    Iterator<TopicIF> iter = batch.iterator();
    while (iter.hasNext()) {
      TopicIF topic = iter.next();
      basenames.addAll(topic.getTopicNames());
      occurrences.addAll(topic.getOccurrences());
    }
    
    // TopicNameIF.scope
    Prefetcher.prefetch(topicmap, basenames,
                        Prefetcher.TopicNameIF, 
                        Prefetcher.TopicNameIF_scope, false);
    
    // OccurrenceIF.scope
    Prefetcher.prefetch(topicmap, occurrences,
                        Prefetcher.OccurrenceIF, 
                        Prefetcher.OccurrenceIF_scope, false);

    // TopicNameIF.variants
    Prefetcher.prefetch(topicmap, basenames,
                        Prefetcher.TopicNameIF, 
                        Prefetcher.TopicNameIF_variants, false);
  }

  private static void prefetchAssociations(TopicMapIF topicmap, Collection<AssociationIF> batch) {
    // AssociationIF.type (need this as the associations themselves haven't been fully loaded)
    Prefetcher.prefetch(topicmap, batch,
                        Prefetcher.AssociationIF, 
                        Prefetcher.AssociationIF_type, false);
    // AssociationIF.roles
    Prefetcher.prefetch(topicmap, batch,
                        Prefetcher.AssociationIF, 
                        Prefetcher.AssociationIF_roles, false);
    // AssociationIF.scope
    Prefetcher.prefetch(topicmap, batch,
                        Prefetcher.AssociationIF, 
                        Prefetcher.AssociationIF_scope, false);
  }
  
  /**
   * PUBLIC: Remove all duplicated characteristics of the given topic,
   * except for duplicate associations the topic may participate in.
   */
  public static void removeDuplicates(TopicIF topic) {

    // base name duplicates
    removeDuplicateTopicNames(topic.getTopicNames());

    // occurrence duplicates
    removeDuplicateOccurrences(topic.getOccurrences());

    // duplicate association roles are not removed here; that job must
    // be done by removing duplicate associations globally
  }

  /**
   * INTERNAL: do not call this method.
   */
  public static void removeDuplicateTopicNames(Collection<TopicNameIF> basenames) {
    Map<String, TopicNameIF> map = new HashMap<String, TopicNameIF>();
    Iterator<TopicNameIF> it = new ArrayList<TopicNameIF>(basenames).iterator();
    while (it.hasNext()) {
      TopicNameIF basename = it.next();
      String key = KeyGenerator.makeTopicNameKey(basename);
      TopicNameIF duplicate = map.get(key);
      if (duplicate != null) {
        if (!basename.equals(duplicate)) {
          MergeUtils.mergeInto(duplicate, basename);
          basename = duplicate; // do this so that we can remove duplicate variants later
        }
      } else {
        map.put(key, basename);
      }

      removeDuplicates(basename);
    }
  }

  /**
   * INTERNAL: do not call this method.
   */
  public static void removeDuplicateOccurrences(Collection<OccurrenceIF> occurs) {
    Map<String, OccurrenceIF> map = new HashMap<String, OccurrenceIF>();
    Iterator<OccurrenceIF> it = new ArrayList<OccurrenceIF>(occurs).iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = it.next();
      String key = KeyGenerator.makeOccurrenceKey(occ);

      OccurrenceIF duplicate = map.get(key);
      if (duplicate != null) {
        if (!duplicate.equals(occ)) {
          MergeUtils.mergeInto(duplicate, occ);
        }
      } else {
        map.put(key, occ);
      }
    }
  }

  /**
   * INTERNAL: do not call this method.
   */
  public static void removeDuplicateAssociations(Collection<AssociationIF> assocs) {
    if (assocs.isEmpty()) {
      return;
    }
    
    Map<String, AssociationIF> map = new HashMap<String, AssociationIF>();
    int batchSize = 50;

    // get topicmap
    AssociationIF a = CollectionUtils.getFirst(assocs);
    TopicMapIF topicmap = a.getTopicMap();

    Iterator<AssociationIF> it = new ArrayList<AssociationIF>(assocs).iterator();
    while (it.hasNext()) {

      // prefetch associations
      List<AssociationIF> batch = CollectionUtils.nextBatch(it, batchSize);
      prefetchAssociations(topicmap, batch);

      // produce key and detect duplicates
      Iterator<AssociationIF> aiter = batch.iterator();
      while (aiter.hasNext()) {
        AssociationIF assoc = aiter.next();
        removeDuplicates(assoc);
        
        String key = KeyGenerator.makeAssociationKey(assoc);
        
        AssociationIF duplicate = map.get(key);
        if (duplicate != null) {
          if (!duplicate.equals(assoc)) {
            MergeUtils.mergeInto(duplicate, assoc);
          }
        } else {
          map.put(key, assoc);
        }
      }
    }
  }

  /**
   * PUBLIC: Remove all duplicate variant names of the given topic name.
   */
  public static void removeDuplicates(TopicNameIF basename) {
    Map<String, VariantNameIF> map = new HashMap<String, VariantNameIF>();
    Iterator<VariantNameIF> it = new ArrayList<VariantNameIF>(basename.getVariants()).iterator();
    while (it.hasNext()) {
      VariantNameIF variant = it.next();
      String key = KeyGenerator.makeVariantKey(variant);

      VariantNameIF duplicate = map.get(key);
      if (duplicate != null) {
        MergeUtils.mergeInto(duplicate, variant);
      } else {
        map.put(key, variant);
      }
    }
  }

  /**
   * PUBLIC: Remove all duplicate association roles of the association.
   */
  public static void removeDuplicates(AssociationIF assoc) {
    Map<String, AssociationRoleIF> map = new HashMap<String, AssociationRoleIF>();
    Iterator<AssociationRoleIF> it = new ArrayList<AssociationRoleIF>(assoc.getRoles()).iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = it.next();
      String key = KeyGenerator.makeAssociationRoleKey(role);

      if (map.get(key) != null) {
        MergeUtils.mergeInto((AssociationRoleIF) map.get(key), role);
      } else {
        map.put(key, role);
      }
    }
  }

  /**
   * PUBLIC: Removes all duplicate associations of this topic.
   * 
   * @since 2.1
   */
  public static Map<AssociationIF, Set<AssociationIF>> removeDuplicateAssociations(TopicIF topic) {
    Map<String, AssociationIF> map = new HashMap<String, AssociationIF>();
    Map<AssociationIF, Set<AssociationIF>> resultMap = new HashMap<AssociationIF, Set<AssociationIF>>();

    Iterator<AssociationRoleIF> it = new ArrayList<AssociationRoleIF>(topic.getRoles()).iterator();
    while (it.hasNext()) {
      AssociationIF assoc = it.next().getAssociation();
      if (assoc == null) {
        continue;
      }

      String key = KeyGenerator.makeAssociationKey(assoc);
      AssociationIF existing = map.get(key);
      
      // For associations where the same topic plays more than one
      // role, the associations are the same, and this is not a duplicate. 
      if (assoc.equals(existing)) {
        continue;
      }

      if (existing != null) {
        if (existing.getTopicMap() != null) {
          copySourceLocators(existing, assoc);
          assoc.remove();
          
          resultMap.get(existing).add(assoc);
        } else {
          map.put(key, assoc);
          resultMap.put(assoc, resultMap.remove(existing));
        }
      } else {
        map.put(key, assoc);
        resultMap.put(assoc, new HashSet<AssociationIF>());
      }
    }
    return resultMap;
  }

  // --- Internal helper methods

  private static void copySourceLocators(TMObjectIF target, TMObjectIF source) {

    Collection<LocatorIF> srclocs = source.getItemIdentifiers();
    if (srclocs.isEmpty()) {
      return;
    }

    LocatorIF[] list = srclocs.toArray(new LocatorIF[srclocs.size()]);

    for (int i = 0; i < list.length; i++) {
      LocatorIF loc = list[i];
      source.removeItemIdentifier(loc);
      target.addItemIdentifier(loc);
    }
  }

}
