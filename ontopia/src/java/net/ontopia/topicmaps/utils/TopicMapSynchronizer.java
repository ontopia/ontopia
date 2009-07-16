
// $Id: TopicMapSynchronizer.java,v 1.19 2008/06/13 08:36:28 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.Set;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.DeciderUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;

import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.utils.QueryUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Implementation of the TMSync algorithm.
 * @since 3.1.1
 */
public class TopicMapSynchronizer {
  
  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(TopicMapSynchronizer.class.getName());
  
  /**
   * PUBLIC: Updates the target topic map against the source topic,
   * including all characteristics from the source topic.
   */
  public static void update(TopicMapIF target, TopicIF source) {
    update(target, source, DeciderUtils.getTrueDecider());
  }
  
  /**
   * PUBLIC: Updates the target topic map against the source topic,
   * synchronizing only the characteristics from the target that are
   * accepted by the filter.
   */
  public static void update(TopicMapIF target, TopicIF source,
                            DeciderIF tfilter) {
    update(target, source, tfilter, DeciderUtils.getTrueDecider());
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
                            DeciderIF tfilter, DeciderIF sfilter) {
    final boolean debug = log.isDebugEnabled();
    
    AssociationTracker tracker = new AssociationTracker();
    update(target, source, tfilter, sfilter, tracker);

    // delete unsupported associations
    Iterator it = tracker.getUnsupported().iterator();
    while (it.hasNext()) {
      AssociationIF tassoc = (AssociationIF) it.next();
      if (debug) log.debug("  target associations removed " + tassoc); 
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
                             DeciderIF tfilter, DeciderIF sfilter,
                             AssociationTracker tracker) {
    final boolean debug = log.isDebugEnabled();

    TopicMapBuilderIF builder = target.getBuilder();
    
    // find target
    TopicIF targett = getTopic(target, source);
    if (targett == null) {
      targett = builder.makeTopic();
      if (debug) log.debug("Updating new target " + targett + " with source " + source);
    } else {
      if (debug) log.debug("Updating existing target " + targett + " with source " + source);
    }
    targett = copyIdentifiers(targett, source);
    
    // synchronize types
    Set origtypes = new CompactHashSet(targett.getTypes());
    Iterator it = source.getTypes().iterator();
    while (it.hasNext()) {
      TopicIF stype = (TopicIF) it.next();
      TopicIF ttype = getOrCreate(target, stype);
      if (origtypes.contains(ttype))
        origtypes.remove(ttype);
      else
        targett.addType(ttype);
    }
    it = origtypes.iterator();
    while (it.hasNext())
      targett.removeType((TopicIF) it.next());
    
    // synchronize names
    Map origs = new HashMap();
    it = targett.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();
      if (tfilter.ok(bn)) {
        if (debug) log.debug("  target name included " + bn); 
        origs.put(KeyGenerator.makeTopicNameKey(bn), bn);
      } else {
        if (debug) log.debug("  target name excluded " + bn); 
      }        
    }
    it = source.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF sbn = (TopicNameIF) it.next();
      if (!sfilter.ok(sbn)) {
        if (debug) log.debug("  source name excluded " + sbn); 
        continue;
      }
      if (debug) log.debug("  source name included " + sbn); 
      TopicIF ttype = getOrCreate(target, sbn.getType());
      Collection tscope = translateScope(target, sbn.getScope());
      String key =  KeyGenerator.makeScopeKey(tscope) + "$" +
                    KeyGenerator.makeTopicKey(ttype) + "$$" +
                    sbn.getValue();
      if (origs.containsKey(key)) {
        TopicNameIF tbn = (TopicNameIF) origs.get(key);
        update(tbn, sbn, tfilter);
        origs.remove(key);
      } else {
        TopicNameIF tbn = builder.makeTopicName(targett, ttype, sbn.getValue());
        addScope(tbn, tscope);
        addReifier(tbn, sbn.getReifier(), tracker);
        update(tbn, sbn, tfilter);
        if (debug) log.debug("  target name added " + tbn); 
      }
    }
    it = origs.values().iterator();
    while (it.hasNext()) {
      TopicNameIF tbn = (TopicNameIF) it.next();
      if (debug) log.debug("  target name removed " + tbn); 
      tbn.remove();
    }
    
    // synchronize occurrences
    origs.clear();
    it = targett.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      if (tfilter.ok(occ)) {
        if (debug) log.debug("  target occurrence included: " + occ); 
        origs.put(KeyGenerator.makeOccurrenceKey(occ), occ);
      } else {
        if (debug) log.debug("  target occurrence excluded " + occ); 
      }        
    }
    it = source.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF socc = (OccurrenceIF) it.next();
      if (!sfilter.ok(socc)) {
        if (debug) log.debug("  source occurrence excluded " + socc); 
        continue;
      }
      if (debug) log.debug("  source occurrence included: " + socc); 
      TopicIF ttype = getOrCreate(target, socc.getType());
      Collection tscope = translateScope(target, socc.getScope());
      String key = KeyGenerator.makeScopeKey(tscope) + "$" +
                   KeyGenerator.makeTopicKey(ttype) +
                   KeyGenerator.makeDataKey(socc);
      if (origs.containsKey(key))
        origs.remove(key);
      else {
        OccurrenceIF tocc = builder.makeOccurrence(targett, ttype, "");
        CopyUtils.copyOccurrenceData(tocc, socc);
        addScope(tocc, tscope);
        addReifier(tocc, socc.getReifier(), tracker);
        if (debug) log.debug("  target occurrence added " + tocc); 
      }
    }
    it = origs.values().iterator();
    while (it.hasNext()) {
      OccurrenceIF tocc = (OccurrenceIF) it.next();
      if (debug) log.debug("  target occurrence removed " + tocc); 
      tocc.remove();
    }
    
    // synchronize associations
    //   originals tracked by AssociationTracker, not the 'origs' set
    it = targett.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      AssociationIF assoc = role.getAssociation();
      if (tfilter.ok(assoc) && tracker.isWithinSyncSet(assoc)) {
        if (debug) log.debug("  target association included: " + assoc); 
        tracker.unwanted(assoc); // means: unwanted if not found in source
      } else {
        if (debug) log.debug("  target association excluded " + assoc); 
      }
    }
    it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      AssociationIF sassoc = role.getAssociation();
      if (!sfilter.ok(sassoc)) {
        if (debug) log.debug("  source association excluded " + sassoc); 
        continue;
      }
      if (debug) log.debug("  source association included: " + sassoc); 
      TopicIF ttype = getOrCreate(target, sassoc.getType());
      Collection tscope = translateScope(target, sassoc.getScope());

      String key = KeyGenerator.makeTopicKey(ttype) + "$" +
                   KeyGenerator.makeScopeKey(tscope) + "$" +
                   makeRoleKeys(target, sassoc.getRoles());
      if (!tracker.isKnown(key)) {
        // if the key is not known it means this association does not
        // exist in the target, and so we must create it
        AssociationIF tassoc = builder.makeAssociation(ttype);
        addScope(tassoc, tscope);
        addReifier(tassoc, sassoc.getReifier(), tracker);
        Iterator it2 = sassoc.getRoles().iterator();
        while (it2.hasNext()) {
          role = (AssociationRoleIF) it2.next();
          builder.makeAssociationRole(tassoc,
                                      getOrCreate(target, role.getType()),
                                      getOrCreate(target, role.getPlayer()));
        }
        if (debug) log.debug("  target association added " + tassoc); 
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
  public static void update(TopicMapIF target, String ttopicq, DeciderIF tchard,
                            TopicMapIF source, String stopicq, DeciderIF schard)
    throws InvalidQueryException {   
    // build sets of topics    
    Set targetts = queryForSet(target, ttopicq);
    Set sourcets = queryForSet(source, stopicq);

    // loop over source topics (we change targetts later, so we have to pass
    // a copy to the tracker)
    AssociationTracker tracker =
      new AssociationTracker(new CompactHashSet(targetts), sourcets);
    Iterator it = sourcets.iterator();
    while (it.hasNext()) {
      TopicIF stopic = (TopicIF) it.next();
      TopicIF ttopic = getOrCreate(target, stopic);
      targetts.remove(ttopic);
      update(target, stopic, tchard, schard, tracker);
    }

    // remove extraneous associations
    it = tracker.getUnsupported().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();
      if (log.isDebugEnabled())
        log.debug("Tracker removing " + assoc);
      assoc.remove();
    }
    
    // remove extraneous topics
    it = targetts.iterator();
    while (it.hasNext())
      ((TopicIF) it.next()).remove();
  }

  // -----------------------------------------------------------------
  // INTERNAL
  // -----------------------------------------------------------------

  private static Set queryForSet(TopicMapIF tm, String query)
    throws InvalidQueryException {
    Set set = new CompactHashSet();
    QueryProcessorIF proc = QueryUtils.getQueryProcessor(tm);
    QueryResultIF result = proc.execute(query);
    while (result.next())
      set.add(result.getValue(0));
    result.close();

    return set;
  }
  
  private static void update(TopicNameIF tbn, TopicNameIF sbn,
                             DeciderIF tfilter) {
    TopicMapIF target = tbn.getTopicMap();
    TopicMapBuilderIF builder = target.getBuilder();
    
    // build map of existing variants
    Map origs = new HashMap();
    Iterator it = tbn.getVariants().iterator();
    while (it.hasNext()) {
      VariantNameIF vn = (VariantNameIF) it.next();
      if (tfilter.ok(vn))
        origs.put(KeyGenerator.makeVariantKey(vn), vn);
    }

    // walk through new variants
    it = sbn.getVariants().iterator();
    while (it.hasNext()) {
      VariantNameIF svn = (VariantNameIF) it.next();
      Collection tscope = translateScope(target, svn.getScope());
      String key = KeyGenerator.makeScopeKey(tscope) +
                   KeyGenerator.makeDataKey(svn);
      if (origs.containsKey(key))
        origs.remove(key); // we've got it already; remember not to delete it
      else {
        // this is a new variant; add it
        VariantNameIF tvn = builder.makeVariantName(tbn, svn.getValue(), svn.getDataType());
        addScope(tvn, tscope);
      }
    }

    // delete old variants not in source
    it = origs.values().iterator();
    while (it.hasNext())
      ((VariantNameIF) it.next()).remove();
  }
  
  private static String makeRoleKeys(TopicMapIF tm, Collection roles) {
    String[] rolekeys = new String[roles.size()];
    int i = 0;
    for (Iterator it = roles.iterator(); it.hasNext(); ) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
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
    
    Iterator it = find.getSubjectLocators().iterator();
    while (it.hasNext() && found == null) {
      LocatorIF psi = (LocatorIF) it.next();
      found = tm.getTopicBySubjectLocator(psi);
    }

    it = find.getSubjectIdentifiers().iterator();
    while (it.hasNext() && found == null) {
      LocatorIF psi = (LocatorIF) it.next();
      found = tm.getTopicBySubjectIdentifier(psi);
    }

    it = find.getItemIdentifiers().iterator();
    while (it.hasNext() && found == null) {
      LocatorIF srcloc = (LocatorIF) it.next();
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

  private static Collection translateScope(TopicMapIF tm, Collection sscope) {
    Collection tscope = new ArrayList();
    Iterator it = sscope.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      tscope.add(getOrCreate(tm, topic));
    }
    return tscope;
  }

  private static void addScope(ScopedIF scoped, Collection scope) {
    Iterator it = scope.iterator();
    while (it.hasNext())
      scoped.addTheme((TopicIF) it.next());
  }

  // reifiers is topic in source, not target!
  private static void addReifier(ReifiableIF reified, TopicIF reifiers,
                                 AssociationTracker tracker) {
    if (!tracker.inSourceTopics(reifiers))
      return;

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
    private Set targettopics; // target topics being synchronized
    private Set sourcetopics; // source topics being synchronized
    private Set wanted;   // there is a source which wants these associations
    private Map unwanted; // no source wants these associations

    public AssociationTracker(Set targettopics, Set sourcetopics) {
      this.targettopics = targettopics;
      this.sourcetopics = sourcetopics;
      this.wanted = new CompactHashSet();
      this.unwanted = new HashMap();
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

      Iterator it = assoc.getRoles().iterator();
      while (it.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it.next();
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

    public Collection getUnsupported() {
      return unwanted.values();
    }

    public boolean inSourceTopics(TopicIF topic) {
      if (sourcetopics == null)
        return false;

      return sourcetopics.contains(topic);
    }
  }
}
