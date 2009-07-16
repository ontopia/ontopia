
// $Id: DuplicateSuppressionUtils.java,v 1.28 2008/06/13 12:31:32 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.OntopiaRuntimeException;
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
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      List batch = (List) CollectionUtils.nextBatch(it, batchSize);
      prefetchTopics(topicmap, batch);
      Iterator iter = batch.iterator();
      while (iter.hasNext()) {
        removeDuplicates((TopicIF) iter.next());
      }
    }
    
    // remove duplicate associations (do one association type at a time)
    ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    Collection assocs;
    Iterator atypes = new ArrayList(cindex.getAssociationTypes()).iterator();
    while (atypes.hasNext()) {
      TopicIF atype = (TopicIF)atypes.next();
      assocs = cindex.getAssociations(atype);
      if (!assocs.isEmpty())
        removeDuplicateAssociations(assocs);
    }
    // remove duplicate untyped associations
    assocs = cindex.getAssociations(null);
    if (!assocs.isEmpty())
      removeDuplicateAssociations(assocs);
  }

  private static void prefetchTopics(TopicMapIF topicmap, Collection batch) {
    // TopicIF.basenames
    Prefetcher.prefetch(topicmap, batch,
                        Prefetcher.TopicIF, 
                        Prefetcher.TopicIF_names, false);

    // TopicIF.occurrences
    Prefetcher.prefetch(topicmap, batch,
                        Prefetcher.TopicIF, 
                        Prefetcher.TopicIF_occurrences, false);

    List basenames = new ArrayList();
    List occurrences = new ArrayList();
                                     
    Iterator iter = batch.iterator();
    while (iter.hasNext()) {
      TopicIF topic = (TopicIF)iter.next();
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

  private static void prefetchAssociations(TopicMapIF topicmap, Collection batch) {
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
  public static void removeDuplicateTopicNames(Collection basenames) {
    HashMap map = new HashMap();
    Iterator it = new ArrayList(basenames).iterator();
    while (it.hasNext()) {
      TopicNameIF basename = (TopicNameIF) it.next();
      String key = KeyGenerator.makeTopicNameKey(basename);
      TopicNameIF duplicate = (TopicNameIF) map.get(key);
      if (duplicate != null) {
        if (duplicate != basename) {
          copySourceLocators(duplicate, basename);
          TopicMapBuilderIF builder = duplicate.getTopicMap().getBuilder();
          
          Iterator it2 = new ArrayList(basename.getVariants()).iterator();
          while (it2.hasNext()) {
            VariantNameIF vns = (VariantNameIF) it2.next();
            VariantNameIF vnt = builder.makeVariantName(duplicate, vns.getValue(), vns.getDataType());
            copyScope(vnt, vns);
            copySourceLocators(vnt, vns);
          }
          if (basename.getTopic() != null)
            basename.remove();
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
  public static void removeDuplicateOccurrences(Collection occurs) {
    HashMap map = new HashMap();
    Iterator it = new ArrayList(occurs).iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      String key = KeyGenerator.makeOccurrenceKey(occ);

      OccurrenceIF duplicate = (OccurrenceIF) map.get(key);
      if (duplicate != null) {
        if (duplicate != occ) {
          copySourceLocators(duplicate, occ);
          if (occ.getTopic() != null)
            occ.remove();
        }
      } else {
        map.put(key, occ);
      }
    }
  }

  /**
   * INTERNAL: do not call this method.
   */
  public static void removeDuplicateAssociations(Collection assocs) {
    if (assocs.isEmpty()) return;
    
    HashMap map = new HashMap();
    int batchSize = 50;

    // get topicmap
    AssociationIF a = (AssociationIF)CollectionUtils.getFirst(assocs);
    TopicMapIF topicmap = a.getTopicMap();

    Iterator it = new ArrayList(assocs).iterator();
    while (it.hasNext()) {

      // prefetch associations
      List batch = (List) CollectionUtils.nextBatch(it, batchSize);
      prefetchAssociations(topicmap, batch);

      // produce key and detect duplicates
      Iterator aiter = batch.iterator();
      while (aiter.hasNext()) {
        AssociationIF assoc = (AssociationIF) aiter.next();
        removeDuplicates(assoc);
        
        String key = KeyGenerator.makeAssociationKey(assoc);
        
        AssociationIF duplicate = (AssociationIF) map.get(key);
        if (duplicate != null) {
          if (duplicate != assoc) {
            copySourceLocators(duplicate, assoc);
            assoc.remove();
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

    HashMap map = new HashMap();
    Iterator it = new ArrayList(basename.getVariants()).iterator();
    while (it.hasNext()) {
      VariantNameIF variant = (VariantNameIF) it.next();
      String key = KeyGenerator.makeVariantKey(variant);

      VariantNameIF duplicate = (VariantNameIF) map.get(key);
      if (duplicate != null) {
        copySourceLocators(duplicate, variant);
        variant.remove();
      } else {
        map.put(key, variant);
      }
    }
  }

  /**
   * PUBLIC: Remove all duplicate association roles of the association.
   */
  public static void removeDuplicates(AssociationIF assoc) {

    HashMap map = new HashMap();
    Iterator it = new ArrayList(assoc.getRoles()).iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      String key = KeyGenerator.makeAssociationRoleKey(role);

      if (map.get(key) != null) {
        copySourceLocators((AssociationRoleIF) map.get(key), role);
        role.remove();
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
  public static Map removeDuplicateAssociations(TopicIF topic) {

    Map map = new HashMap();
    Map resultMap = new HashMap();
    TopicMapIF topicmap = topic.getTopicMap();

    Iterator it = new ArrayList(topic.getRoles()).iterator();
    while (it.hasNext()) {
      AssociationIF assoc = ((AssociationRoleIF) it.next()).getAssociation();
      if (assoc == null) continue;

      String key = KeyGenerator.makeAssociationKey(assoc);
      AssociationIF existing = (AssociationIF) map.get(key);
      
      // For associations where the same topic plays more than one
      // role, the associations are the same, and this is not a duplicate. 
      if (assoc.equals(existing))
        continue;

      if (existing != null) {
        if (existing.getTopicMap() != null) {
          copySourceLocators(existing, assoc);
          assoc.remove();
          
          ((HashSet) resultMap.get(existing)).add(assoc);
        } else {
          map.put(key, assoc);
          resultMap.put(assoc, resultMap.remove(existing));
        }
      } else {
        map.put(key, assoc);
        resultMap.put(assoc, new HashSet());
      }
    }
    return resultMap;
  }

  // --- Internal helper methods

  private static void copyScope(ScopedIF target, ScopedIF source) {
    Iterator it = source.getScope().iterator();
    while (it.hasNext())
      target.addTheme((TopicIF) it.next());
  }

  private static void copySourceLocators(TMObjectIF target, TMObjectIF source) {

    Collection srclocs = source.getItemIdentifiers();
    if (srclocs.isEmpty()) return;

    Object[] list = srclocs.toArray();

    for (int i = 0; i < list.length; i++) {
      LocatorIF loc = (LocatorIF) list[i];
      source.removeItemIdentifier(loc);
      target.addItemIdentifier(loc);
    }
  }

}
