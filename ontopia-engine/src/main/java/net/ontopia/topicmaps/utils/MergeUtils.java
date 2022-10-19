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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.utils.CompactHashSet;
import org.apache.commons.collections4.CollectionUtils;

/**
 * PUBLIC: Utilities for merging topics and topic maps. This class
 * provides static methods for testing whether topics should be
 * merged, merging topics and for merging topic maps. 
 */

public class MergeUtils {

  /**
   * PUBLIC: Tests whether two topics should be merged or not,
   * according to XTM rules.
   *
   * @param t1 topicIF; #1 topic to merge
   * @param t2 topicIF; #2 topic to merge
   * @return boolean; true if the topics should be merged, false otherwise.
   */
  public static boolean shouldMerge(TopicIF t1, TopicIF t2) {
    // check subject locators
    if (CollectionUtils.containsAny(t1.getSubjectLocators(), t2.getSubjectLocators()))
      return true;
    
    // check subject indicators and source locators
    if (CollectionUtils.containsAny(t1.getSubjectIdentifiers(), t2.getSubjectIdentifiers()) ||
        CollectionUtils.containsAny(t1.getItemIdentifiers(), t2.getSubjectIdentifiers()))
      return true;
    if (CollectionUtils.containsAny(t1.getItemIdentifiers(), t2.getItemIdentifiers()) ||
        CollectionUtils.containsAny(t1.getSubjectIdentifiers(), t2.getItemIdentifiers()))
      return true;

    // should merge if they reify the same object
    ReifiableIF r1 = t1.getReified();
    ReifiableIF r2 = t2.getReified();
    if (r1 != null && Objects.equals(r1, r2))
      return true;

    return false;
  }

  /**
   * PUBLIC: Merges the characteristics of one topic into another
   * topic.  The source topic stripped of characteristics, all of
   * which are moved to the target topic. Duplicate characteristics
   * are suppressed. The topics must be in the same topic map, and the
   * source topic is removed from the topic map.
   *
   * @param source topicIF; the source topic. This is empty after the
   *            operation and is removed from the topic map.
   * @param target topicIF; the target topic. This gets new characteristics.
   * @exception throws ConstraintViolationException if the two topics
   * have different values for the 'subject' property, since if they
   * do they cannot represent the same subject. If this exception is
   * thrown both topics remain untouched.
   */
  public static void mergeInto(TopicIF target, TopicIF source)
    throws ConstraintViolationException {

    if (target.getTopicMap() == null)
      throw new IllegalArgumentException("Target topic has no topic map");
    if (source.getTopicMap() == null)
      throw new IllegalArgumentException("Source topic has no topic map");
    if (!target.getTopicMap().equals(source.getTopicMap()))
      throw new IllegalArgumentException("Topics not in same topic map");
    if (target.equals(source))
      throw new IllegalArgumentException("Cannot merge topic with itself!");

    // move reified
    moveReified(target, source);

    // replace source by target throughout
    replaceTopics(target, source);

    // remove subject locators from source
    List<LocatorIF> subjectLocators = new ArrayList<LocatorIF>(source.getSubjectLocators());
    for (LocatorIF subjectLocator : subjectLocators) {
      source.removeSubjectLocator(subjectLocator);
    }

    // remove subject indicators from source
    List<LocatorIF> subjectIdentifiers = new ArrayList<LocatorIF>(source.getSubjectIdentifiers());
    for (LocatorIF subjectIdentifier: subjectIdentifiers) {
      source.removeSubjectIdentifier(subjectIdentifier);
    }

    // remove item identifiers from source
    List<LocatorIF> itemIdentifiers = new ArrayList<LocatorIF>(source.getItemIdentifiers());
    for (LocatorIF itemIdentifier : itemIdentifiers) {
      source.removeItemIdentifier(itemIdentifier);
    }

    // add subject locators to target
    for (LocatorIF subjectLocator : subjectLocators) {
      target.addSubjectLocator(subjectLocator);
    }

    // add subject indicators to target
    for (LocatorIF subjectIdentifier: subjectIdentifiers) {
      target.addSubjectIdentifier(subjectIdentifier);
    }

    // add item identifiers to target
    for (LocatorIF itemIdentifier : itemIdentifiers) {
      target.addItemIdentifier(itemIdentifier);
    }
      
    // copying types
    for (TopicIF type : source.getTypes()) {
      target.addType(type);
    }
        
    // copying base names
    Map<String, TopicNameIF> topicnameMap = buildKeyMap(target.getTopicNames());
    for (TopicNameIF sourcebn : new ArrayList<TopicNameIF>(source.getTopicNames())) {
      String key = KeyGenerator.makeTopicNameKey(sourcebn);
      TopicNameIF targetbn = topicnameMap.get(key);

      if (targetbn == null) {
        targetbn = CopyUtils.copyTopicName(target, sourcebn);
        moveReifier(targetbn, sourcebn);
        sourcebn.remove();
      } else
        mergeInto(targetbn, sourcebn);
    }

    // copying occurrences
    Map<String, OccurrenceIF> occurrenceMap = buildKeyMap(target.getOccurrences());
    for (OccurrenceIF sourceoc : new ArrayList<OccurrenceIF>(source.getOccurrences())) {
      OccurrenceIF targetoc = occurrenceMap.get(KeyGenerator.makeOccurrenceKey(sourceoc));
      if (targetoc == null) {
        targetoc = CopyUtils.copyOccurrence(target, sourceoc);
        moveReifier(targetoc, sourceoc);
        sourceoc.remove();
      } else
        mergeInto(targetoc, sourceoc);
    }

    // copying roles
    Set<String> keys = new CompactHashSet<String>();
    for (AssociationRoleIF role : target.getRoles()) {
      keys.add(KeyGenerator.makeAssociationKey(role.getAssociation()));
    }
    
    for (AssociationRoleIF ar : new ArrayList<AssociationRoleIF>(source.getRoles())) {
      ar.setPlayer(target);

      String key = KeyGenerator.makeAssociationKey(ar.getAssociation());
      if (keys.contains(key)) {
        ar.getAssociation().remove();
        // ISSUE: should we move any reifier over to the duplicate?
      }
    }

    // removing source
    source.remove();

    // notify transactions of performed merge
    notifyTransaction(source, target);
  }

  private static <R extends ReifiableIF> Map<String, R> buildKeyMap(Collection<R> objects) {
    Map<String, R> map = new HashMap<String, R>();
    Iterator<R> it = objects.iterator();
    while (it.hasNext()) {
      R object = it.next();
      String key = KeyGenerator.makeKey(object);
      map.put(key, object);
    }
    return map;
  }

  /**
   * INTERNAL: Replaces source by target throughout the topic map.
   * source and target must belong to the same topic map.
   */
  private static void replaceTopics(TopicIF target, TopicIF source) {
    TopicMapIF topicmap = target.getTopicMap();

    // types
    ClassInstanceIndexIF typeIndex = (ClassInstanceIndexIF)
      topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    replaceTopicType(typeIndex.getAssociationRoles(source), target);
    replaceTopicType(typeIndex.getAssociations(source), target);
    replaceTopicType(typeIndex.getTopicNames(source), target);
    replaceTopicType(typeIndex.getOccurrences(source), target);
    replaceTopicTypes(typeIndex.getTopics(source), target, source);

    // scopes
    ScopeIndexIF scopeIndex = (ScopeIndexIF)
      topicmap.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");

    replaceTopicInScope(scopeIndex.getAssociations(source), target, source);
    replaceTopicInScope(scopeIndex.getTopicNames(source), target, source);
    replaceTopicInScope(scopeIndex.getOccurrences(source), target, source);
    replaceTopicInScope(scopeIndex.getVariants(source), target, source);
  }

  private static <T extends TypedIF> void replaceTopicType(Collection<T> objects, TopicIF t1) {
    Iterator<T> it = objects.iterator();
    while (it.hasNext()) {
      T object = it.next();
      object.setType(t1);
    }
  }

  /**
   * INTERNAL: Replace source by target as the type of objects.
   */
  private static void replaceTopicTypes(Collection<TopicIF> objects,
                                        TopicIF target,
                                        TopicIF source) {
    Iterator<TopicIF> it = objects.iterator();
    while (it.hasNext()) {
      TopicIF object = it.next();
      object.removeType(source);
      object.addType(target);
    }
  }

  private static <S extends ScopedIF> void replaceTopicInScope(Collection<S> objects, TopicIF t1,
                                          TopicIF t2) {
    Iterator<S> it = objects.iterator();
    while (it.hasNext()) {
      S object = it.next();
      object.removeTheme(t2);
      object.addTheme(t1);
    }
  }

  /**
   * PUBLIC: Merges the source name into the target name. The two
   * names must be in the same topic map, but need not have the same
   * parent topic. It is assumed (but not verified) that the two
   * names are actually equal.
   * @since 5.1.0
   */
  public static void mergeInto(TopicNameIF target, TopicNameIF source) {
    Iterator<VariantNameIF> it = new ArrayList<VariantNameIF>(source.getVariants()).iterator();
    while (it.hasNext()) {
      VariantNameIF sourcevn = it.next();
      VariantNameIF targetvn = CopyUtils.copyVariant(target, sourcevn);
      moveReifier(targetvn, sourcevn);
      sourcevn.remove();
    }
    moveReifier(target, source);
    moveItemIdentifiers(target, source);
    source.remove();
  }

  private static void moveItemIdentifiers(TMObjectIF target, TMObjectIF source) {
    Iterator<LocatorIF> it = new ArrayList<LocatorIF>(source.getItemIdentifiers()).iterator();
    while (it.hasNext()) {
      LocatorIF itemid = it.next();
      source.removeItemIdentifier(itemid);
      target.addItemIdentifier(itemid);
    }
  }

  /**
   * PUBLIC: Merges the source occurrence into the target
   * occurrence. The two occurrences must be in the same topic map, but
   * need not have the same parent topic. It is assumed (but not
   * verified) that the two occurrences are actually equal.
   * @since 5.1.0
   */
  public static void mergeInto(OccurrenceIF target, OccurrenceIF source) {
    moveReifier(target, source);
    moveItemIdentifiers(target, source);
    source.remove();
  }

  /**
   * PUBLIC: Merges the source association into the target
   * association. The two associations must be in the same topic
   * map. If the two associations are not actually equal a
   * ConstraintViolationException is thrown.
   * @since 5.1.0
   */
  public static void mergeInto(AssociationIF target, AssociationIF source) {
    moveReifier(target, source);
    moveItemIdentifiers(target, source);

    // set up key map
    Map<String, AssociationRoleIF> keys = new HashMap<String, AssociationRoleIF>();
    Iterator<AssociationRoleIF> it = target.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = it.next();
      keys.put(KeyGenerator.makeAssociationRoleKey(role), role);
    }

    // merge the roles
    it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF srole = it.next();
      AssociationRoleIF trole = keys.get(KeyGenerator.makeAssociationRoleKey(srole));
      if (trole == null)
        throw new ConstraintViolationException("Cannot merge unequal associations");
      mergeIntoChecked(trole, srole);
    }    
    source.remove();
  }

  /**
   * PUBLIC: Merges the source role into the target role.  The two
   * roles must be in the same topic map, but need not have the same
   * parent association. If the associations are not the same, they
   * are merged, provided that they are equal; if they are not equal,
   * a ConstraintViolationException is thrown. It is assumed (but not
   * verified) that the two roles are actually equal.
   * @since 5.1.0
   */
  public static void mergeInto(AssociationRoleIF target, AssociationRoleIF source) {
    if (target.getAssociation() != source.getAssociation()) {
      String key1 = KeyGenerator.makeAssociationKey(target.getAssociation());
      String key2 = KeyGenerator.makeAssociationKey(source.getAssociation());
      if (!key1.equals(key2))
        throw new ConstraintViolationException("Cannot merge roles in different "
                                               + " associations");
      mergeInto(target.getAssociation(), source.getAssociation());
    } else {
      mergeIntoChecked(target, source);
      source.remove();
    }
  }

  private static void mergeIntoChecked(AssociationRoleIF target,
                                       AssociationRoleIF source) {
    moveReifier(target, source);
    moveItemIdentifiers(target, source);
  }

  /**
   * PUBLIC: Merges the source variant into the target variant.  The
   * two variants must be in the same topic map, but need not have the
   * same parent name. It is assumed (but not verified) that the two
   * variants are actually equal.
   * @since 5.1.0
   */
  public static void mergeInto(VariantNameIF target, VariantNameIF source) {
    moveReifier(target, source);
    moveItemIdentifiers(target, source);
    source.remove();
  }
  
  /**
   * PUBLIC: Merges the source object into the target object.  The two
   * objects must be in the same topic map, but need not have the same
   * parent. It is assumed (but not verified) that the two objects are
   * actually equal.
   * @since 5.1.0
   */
  public static void mergeInto(ReifiableIF target, ReifiableIF source) {
    if (target instanceof TopicNameIF)
      mergeInto((TopicNameIF) target, (TopicNameIF) source);
    else if (target instanceof OccurrenceIF)
      mergeInto((OccurrenceIF) target, (OccurrenceIF) source);
    else if (target instanceof AssociationIF)
      mergeInto((AssociationIF) target, (AssociationIF) source);
    else if (target instanceof AssociationRoleIF)
      mergeInto((AssociationRoleIF) target, (AssociationRoleIF) source);
    else if (target instanceof VariantNameIF)
      mergeInto((VariantNameIF) target, (VariantNameIF) source);
    else
      throw new UnsupportedOperationException("Cannot merge objects of this type: "
                                              + target);
  }

  /**
   * PUBLIC: Merges the source object into a target topic in another
   * topic map. Makes no attempt to verify that the source topic
   * represents the same subject as the target topic.
   * @since 5.1.3
   */
  public static ReifiableIF mergeInto(TopicIF target, ReifiableIF source) {
    if (source instanceof TopicNameIF)
      return mergeInto(target, (TopicNameIF) source);
    else if (source instanceof OccurrenceIF)
      return mergeInto(target, (OccurrenceIF) source);
    else if (source instanceof AssociationIF)
      return mergeInto(target.getTopicMap(), (AssociationIF) source);
    else
      throw new UnsupportedOperationException("Cannot merge objects of this type: "
                                              + source);
  }

  /**
   * PUBLIC: Merges the source topic name into the target topic in
   * another topic map. Makes no attempt to verify that the source
   * topic represents the same subject as the target topic.
   * @return The new topic name in the target topic map.
   * @since 5.1.3
   */
  public static TopicNameIF mergeInto(TopicIF target, TopicNameIF source) {
    TopicMapIF tm = target.getTopicMap();
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF type = findTopic(tm, source.getType());
    TopicNameIF newtn = builder.makeTopicName(target, type, source.getValue());
    for (TopicIF theme : source.getScope())
      newtn.addTheme(findTopic(tm, theme));
    return newtn;
  }

  /**
   * PUBLIC: Merges the source occurrence into the target topic in
   * another topic map. Makes no attempt to verify that the source
   * topic represents the same subject as the target topic.
   * @return The new occurrence in the target topic map.
   * @since 5.1.3
   */
  public static OccurrenceIF mergeInto(TopicIF target, OccurrenceIF source) {
    TopicMapIF tm = target.getTopicMap();
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF type = findTopic(tm, source.getType());
    OccurrenceIF newocc = builder.makeOccurrence(target, type,
                                                 source.getValue(),
                                                 source.getDataType());
    for (TopicIF theme : source.getScope())
      newocc.addTheme(findTopic(tm, theme));
    return newocc;
  }

  /**
   * PUBLIC: Merges the source association into the target topic
   * map. Makes no attempt to verify that the source association is
   * not already present.
   * @return The new association in the target topic map.
   * @since 5.1.3
   */
  public static AssociationIF mergeInto(TopicMapIF topicmap,
                                       AssociationIF source) {
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF type = findTopic(topicmap, source.getType());
    AssociationIF newa = builder.makeAssociation(type);
    for (TopicIF theme : source.getScope())
      newa.addTheme(findTopic(topicmap, theme));

    for (AssociationRoleIF role : source.getRoles()) {
      type = findTopic(topicmap, role.getType());
      TopicIF player = findTopic(topicmap, role.getPlayer());
      builder.makeAssociationRole(newa, type, player);
    }
    
    return newa;
  }
  
  /**
   * PUBLIC: Merges the source topic from into the target topic map,
   * when the source topic is not already in the target topic map.
   * All characteristics of the source topic are copied over, but
   * topics referenced from the source topic are only included as
   * identity stubs. The source topic is untouched.
   * @since 2.0
   */
  public static TopicIF mergeInto(TopicMapIF targettm, TopicIF source) {
    return mergeInto(targettm, source, (o) -> true);
  }

  /**
   * PUBLIC: Merges the source topic from into the target topic map,
   * when the source topic is not already in the target topic map.
   * All characteristics of the source topic that are approved by the
   * decider are copied over, but topics referenced from the source
   * topic are only included as identity stubs. The source topic is
   * untouched.
   * @param decider Used to decide which topic characteristics to copy.
   * Is asked for each base name, occurrence, and association role.
   * @since 2.0
   */
  public static TopicIF mergeInto(TopicMapIF targettm, TopicIF source,
                                  Predicate<TMObjectIF> decider) {
    if (source.getTopicMap() == targettm)
      return source;

    TopicMapBuilderIF builder = targettm.getBuilder();
    TopicIF target = copyTopic(targettm, source);

    // copying types
    Iterator<TopicIF> typeIterator = source.getTypes().iterator();
    while (typeIterator.hasNext()) 
      target.addType(copyTopic(targettm, typeIterator.next()));

    // copying base names
    Iterator<TopicNameIF> topicnameIterator = source.getTopicNames().iterator();
    while (topicnameIterator.hasNext()) {
      TopicNameIF bnsource = topicnameIterator.next();
      if (!decider.test(bnsource))
        continue;
      TopicNameIF bntarget = builder.makeTopicName(target, 
                                                 resolveTopic(builder.getTopicMap(), bnsource.getType()), 
                                                 bnsource.getValue());
      copyScope(bntarget, bnsource);

      Iterator<VariantNameIF> it2 = bnsource.getVariants().iterator();
      while (it2.hasNext()) {
        VariantNameIF vnsource = it2.next();
        if (!decider.test(vnsource))
          continue;
        
        VariantNameIF vntarget = builder.makeVariantName(bntarget, vnsource.getValue(), vnsource.getDataType(), Collections.emptySet());
        copyScope(vntarget, vnsource);
        vntarget = resolveIdentities(vntarget, vnsource);
        copyReifier(vntarget, vnsource);
      }
      
      bntarget = resolveIdentities(bntarget, bnsource);
      copyReifier(bntarget, bnsource);
    }
    
    // copying occurrences
    Iterator<OccurrenceIF> occurrenceIterator = source.getOccurrences().iterator();
    while (occurrenceIterator.hasNext()) {
      OccurrenceIF osource = occurrenceIterator.next();
      if (!decider.test(osource))
        continue;
      OccurrenceIF otarget = builder.makeOccurrence(target, 
                                                    resolveTopic(builder.getTopicMap(), osource.getType()), 
                                                    "");
      CopyUtils.copyOccurrenceData(otarget, osource);
      copyScope(otarget, osource);
      otarget = resolveIdentities(otarget, osource);
      copyReifier(otarget, osource);
    }
    
    // copying associations
    Iterator<AssociationRoleIF> roleIterator = source.getRoles().iterator();
    while (roleIterator.hasNext()) {
      AssociationRoleIF rstart = roleIterator.next();
      if (!decider.test(rstart))
        continue;
      AssociationIF asource = rstart.getAssociation();

      AssociationIF atarget = builder.makeAssociation(resolveTopic(builder.getTopicMap(), asource.getType()));
      copyScope(atarget, asource);

      Iterator<AssociationRoleIF> it2 = asource.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF rsource = it2.next();
        AssociationRoleIF rtarget = 
          builder.makeAssociationRole(atarget,
                                      resolveTopic(builder.getTopicMap(), rsource.getType()),
                                      (rsource.equals(rstart) ? target : copyTopic(targettm, rsource.getPlayer())));
        rtarget = resolveIdentities(rtarget, rsource);
        copyReifier(rtarget, rsource);
      }

      atarget = resolveIdentities(atarget, asource);
      copyReifier(atarget, asource);
    }
    return target;
  }

  private static void copyScope(ScopedIF target, ScopedIF source) {
    Iterator<TopicIF> it = source.getScope().iterator();
    while (it.hasNext())
      target.addTheme(copyTopic(target.getTopicMap(), it.next()));
  }
  
  private static TopicIF resolveTopic(TopicMapIF targetTopicMap,
                                      TopicIF sourceTopic) {
    if (sourceTopic == null) 
      return null;
    else
      return copyTopic(targetTopicMap, sourceTopic);
  }
  
  // returns false if object is a duplicate, true if it is not
  // expects caller to remove duplicate
  @SuppressWarnings("unchecked")
  private static <O extends TMObjectIF> O resolveIdentities(O target, O source) {
    TopicMapIF targettm = target.getTopicMap();
    Iterator<LocatorIF> it = source.getItemIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();

      TMObjectIF object = targettm.getObjectByItemIdentifier(loc);
      if (object != null) {
        if (!equals(target, object)) {
          throw new ConstraintViolationException("Different topic map objects have " +
                                                 "the same source locator (" + loc +
                                                 "): " + target + " and " + object);
        } else {
          target.remove();
          return (O) object; // this is a duplicate
        }
      } else
        target.addItemIdentifier(loc);
    }

    return target;
  }

  private static void copyReifier(ReifiableIF target, ReifiableIF source) {
    TopicIF reifier = source.getReifier();
    if (reifier != null) {
      TopicIF treifier = mergeInto(target.getTopicMap(), reifier);
      target.setReifier(treifier);
    }
  }

  private static void copyReifier(ReifiableIF target, ReifiableIF source, Map<TopicIF, TopicIF> mergemap) {
    TopicIF _sourceReifier = source.getReifier();
    if (_sourceReifier != null) {
      TopicIF targetReifier = target.getReifier();
      TopicIF sourceReifier = resolveTopic(target.getTopicMap(), _sourceReifier, mergemap);
      if (targetReifier == null) {
        if (sourceReifier != null) {
          if (sourceReifier.getReified() != null) {
            mergeInto(target, sourceReifier.getReified());
          } else {
            target.setReifier(sourceReifier);
          }
        }
      } else if (sourceReifier != null) {
        if (!targetReifier.equals(sourceReifier))
          mergeInto(targetReifier, sourceReifier);
      }
    }
  }

  private static TopicIF copyTopic(TopicMapIF targettm, TopicIF source) {
    if (source == null) return null;
    TopicMapBuilderIF builder = targettm.getBuilder();
    TopicIF target = builder.makeTopic();
    return copyIdentifiers(target, source);
  }
  
  /**
   * INTERNAL: Copies all the identifiers from the source to the
   * target topic. Note that this method might cause topics in the
   * target topic map to merge. The source and target are assumed to
   * come from different topic maps.
   */
  public static TopicIF copyIdentifiers(TopicIF target, TopicIF source) {
    TopicMapIF targettm = target.getTopicMap();

    // merging on subject locators
    Iterator<LocatorIF> it = source.getSubjectLocators().iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TopicIF found = targettm.getTopicBySubjectLocator(loc);
      if (found != null) {
        if (!found.equals(target)) {
          mergeInto(found, target);
          target = found;
        }
      } else
        target.addSubjectLocator(loc);
    }

    // merging on subject identifiers
    it = source.getSubjectIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TopicIF found = targettm.getTopicBySubjectIdentifier(loc);

      if (found == null) {
        TMObjectIF f = targettm.getObjectByItemIdentifier(loc);
        if (f instanceof TopicIF)
          found = (TopicIF) f;
      }
      
      if (found != null) {
        if (found != target) {
          mergeInto(found, target);
          target = found;
        }
      }

      // have to copy subject identifier across, in case we merged via item
      // identifier
      target.addSubjectIdentifier(loc);
    }

    // merging on item identifiers
    it = source.getItemIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TMObjectIF f = targettm.getObjectByItemIdentifier(loc);
      if (f != null && !(f instanceof TopicIF))
        throw new ConstraintViolationException("Item identifier " + loc +
                                               " of source topic clashed with"+
                                               " " + f);
      
      TopicIF found = (TopicIF) f;
      if (found == null)
        found = targettm.getTopicBySubjectIdentifier(loc);
      
      if (found != null) {
        if (found != target) {
          mergeInto(found, target);
          target = found;
        }
      }

      // have to copy item identifier across, in case we merged via subject
      // identifier
      target.addItemIdentifier(loc); 
    }
    
    return target;
  }

  public static TopicIF copyIdentifiers(TopicIF target, TopicIF source, Map<TopicIF, TopicIF> mergemap) {
    TopicMapIF targettm = target.getTopicMap();

    // merging subject locators
    Iterator<LocatorIF> it = source.getSubjectLocators().iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TopicIF found = targettm.getTopicBySubjectLocator(loc);
      if (found != null) {
        if (found != target) {
          mergemap.put(source, found);
          mergeInto(found, target);
          target = found;
        }
      } else {
        target.addSubjectLocator(loc);
      }
    }

    // merging subject indicators
    it = source.getSubjectIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TopicIF found = targettm.getTopicBySubjectIdentifier(loc);
      if (found != null) {
        if (found != target) {
          mergemap.put(source, found);
          mergeInto(found, target);
          target = found;
        }
      } else {
        target.addSubjectIdentifier(loc);
      }
    }

    // merging source locators
    it = source.getItemIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      TMObjectIF f = targettm.getObjectByItemIdentifier(loc);
      if (f != null && !(f instanceof TopicIF))
        throw new ConstraintViolationException("Source locator " + loc +
                                               " of source topic clashed with"+
                                               " " + f);
      
      TopicIF found = (TopicIF) f;
      if (found != null) {
        if (found != target) {
          mergemap.put(source, found);
          mergeInto(found, target);
          target = found;
        }
      } else {
        target.addItemIdentifier(loc);
      }
    }
    
    return target;
  }
  
  /**
   * PUBLIC: Merges one topic map into another topic map. The source topic
   * map is left untouched, while its contents are copied into the
   * target topic map. The target topic map is updated accordingly,
   * and no duplicate characteristics should be present after the merge.
   *
   * <p>Merges are done on the basis of subject locators, subject
   * identifiers, item identifiers, and topic names (with scope).
   *
   * @param source topicIF; the source topic map. This is untouched after the
   *            operation.
   * @param target topicIF; the target topic map. This gets new topics
   *    and topic characteristics.
   * @exception throws ConstraintViolationException if two topics
   *    that are to be merged under XTM 1.0 rules have different values
   *    for the 'subject' property, since if they do they cannot
   *    represent the same subject.
   */

  public static void mergeInto(TopicMapIF target, TopicMapIF source)
    throws ConstraintViolationException {
    // Initialization
    Map<TopicIF, TopicIF> mergemap = new HashMap<TopicIF, TopicIF>(); // see INV comment below for enlightenment

    // STEP 1: URI-based merges
    // may find that topics in target should be merged, due to extra
    // information provided by source; in these cases, merge those
    // topics and update mergemap accordingly
    Map<TopicIF, Set<TopicIF>> mergemapRev = new HashMap<TopicIF, Set<TopicIF>>();
    Iterator<TopicIF> it = source.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF sourceT = it.next();
      TopicIF targetT;

      // subject locators
      Iterator<LocatorIF> it2 = new ArrayList<LocatorIF>(sourceT.getSubjectLocators()).iterator();
      while (it2.hasNext()) {
        LocatorIF loc = it2.next();
        targetT = target.getTopicBySubjectLocator(loc);
        if (targetT != null)
          registerMerge(targetT, sourceT, mergemap, mergemapRev);
      }

      // subject identifiers
      it2 = new ArrayList<LocatorIF>(sourceT.getSubjectIdentifiers()).iterator();
      while (it2.hasNext()) {
        LocatorIF ind = it2.next();
        targetT = target.getTopicBySubjectIdentifier(ind);
        if (targetT == null) {
          TMObjectIF object = target.getObjectByItemIdentifier(ind);
          if (object != null && object instanceof TopicIF) {
            targetT = (TopicIF) object;
          }
        }
        if (targetT != null)
          registerMerge(targetT, sourceT, mergemap, mergemapRev);
      }

      // item identifiers
      it2 = new ArrayList<LocatorIF>(sourceT.getItemIdentifiers()).iterator();
      while (it2.hasNext()) {
        LocatorIF loc = it2.next();
        TMObjectIF object = target.getObjectByItemIdentifier(loc);
        if (object != null && object instanceof TopicIF) 
          targetT = (TopicIF) object;
        else
          targetT = target.getTopicBySubjectIdentifier(loc);

        if (targetT != null)
          registerMerge(targetT, sourceT, mergemap, mergemapRev);
      }
    }
    
    //  INV: mergeMap contains sourceT -> targetT mapping for all
    //  topics in source that are to be merged with a topic in target,
    //  based on URIs.  no topics in target need to be merged with
    //  each other.

    mergemapRev = null; // no longer needed; conserve memory
    
    // STEP 3: copy to target
    Map<TopicIF, TopicIF> merged = new HashMap<TopicIF, TopicIF>(mergemap);

    // a) copy unmerged topics
    it = source.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF t2 = it.next();
      if (!mergemap.containsKey(t2)) 
        copyTopic(target, t2, mergemap);
    }
        
    // b) copy characteristics of merged topics (except roles)
    it = merged.keySet().iterator();
    while (it.hasNext()) {
      TopicIF t2 = it.next();
      TopicIF t1 = (TopicIF) merged.get(t2);
      copyCharacteristics(t1, t2, mergemap);
    }
        
    // c) copy associations
    Set<String> assocs = getAssociationKeySet(target.getAssociations());
    Iterator<AssociationIF> associationIterator = source.getAssociations().iterator();
    while (associationIterator.hasNext())
      copyAssociation(target, associationIterator.next(), mergemap, assocs);

    // d) reifier
    // NOTE: the reifier is *not* to be copied, because if a topic is
    // reifying the source topic map that's a different subject from
    // the target topic map, and so if we copied we'd be changing the
    // subject of the topic.
  }

  private static void registerMerge(TopicIF target, TopicIF source,
                                    Map<TopicIF, TopicIF> mergemap, Map<TopicIF, Set<TopicIF>> mergemapRev) {
    if (target.getTopicMap() == null) 
      throw new IllegalArgumentException("Target " + target + " has no topic map");

    // do the merge
    Set<TopicIF> sources = mergemapRev.get(target);
    if (sources == null) {
      sources = new CompactHashSet<TopicIF>();
      mergemapRev.put(target, sources);
    }
    sources.add(source);

    TopicIF origTarget = mergemap.get(source);
    mergemap.put(source, target);

    if (origTarget != null && !origTarget.equals(target)) {
      Iterator<TopicIF> it = mergemapRev.get(origTarget).iterator();
      while (it.hasNext()) {
        TopicIF otherSource = it.next();
        sources.add(otherSource);
        mergemap.put(otherSource, target);
      }
      mergemapRev.remove(origTarget);
      mergeInto(target, origTarget);
    }
  }
  
  private static void copyAssociation(TopicMapIF targettm,
                                      AssociationIF source,
                                      Map<TopicIF, TopicIF> mergemap,
                                      Set<String> assocs) {
    TopicMapBuilderIF builder = targettm.getBuilder();

    AssociationIF target = builder.makeAssociation(resolveTopic(builder.getTopicMap(), source.getType(), mergemap));
    copyScope(target, source, mergemap);

    Iterator<AssociationRoleIF> it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF sourceRole = it.next();
      builder.makeAssociationRole(target,
                                    resolveTopic(builder.getTopicMap(), sourceRole.getType(), mergemap),
                                    resolveTopic(builder.getTopicMap(), sourceRole.getPlayer(), mergemap));
    }

    if (assocs.contains(KeyGenerator.makeAssociationKey(target)))
      target.remove();
    else {
      copyReifier(target, source, mergemap);
      copySourceLocators(target, source);
    }
  }

  private static Set<String> getAssociationKeySet(Collection<AssociationIF> associations) {
    Set<String> assocs = new CompactHashSet<String>();
    Iterator<AssociationIF> it = associations.iterator();
    while (it.hasNext()) {
      AssociationIF assoc = it.next();
      assocs.add(KeyGenerator.makeAssociationKey(assoc));
    }
    return assocs;
  }
    
  // FIXME: note: updates mergemap
  private static TopicIF copyTopic(TopicMapIF targettm, TopicIF source,
                                   Map<TopicIF, TopicIF> mergemap) {
    TopicMapBuilderIF builder =
      targettm.getBuilder();
    TopicIF target = builder.makeTopic();
    mergemap.put(source, target);
    copyCharacteristics(target, source, mergemap);
    
    if (target.getTopicMap() == null) // got merged away (bug #2168)
      return (TopicIF) mergemap.get(source);
    else
      return target;
  }

  // assumes the objects are in different topic maps
  private static void copySourceLocators(TMObjectIF target, TMObjectIF source) {
    Iterator<LocatorIF> it = source.getItemIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF srcloc = it.next();
      try {
        target.addItemIdentifier(srcloc);
      } catch (UniquenessViolationException e) {
        TopicMapIF tm = target.getTopicMap();
        TMObjectIF other = tm.getObjectByItemIdentifier(srcloc);
        if (!equals(target, other))
          throw e;

        // so, they were equal. that means they should merge. so what
        // do we do now? is it enough to transfer the source locators?
        // and what happens if we lose 'target'? surely it's needed
        // elsewhere?
      }
    }
  }
  
  private static void copyCharacteristics(TopicIF target, TopicIF source,
                                          Map<TopicIF, TopicIF> mergemap) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();

    // copy identifiers
    target = copyIdentifiers(target, source, mergemap);

    // copying types
    Iterator<TopicIF> typeIterator = source.getTypes().iterator();
    while (typeIterator.hasNext()) {
      TopicIF sourceType = typeIterator.next();
      target.addType(resolveTopic(target.getTopicMap(), sourceType, mergemap));
    }
        
    // copying base names
    HashMap<String, TopicNameIF> map = new HashMap<String, TopicNameIF>();
    Iterator<TopicNameIF> topicnameIterator = target.getTopicNames().iterator();
    while (topicnameIterator.hasNext()) {
      TopicNameIF bn = topicnameIterator.next();
      String key = KeyGenerator.makeTopicNameKey(bn);
      map.put(key, bn);
    }

    topicnameIterator = source.getTopicNames().iterator();
    while (topicnameIterator.hasNext()) {
      TopicNameIF bn2 = topicnameIterator.next();

      // first copy the type, fixes #409
      TopicIF nametype = mergemap.get(bn2.getType());
      if (nametype == null) {
        nametype = copyTopic(builder.getTopicMap(), bn2.getType());
        mergemap.put(bn2.getType(), nametype);
        copyCharacteristics(nametype, bn2.getType(), mergemap);
      }
      TopicNameIF bn1 = builder.makeTopicName(target, nametype, bn2.getValue());
      copyScope(bn1, bn2, mergemap);

      String key = KeyGenerator.makeTopicNameKey(bn1);
      TopicNameIF dupl = map.get(key);
      if (dupl == null) {
        copyVariants(bn1, bn2, mergemap);
      } else {
        bn1.remove();
        bn1 = dupl;
        copyVariants(bn1, bn2, mergemap);
      }
      copyReifier(bn1, bn2, mergemap);
      copySourceLocators(bn1, bn2);
    }
        
    // copying occurrences
    Set<String> keys = new CompactHashSet<String>();
    Iterator<OccurrenceIF> occurrenceIterator = target.getOccurrences().iterator();
    while (occurrenceIterator.hasNext())
      keys.add(KeyGenerator.makeOccurrenceKey(occurrenceIterator.next()));
        
    occurrenceIterator = source.getOccurrences().iterator();
    while (occurrenceIterator.hasNext()) {
      OccurrenceIF occ2 = occurrenceIterator.next();

      // first copy the type, fixes #409
      TopicIF occtype = mergemap.get(occ2.getType());
      if (occtype == null) {
        occtype = copyTopic(builder.getTopicMap(), occ2.getType());
        mergemap.put(occ2.getType(), occtype);
        copyCharacteristics(occtype, occ2.getType(), mergemap);
      }
      OccurrenceIF occ1 = builder.makeOccurrence(target, occtype, "");      
      CopyUtils.copyOccurrenceData(occ1, occ2);
      copyScope(occ1, occ2, mergemap);
      copyReifier(occ1, occ2, mergemap);

      if (keys.contains(KeyGenerator.makeOccurrenceKey(occ1))) 
        occ1.remove();
      else
        copySourceLocators(occ1, occ2);
    }
        
    // note: roles are not copied; they are left for the
    // association copying, which will take care of them more
    // cleanly than we could do here
  }

  private static void copyScope(ScopedIF target, ScopedIF source,
                                Map<TopicIF, TopicIF> mergemap) {
    Iterator<TopicIF> it = source.getScope().iterator();
    while (it.hasNext()) {
      TopicIF replacement = it.next();
      target.addTheme(resolveTopic(target.getTopicMap(), replacement, mergemap));
    }
  }

  private static TopicIF resolveTopic(TopicMapIF targetTopicMap,
                                      TopicIF sourceTopic,
                                      Map<TopicIF, TopicIF> mergemap) {
    if (sourceTopic == null)
      return null;
    if (mergemap.containsKey(sourceTopic))
      return mergemap.get(sourceTopic);
    else
      return copyTopic(targetTopicMap, sourceTopic, mergemap);
  }

  private static void copyVariants(TopicNameIF target, TopicNameIF source,
                                   Map<TopicIF, TopicIF> mergemap) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();
        
    Iterator<VariantNameIF> it = source.getVariants().iterator();
    while (it.hasNext()) {
      VariantNameIF sv = it.next();
      VariantNameIF tv = builder.makeVariantName(target, sv.getValue(), sv.getDataType(), Collections.emptySet());
      copyScope(tv, sv, mergemap);
			copyReifier(tv, sv, mergemap);
      copySourceLocators(tv, sv);
    }
  }

  /**
   * PUBLIC: Find a topic in the other topic map which would merge
   * with the given topic if that were to be added to the same topic
   * map. Even if there are more topics which would merge only one is
   * returned.
   * @param othertm The topic map to find the corresponding topic in.
   * @param topic A topic in a topic map other than othertm to look up
   *              in othertm.
   * @return The corresponding topic.
   * @since 5.1.3
   */
  public static TopicIF findTopic(TopicMapIF othertm, TopicIF topic) {
    TopicIF other;
    for (LocatorIF si : topic.getSubjectIdentifiers()) {
      other = othertm.getTopicBySubjectIdentifier(si);
      if (other != null)
        return other;
    }
    for (LocatorIF sl : topic.getSubjectLocators()) {
      other = othertm.getTopicBySubjectLocator(sl);
      if (other != null)
        return other;
    }
    for (LocatorIF ii : topic.getItemIdentifiers()) {
      other = (TopicIF) othertm.getObjectByItemIdentifier(ii);
      if (other != null)
        return other;
    }
    return null;
  }
  
  // --- equals methods

  // assumes obj1 and obj2 belong to same TM
  private static boolean equals(TMObjectIF obj1, TMObjectIF obj2) {
    // can't be topics, or we wouldn't be here
    
    if (obj1 instanceof AssociationIF && obj2 instanceof AssociationIF) {
      AssociationIF a1 = (AssociationIF) obj1;
      AssociationIF a2 = (AssociationIF) obj2;

      if (a1.getType() == a2.getType() &&
          a1.getRoles().size() == a2.getRoles().size() &&
          a1.getScope().equals(a2.getScope())) {
        ArrayList<AssociationRoleIF> roles2 = new ArrayList<AssociationRoleIF>(a2.getRoles());
        Iterator<AssociationRoleIF> it1 = a1.getRoles().iterator();
        while (it1.hasNext()) {
          AssociationRoleIF role1 = it1.next();
          Iterator<AssociationRoleIF> it2 = roles2.iterator();
          boolean found = false;
          while (it2.hasNext()) {
            AssociationRoleIF role2 = it2.next();
            if (role2.getPlayer() == role1.getPlayer() &&
                role1.getType() == role2.getType()) {
              roles2.remove(role2);
              found = true;
              break;
            }
          }
          if (!found)
            break;
        }

        return roles2.isEmpty();
      }

    } else if (obj1 instanceof TopicNameIF && obj2 instanceof TopicNameIF) {
      TopicNameIF bn1 = (TopicNameIF) obj1;
      TopicNameIF bn2 = (TopicNameIF) obj2;

      return (bn1.getTopic().equals(bn2.getTopic()) &&
              sameAs(bn1.getValue(), bn2.getValue()) &&
              sameAs(bn1.getType(), bn2.getType()) &&
              sameAs(bn1.getScope(), bn2.getScope()));

    } else if (obj1 instanceof OccurrenceIF && obj2 instanceof OccurrenceIF) {

      OccurrenceIF occ1 = (OccurrenceIF) obj1;
      OccurrenceIF occ2 = (OccurrenceIF) obj2;      
      
      return (occ1.getTopic().equals(occ2.getTopic()) &&
              sameAs(occ1.getValue(), occ2.getValue()) &&
              sameAs(occ1.getDataType(), occ2.getDataType()) &&
              sameAs(occ1.getType(), occ2.getType()) &&
              sameAs(occ1.getScope(), occ2.getScope()));

    }

    return false;
  }

  private static boolean sameAs(Object o1, Object o2) {
    return ((o1 == null && o2 == null) ||
            (o1 != null && o1.equals(o2)));
  }

  private static void moveReified(TopicIF target, TopicIF source) {
    ReifiableIF sreified = source.getReified();
    if (sreified != null) {
      ReifiableIF treified = target.getReified();
      if (treified != null) {
        if (!KeyGenerator.makeKey(sreified).equals(KeyGenerator.makeKey(treified)))
          throw new ConstraintViolationException("Cannot merge topics which " +
                                                 "reify different objects");

        // FIXME: must verify that parents are equal
        
        mergeInto(treified, sreified);
        
      } else {
        sreified.setReifier(null);
        sreified.setReifier(target);
      }
    }
  }

  private static void moveReifier(ReifiableIF target, ReifiableIF source) {
    TopicIF sreifier = source.getReifier();
    if (sreifier != null) {
      TopicIF treifier = target.getReifier();
      if (treifier != null) {
        source.setReifier(null);
        mergeInto(treifier, sreifier);
      } else {
        source.setReifier(null);
        target.setReifier(sreifier);
      }
    }
  }

  private static void notifyTransaction(TMObjectIF source, TMObjectIF target) {
    TopicMapStoreIF store = target.getTopicMap().getStore();
    if (store instanceof RDBMSTopicMapStore) {
      ((RDBMSTopicMapStore) store).merged(source, target);
    }
  }
}
