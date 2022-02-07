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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.utils.CompactHashSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Implementation of the TMSync algorithm.
 * @since 3.1.1
 */
public class TopicMapSynchronizer {
  
  // --- define a logging category.
  private static final Logger log = LoggerFactory.getLogger(TopicMapSynchronizer.class.getName());
  
  /**
   * PUBLIC: Updates the target topic map against the source topic,
   * including all characteristics from the source topic.
   */
  public static void update(TopicMapIF target, TopicIF source) {
    update(target, source, (o) -> true);
  }
  
  /**
   * PUBLIC: Updates the target topic map against the source topic,
   * synchronizing only the characteristics from the target that are
   * accepted by the filter.
   */
  public static void update(TopicMapIF target, TopicIF source,
                            Predicate<TMObjectIF> tfilter) {
    update(target, source, tfilter, (o) -> true);
  }

  /**
   * PUBLIC: Updates the target topic map against the source topic,
   * synchronizing only the characteristics from the target and source
   * that are accepted by the filters.
   * @param target the topic map to update
   * @param source the topic to get updates from
   * @param tfilter filter for the target characteristics to update
   * @param sfilter filter for the source characteristics to include
   * @since 3.2.0
   */
  public static void update(TopicMapIF target, TopicIF source,
                            Predicate<TMObjectIF> tfilter, Predicate<TMObjectIF> sfilter) {
    
    AssociationTracker tracker = new AssociationTracker();
    update(target, source, tfilter, sfilter, tracker);

    // delete unsupported associations
    Iterator<AssociationIF> it = tracker.getUnsupported().iterator();
    while (it.hasNext()) {
      AssociationIF tassoc = it.next();
      log.debug("  target associations removed {}", tassoc); 
      tassoc.remove();
    }
  }
  
  /**
   * INTERNAL: Updates the target topic in the usual way, but does not
   * delete associations. Instead, it registers its findings using the
   * AssociationTracker. It is then up to the caller to delete
   * unwanted associations. The general principle is that associations
   * are wanted as long as there is one source that wants them; the
   * method will therefore feel free to copy new associations from the
   * source. In addition, associations to topics outside the set of
   * topics being synchronized must be kept because they cannot be
   * synchronized (they belong to the topics not being synchronized).
   */
  private static void update(TopicMapIF target, TopicIF source,
                             Predicate<TMObjectIF> tfilter, Predicate<TMObjectIF> sfilter,
                             AssociationTracker tracker) {

    TopicMapBuilderIF builder = target.getBuilder();
    
    // find target
    TopicIF targett = getTopic(target, source);
    if (targett == null) {
      targett = builder.makeTopic();
      log.debug("Updating new target {} with source {}", targett, source);
    } else {
      log.debug("Updating existing target {} with source {}", targett, source);
    }
    targett = copyIdentifiers(targett, source);
    
    // synchronize types
    Set<TopicIF> origtypes = new CompactHashSet<TopicIF>(targett.getTypes());
    Iterator<TopicIF> topicIterator = source.getTypes().iterator();
    while (topicIterator.hasNext()) {
      TopicIF stype = topicIterator.next();
      TopicIF ttype = getOrCreate(target, stype);
      if (origtypes.contains(ttype))
        origtypes.remove(ttype);
      else
        targett.addType(ttype);
    }
    topicIterator = origtypes.iterator();
    while (topicIterator.hasNext())
      targett.removeType(topicIterator.next());
    
    // synchronize names
    Map<String, TopicNameIF> originalTopicNames = new HashMap<String, TopicNameIF>();
    Iterator<TopicNameIF> topicnameIterator = targett.getTopicNames().iterator();
    while (topicnameIterator.hasNext()) {
      TopicNameIF bn = topicnameIterator.next();
      if (tfilter.test(bn)) {
        log.debug("  target name included {}", bn); 
        originalTopicNames.put(KeyGenerator.makeTopicNameKey(bn), bn);
      } else {
        log.debug("  target name excluded {}", bn); 
      }        
    }
    topicnameIterator = source.getTopicNames().iterator();
    while (topicnameIterator.hasNext()) {
      TopicNameIF sbn = topicnameIterator.next();
      if (!sfilter.test(sbn)) {
        log.debug("  source name excluded {}", sbn); 
        continue;
      }
      log.debug("  source name included {}", sbn); 
      TopicIF ttype = getOrCreate(target, sbn.getType());
      Collection<TopicIF> tscope = translateScope(target, sbn.getScope());
      String key =  KeyGenerator.makeScopeKey(tscope) + "$" +
                    KeyGenerator.makeTopicKey(ttype) + "$$" +
                    sbn.getValue();
      if (originalTopicNames.containsKey(key)) {
        TopicNameIF tbn = originalTopicNames.get(key);
        update(tbn, sbn, tfilter);
        originalTopicNames.remove(key);
      } else {
        TopicNameIF tbn = builder.makeTopicName(targett, ttype, sbn.getValue());
        addScope(tbn, tscope);
        addReifier(tbn, sbn.getReifier(), tfilter, sfilter, tracker);
        update(tbn, sbn, tfilter);
        log.debug("  target name added {}", tbn); 
      }
    }
    topicnameIterator = originalTopicNames.values().iterator();
    while (topicnameIterator.hasNext()) {
      TopicNameIF tbn = topicnameIterator.next();
      log.debug("  target name removed {}", tbn); 
      tbn.remove();
    }
    
    // synchronize occurrences
    Map<String, OccurrenceIF> originalOccurrences = new HashMap<String, OccurrenceIF>();
    Iterator<OccurrenceIF> occurrenceIterator = targett.getOccurrences().iterator();
    while (occurrenceIterator.hasNext()) {
      OccurrenceIF occ = occurrenceIterator.next();
      if (tfilter.test(occ)) {
        log.debug("  target occurrence included: {}", occ); 
        originalOccurrences.put(KeyGenerator.makeOccurrenceKey(occ), occ);
      } else {
        log.debug("  target occurrence excluded {}", occ); 
      }        
    }
    occurrenceIterator = source.getOccurrences().iterator();
    while (occurrenceIterator.hasNext()) {
      OccurrenceIF socc = occurrenceIterator.next();
      if (!sfilter.test(socc)) {
        log.debug("  source occurrence excluded {}", socc); 
        continue;
      }
      log.debug("  source occurrence included: {}", socc); 
      TopicIF ttype = getOrCreate(target, socc.getType());
      Collection<TopicIF> tscope = translateScope(target, socc.getScope());
      String key = KeyGenerator.makeScopeKey(tscope) + "$" +
                   KeyGenerator.makeTopicKey(ttype) +
                   KeyGenerator.makeDataKey(socc);
      if (originalOccurrences.containsKey(key))
        originalOccurrences.remove(key);
      else {
        OccurrenceIF tocc = builder.makeOccurrence(targett, ttype, "");
        CopyUtils.copyOccurrenceData(tocc, socc);
        addScope(tocc, tscope);
        addReifier(tocc, socc.getReifier(), tfilter, sfilter, tracker);
        log.debug("  target occurrence added {}", tocc); 
      }
    }
    occurrenceIterator = originalOccurrences.values().iterator();
    while (occurrenceIterator.hasNext()) {
      OccurrenceIF tocc = occurrenceIterator.next();
      log.debug("  target occurrence removed {}", tocc); 
      tocc.remove();
    }
    
    // synchronize associations
    //   originals tracked by AssociationTracker, not the 'origs' set
    Iterator<AssociationRoleIF> roleIterator = targett.getRoles().iterator();
    while (roleIterator.hasNext()) {
      AssociationRoleIF role = roleIterator.next();
      AssociationIF assoc = role.getAssociation();
      if (tfilter.test(assoc) && tracker.isWithinSyncSet(assoc)) {
        log.debug("  target association included: {}", assoc); 
        tracker.unwanted(assoc); // means: unwanted if not found in source
      } else {
        log.debug("  target association excluded {}", assoc); 
      }
    }
    roleIterator = source.getRoles().iterator();
    while (roleIterator.hasNext()) {
      AssociationRoleIF role = roleIterator.next();
      AssociationIF sassoc = role.getAssociation();
      if (!sfilter.test(sassoc)) {
        log.debug("  source association excluded {}", sassoc); 
        continue;
      }
      log.debug("  source association included: {}", sassoc); 
      TopicIF ttype = getOrCreate(target, sassoc.getType());
      Collection<TopicIF> tscope = translateScope(target, sassoc.getScope());

      String key = KeyGenerator.makeTopicKey(ttype) + "$" +
                   KeyGenerator.makeScopeKey(tscope) + "$" +
                   makeRoleKeys(target, sassoc.getRoles());
      if (!tracker.isKnown(key)) {
        // if the key is not known it means this association does not
        // exist in the target, and so we must create it
        AssociationIF tassoc = builder.makeAssociation(ttype);
        addScope(tassoc, tscope);
        addReifier(tassoc, sassoc.getReifier(), tfilter, sfilter, tracker);
        Iterator<AssociationRoleIF> it2 = sassoc.getRoles().iterator();
        while (it2.hasNext()) {
          role = it2.next();
          builder.makeAssociationRole(tassoc,
                                      getOrCreate(target, role.getType()),
                                      getOrCreate(target, role.getPlayer()));
        }
        log.debug("  target association added {}", tassoc); 
      }
      tracker.wanted(key);
    }
    // run duplicate suppression
    DuplicateSuppressionUtils.removeDuplicates(targett);
    DuplicateSuppressionUtils.removeDuplicateAssociations(targett);
  }

  /**
   * PUBLIC: Updates the target topic map from the source topic map,
   * synchronizing the selected topics in the target (ttopicq) with
   * the selected topics in the source (stopicq) using the deciders to
   * filter topic characteristics to synchronize.
   * @param target the topic map to update
   * @param ttopicq tolog query selecting the target topics to update
   * @param tchard filter for the target characteristics to update
   * @param source the source topic map
   * @param stopicq tolog query selecting the source topics to use
   * @param schard filter for the source characteristics to update
   */
  public static void update(TopicMapIF target, String ttopicq, Predicate<TMObjectIF> tchard,
                            TopicMapIF source, String stopicq, Predicate<TMObjectIF> schard)
    throws InvalidQueryException {   
    // build sets of topics    
    Set<TopicIF> targetts = queryForSet(target, ttopicq);
    Set<TopicIF> sourcets = queryForSet(source, stopicq);

    // loop over source topics (we change targetts later, so we have to pass
    // a copy to the tracker)
    AssociationTracker tracker =
      new AssociationTracker(new CompactHashSet<TopicIF>(targetts), sourcets);
    Iterator<TopicIF> topicIterator = sourcets.iterator();
    while (topicIterator.hasNext()) {
      TopicIF stopic = topicIterator.next();
      TopicIF ttopic = getOrCreate(target, stopic);
      targetts.remove(ttopic);
      update(target, stopic, tchard, schard, tracker);
    }

    // remove extraneous associations
    Iterator<AssociationIF> associationIterator = tracker.getUnsupported().iterator();
    while (associationIterator.hasNext()) {
      AssociationIF assoc = associationIterator.next();
      log.debug("Tracker removing {}", assoc);
      assoc.remove();
    }
    
    // remove extraneous topics
    topicIterator = targetts.iterator();
    while (topicIterator.hasNext())
      topicIterator.next().remove();
  }

  // -----------------------------------------------------------------
  // INTERNAL
  // -----------------------------------------------------------------

  private static Set<TopicIF> queryForSet(TopicMapIF tm, String query)
    throws InvalidQueryException {
    Set<TopicIF> set = new CompactHashSet<TopicIF>();
    QueryProcessorIF proc = QueryUtils.getQueryProcessor(tm);
    QueryResultIF result = proc.execute(query);
    while (result.next())
      set.add((TopicIF) result.getValue(0));
    result.close();

    return set;
  }
  
  private static void update(TopicNameIF tbn, TopicNameIF sbn,
                             Predicate<TMObjectIF> tfilter) {
    TopicMapIF target = tbn.getTopicMap();
    TopicMapBuilderIF builder = target.getBuilder();
    
    // build map of existing variants
    Map<String, VariantNameIF> origs = new HashMap<String, VariantNameIF>();
    Iterator<VariantNameIF> it = tbn.getVariants().iterator();
    while (it.hasNext()) {
      VariantNameIF vn = it.next();
      if (tfilter.test(vn))
        origs.put(KeyGenerator.makeVariantKey(vn), vn);
    }

    // walk through new variants
    it = sbn.getVariants().iterator();
    while (it.hasNext()) {
      VariantNameIF svn = it.next();
      Collection<TopicIF> tscope = translateScope(target, svn.getScope());
      String key = KeyGenerator.makeScopeKey(tscope) +
                   KeyGenerator.makeDataKey(svn);
      if (origs.containsKey(key))
        origs.remove(key); // we've got it already; remember not to delete it
      else {
        // this is a new variant; add it
        VariantNameIF tvn = builder.makeVariantName(tbn, svn.getValue(), svn.getDataType(), Collections.emptySet());
        addScope(tvn, tscope);
      }
    }

    // delete old variants not in source
    it = origs.values().iterator();
    while (it.hasNext())
      it.next().remove();
  }
  
  private static String makeRoleKeys(TopicMapIF tm, Collection<AssociationRoleIF> roles) {
    String[] rolekeys = new String[roles.size()];
    int i = 0;
    for (Iterator<AssociationRoleIF> it = roles.iterator(); it.hasNext(); ) {
      AssociationRoleIF role = it.next();
      TopicIF ttype = getOrCreate(tm, role.getType());
      TopicIF tplayer = getOrCreate(tm, role.getPlayer());
      rolekeys[i++] = KeyGenerator.makeTopicKey(ttype) + ":" +
                      KeyGenerator.makeTopicKey(tplayer);
    }

    Arrays.sort(rolekeys);
    return StringUtils.join(rolekeys, "$");
  }
  
  private static TopicIF getOrCreate(TopicMapIF tm, TopicIF source) {
    if (source == null)
      return null;
    
    TopicIF target = getTopic(tm, source);
    if (target == null) {
      target = tm.getBuilder().makeTopic();
      target = copyIdentifiers(target, source);      
    }
    return target;
  }
  
  private static TopicIF getTopic(TopicMapIF tm, TopicIF find) {
    // ISSUE: what if find maps to multiple topics in target?
    // ISSUE: what if find has no identity?
    
    TopicIF found = null;
    
    Iterator<LocatorIF> it = find.getSubjectLocators().iterator();
    while (it.hasNext() && found == null) {
      LocatorIF psi = it.next();
      found = tm.getTopicBySubjectLocator(psi);
    }

    it = find.getSubjectIdentifiers().iterator();
    while (it.hasNext() && found == null) {
      LocatorIF psi = it.next();
      found = tm.getTopicBySubjectIdentifier(psi);
    }

    it = find.getItemIdentifiers().iterator();
    while (it.hasNext() && found == null) {
      LocatorIF srcloc = it.next();
      TMObjectIF obj = tm.getObjectByItemIdentifier(srcloc);
      // ISSUE: what if this is not a topic?
      if (obj instanceof TopicIF)
        found = (TopicIF) obj;
    }

    return found;
  }

  private static TopicIF copyIdentifiers(TopicIF target, TopicIF source) {
    return MergeUtils.copyIdentifiers(target, source);
  }

  private static Collection<TopicIF> translateScope(TopicMapIF tm, Collection<TopicIF> sscope) {
    Collection<TopicIF> tscope = new ArrayList<TopicIF>();
    Iterator<TopicIF> it = sscope.iterator();
    while (it.hasNext()) {
      TopicIF topic = it.next();
      tscope.add(getOrCreate(tm, topic));
    }
    return tscope;
  }

  private static void addScope(ScopedIF scoped, Collection<TopicIF> scope) {
    Iterator<TopicIF> it = scope.iterator();
    while (it.hasNext())
      scoped.addTheme(it.next());
  }

  // reifiers is topic in source, not target!
  private static void addReifier(ReifiableIF reified, TopicIF reifiers,
                                 Predicate<TMObjectIF> tfilter, Predicate<TMObjectIF> sfilter,
                                 AssociationTracker tracker) {
    if (reifiers == null)
      return;
    
    if (!tracker.isSourceTopicsSet()) {
      // this means we're synchronizing a single topic. different mode
      // of operation
      if (!sfilter.test(reifiers))
        return; // client doesn't want the reifier, so we skip it

      // FIXME: if there is cycle of reification here we could fall into
      //        a recursion well

      // sync the reifier across
      update(reified.getTopicMap(), reifiers, tfilter, sfilter, tracker);
    } else if (!tracker.inSourceTopics(reifiers))
      // this means we're synchronizing a set of topics, but the reifier
      // is not one of them, so we skip it
      return;

    // just set the reifier. statements about the reifier will either be
    // synchronized by the main code, or have been synchronized above.
    TopicIF reifiert = getOrCreate(reified.getTopicMap(), reifiers);
    reified.setReifier(reifiert);
  }

  // --- AssociationTracker

  /**
   * Used to track which associations are wanted by at least one
   * topic, and which are not wanted by any topic. In addition, it
   * keeps track of which topics are being synchronized (in both
   * source and target) in order to be able to control which
   * associations should be synchronized.
   */
  static class AssociationTracker {
    private Set<TopicIF> targettopics; // target topics being synchronized
    private Set<TopicIF> sourcetopics; // source topics being synchronized
    private Set<String> wanted;   // there is a source which wants these associations
    private Map<String, AssociationIF> unwanted; // no source wants these associations

    public AssociationTracker(Set<TopicIF> targettopics, Set<TopicIF> sourcetopics) {
      this.targettopics = targettopics;
      this.sourcetopics = sourcetopics;
      this.wanted = new CompactHashSet<String>();
      this.unwanted = new HashMap<String, AssociationIF>();
    }
    
    public AssociationTracker() {
      this(null, null);
    }

    /**
     * Returns true iff all players are within set of topics being
     * synchronized.
     */
    public boolean isWithinSyncSet(AssociationIF assoc) {
      if (targettopics == null)
        return true;

      Iterator<AssociationRoleIF> it = assoc.getRoles().iterator();
      while (it.hasNext()) {
        AssociationRoleIF role = it.next();
        if (!targettopics.contains(role.getPlayer()))
          return false;
      }

      return true;
    }

    public boolean isKnown(String key) {
      return wanted.contains(key) || unwanted.containsKey(key);
    }

    public void wanted(String key) {
      // we do not pass the AssociationIF object as this may not exist in
      // the target
      if (unwanted.containsKey(key))
        unwanted.remove(key);
      wanted.add(key);
    }

    public void unwanted(AssociationIF assoc) {
      String key = KeyGenerator.makeAssociationKey(assoc);
      if (!wanted.contains(key))
        unwanted.put(key, assoc);
    }

    public Collection<AssociationIF> getUnsupported() {
      return unwanted.values();
    }

    public boolean isSourceTopicsSet() {
      return (sourcetopics != null);
    }

    public boolean inSourceTopics(TopicIF topic) {
      if (sourcetopics == null)
        return false;

      return sourcetopics.contains(topic);
    }
  }
}
