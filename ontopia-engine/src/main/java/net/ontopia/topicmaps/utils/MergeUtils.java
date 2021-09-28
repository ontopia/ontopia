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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.DeciderUtils;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.CompactHashSet;
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
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.impl.utils.ReificationUtils;

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
    if (CollectionUtils.overlaps(t1.getSubjectLocators(), t2.getSubjectLocators()))
      return true;
    
    // check subject indicators and source locators
    if (CollectionUtils.overlaps(t1.getSubjectIdentifiers(), t2.getSubjectIdentifiers()) ||
        CollectionUtils.overlaps(t1.getItemIdentifiers(), t2.getSubjectIdentifiers()))
      return true;
    if (CollectionUtils.overlaps(t1.getItemIdentifiers(), t2.getItemIdentifiers()) ||
        CollectionUtils.overlaps(t1.getSubjectIdentifiers(), t2.getItemIdentifiers()))
      return true;

    // should merge if they reify the same object
    ReifiableIF r1 = t1.getReified();
    ReifiableIF r2 = t2.getReified();
    if (r1 != null && ObjectUtils.equals(r1, r2))
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
    if (target == source)
      throw new IllegalArgumentException("Cannot merge topic with itself!");

    // move reified
    moveReified(target, source);

    // replace source by target throughout
    replaceTopics(target, source);

    // remove subject locators from source
    List sublocs = new ArrayList(source.getSubjectLocators());
    Iterator it = sublocs.iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
      source.removeSubjectLocator(loc);
    }

    // remove subject indicators from source
    List subinds = new ArrayList(source.getSubjectIdentifiers());
    it = subinds.iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
      source.removeSubjectIdentifier(loc);
    }

    // remove item identifiers from source
    List itemids = new ArrayList(source.getItemIdentifiers());
    it = itemids.iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
      source.removeItemIdentifier(loc);
    }

    // add subject locators to target
    it = sublocs.iterator();
    while (it.hasNext()) {    
      LocatorIF loc = (LocatorIF) it.next();
      target.addSubjectLocator(loc);
    }

    // add subject indicators to target
    it = subinds.iterator();
    while (it.hasNext()) {    
      LocatorIF loc = (LocatorIF) it.next();
      target.addSubjectIdentifier(loc);
    }

    // add item identifiers to target
    it = itemids.iterator();
    while (it.hasNext()) {    
      LocatorIF loc = (LocatorIF) it.next();
      target.addItemIdentifier(loc);
    }
      
    // copying types
    it = source.getTypes().iterator();
    while (it.hasNext())
      target.addType((TopicIF) it.next());
        
    // copying base names
    Map map = buildKeyMap(target.getTopicNames());

    List sbns = new ArrayList(source.getTopicNames());
    it = sbns.iterator();
    while (it.hasNext()) {
      TopicNameIF sourcebn = (TopicNameIF) it.next();
      String key = KeyGenerator.makeTopicNameKey(sourcebn);
      TopicNameIF targetbn = (TopicNameIF) map.get(key);

      if (targetbn == null) {
        targetbn = CopyUtils.copyTopicName(target, sourcebn);
        moveReifier(targetbn, sourcebn);
        sourcebn.remove();
      } else
        mergeInto(targetbn, sourcebn);
    }

    // copying occurrences
    map = buildKeyMap(target.getOccurrences());
    it = new ArrayList(source.getOccurrences()).iterator();
    while (it.hasNext()) {
      OccurrenceIF sourceoc = (OccurrenceIF) it.next();
      OccurrenceIF targetoc = (OccurrenceIF) map.get(KeyGenerator.makeOccurrenceKey(sourceoc));
      if (targetoc == null) {
        targetoc = CopyUtils.copyOccurrence(target, sourceoc);
        moveReifier(targetoc, sourceoc);
        sourceoc.remove();
      } else
        mergeInto(targetoc, sourceoc);
    }

    // copying roles
    Set keys = new CompactHashSet();
    it = target.getRoles().iterator();
    while (it.hasNext())
      keys.add(KeyGenerator.makeAssociationKey(
                 ((AssociationRoleIF) it.next()).getAssociation()));
    
    it = new ArrayList(source.getRoles()).iterator();
    while (it.hasNext()) {
      AssociationRoleIF ar = (AssociationRoleIF) it.next();
      ar.setPlayer(target);

      String key = KeyGenerator.makeAssociationKey(ar.getAssociation());
      if (keys.contains(key)) {
        ar.getAssociation().remove();
        // ISSUE: should we move any reifier over to the duplicate?
      }
    }

    // removing source
    source.remove();
  }

  private static Map buildKeyMap(Collection objects) {
    Map map = new HashMap();
    Iterator it = objects.iterator();
    while (it.hasNext()) {
      ReifiableIF object = (ReifiableIF) it.next();
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

  private static void replaceTopicType(Collection objects, TopicIF t1) {
    Iterator it = objects.iterator();
    while (it.hasNext()) {
      TypedIF object = (TypedIF) it.next();
      object.setType(t1);
    }
  }

  /**
   * INTERNAL: Replace source by target as the type of objects.
   */
  private static void replaceTopicTypes(Collection objects,
                                        TopicIF target,
                                        TopicIF source) {
    Iterator it = objects.iterator();
    while (it.hasNext()) {
      TopicIF object = (TopicIF) it.next();
      object.removeType(source);
      object.addType(target);
    }
  }

  private static void replaceTopicInScope(Collection objects, TopicIF t1,
                                          TopicIF t2) {
    Iterator it = objects.iterator();
    while (it.hasNext()) {
      ScopedIF object = (ScopedIF) it.next();
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
    Iterator it = new ArrayList(source.getVariants()).iterator();
    while (it.hasNext()) {
      VariantNameIF sourcevn = (VariantNameIF) it.next();
      VariantNameIF targetvn = CopyUtils.copyVariant(target, sourcevn);
      moveReifier(targetvn, sourcevn);
      sourcevn.remove();
    }
    moveReifier(target, source);
    moveItemIdentifiers(target, source);
    source.remove();
  }

  private static void moveItemIdentifiers(TMObjectIF target, TMObjectIF source) {
    Iterator it = new ArrayList(source.getItemIdentifiers()).iterator();
    while (it.hasNext()) {
      LocatorIF itemid = (LocatorIF) it.next();
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
    Iterator it = target.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      keys.put(KeyGenerator.makeAssociationRoleKey(role), role);
    }

    // merge the roles
    it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF srole = (AssociationRoleIF) it.next();
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
    return mergeInto(targettm, source, DeciderUtils.getTrueDecider());
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
                                  DeciderIF decider) {
    if (source.getTopicMap() == targettm)
      return source;

    TopicMapBuilderIF builder = targettm.getBuilder();
    TopicIF target = copyTopic(targettm, source);

    // copying types
    Iterator it = source.getTypes().iterator();
    while (it.hasNext()) 
      target.addType(copyTopic(targettm, (TopicIF) it.next()));

    // copying base names
    it = source.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bnsource = (TopicNameIF) it.next();
      if (!decider.ok(bnsource))
        continue;
      TopicNameIF bntarget = builder.makeTopicName(target, 
                                                 resolveTopic(builder.getTopicMap(), bnsource.getType()), 
                                                 bnsource.getValue());
      copyScope(bntarget, bnsource);

      Iterator it2 = bnsource.getVariants().iterator();
      while (it2.hasNext()) {
        VariantNameIF vnsource = (VariantNameIF) it2.next();
        if (!decider.ok(vnsource))
          continue;
        
        VariantNameIF vntarget = builder.makeVariantName(bntarget, vnsource.getValue(), vnsource.getDataType());
        copyScope(vntarget, vnsource);
        vntarget = (VariantNameIF)resolveIdentities(vntarget, vnsource);
        copyReifier(vntarget, vnsource);
      }
      
      bntarget = (TopicNameIF)resolveIdentities(bntarget, bnsource);
      copyReifier(bntarget, bnsource);
    }
    
    // copying occurrences
    it = source.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF osource = (OccurrenceIF) it.next();
      if (!decider.ok(osource))
        continue;
      OccurrenceIF otarget = builder.makeOccurrence(target, 
                                                    resolveTopic(builder.getTopicMap(), osource.getType()), 
                                                    "");
      CopyUtils.copyOccurrenceData(otarget, osource);
      copyScope(otarget, osource);
      otarget = (OccurrenceIF)resolveIdentities(otarget, osource);
      copyReifier(otarget, osource);
    }
    
    // copying associations
    it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF rstart = (AssociationRoleIF) it.next();
      if (!decider.ok(rstart))
        continue;
      AssociationIF asource = rstart.getAssociation();

      AssociationIF atarget = builder.makeAssociation(resolveTopic(builder.getTopicMap(), asource.getType()));
      copyScope(atarget, asource);

      Iterator it2 = asource.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF rsource = (AssociationRoleIF) it2.next();
        AssociationRoleIF rtarget = 
          builder.makeAssociationRole(atarget,
                                      resolveTopic(builder.getTopicMap(), rsource.getType()),
                                      (rsource == rstart ? target : copyTopic(targettm, rsource.getPlayer())));
        rtarget = (AssociationRoleIF)resolveIdentities(rtarget, rsource);
        copyReifier(rtarget, rsource);
      }

      atarget = (AssociationIF)resolveIdentities(atarget, asource);
      copyReifier(atarget, asource);
    }
    return target;
  }

  private static void copyScope(ScopedIF target, ScopedIF source) {
    Iterator it = source.getScope().iterator();
    while (it.hasNext())
      target.addTheme(copyTopic(target.getTopicMap(), (TopicIF) it.next()));
  }
  
  private static TopicIF resolveTopic(TopicMapIF targetTopicMap,
                                      TopicIF sourceTopic) {
    if (sourceTopic == null) 
      return null;
    else
      return copyTopic(targetTopicMap, sourceTopic);
  }
  
  private static void copyType(TypedIF target, TypedIF source) {
    TopicIF type = (TopicIF) source.getType();
    if (type == null)
      target.setType(null);
    else
      target.setType(copyTopic(target.getTopicMap(), type));
  }

  // returns false if object is a duplicate, true if it is not
  // expects caller to remove duplicate
  private static TMObjectIF resolveIdentities(TMObjectIF target, TMObjectIF source) {
    TopicMapIF targettm = target.getTopicMap();
    Iterator it = source.getItemIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();

      TMObjectIF object = targettm.getObjectByItemIdentifier(loc);
      if (object != null) {
        if (!equals(target, object)) {
          throw new ConstraintViolationException("Different topic map objects have " +
                                                 "the same source locator (" + loc +
                                                 "): " + target + " and " + object);
        } else {
          target.remove();
          return object; // this is a duplicate
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

  private static void copyReifier(ReifiableIF target, ReifiableIF source, Map mergemap) {
    TopicIF _sourceReifier = source.getReifier();
    if (_sourceReifier != null) {
      TopicIF targetReifier = target.getReifier();
      TopicIF sourceReifier = resolveTopic(target.getTopicMap(), _sourceReifier, mergemap);
      if (targetReifier == null) {
        if (sourceReifier != null) target.setReifier(sourceReifier);
      } else if (sourceReifier != null) {
        if (targetReifier != sourceReifier)
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
    Iterator it = source.getSubjectLocators().iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
      TopicIF found = targettm.getTopicBySubjectLocator(loc);
      if (found != null) {
        if (found != target) {
          mergeInto(found, target);
          target = found;
        }
      } else
        target.addSubjectLocator(loc);
    }

    // merging on subject identifiers
    it = source.getSubjectIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
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
      LocatorIF loc = (LocatorIF) it.next();
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

  public static TopicIF copyIdentifiers(TopicIF target, TopicIF source, Map mergemap) {
    TopicMapIF targettm = target.getTopicMap();

    // merging subject locators
    Iterator it = source.getSubjectLocators().iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
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
      LocatorIF loc = (LocatorIF) it.next();
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
      LocatorIF loc = (LocatorIF) it.next();
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
    Map mergemap = new HashMap(); // see INV comment below for enlightenment
    TopicMapBuilderIF builder = target.getBuilder();

    // STEP 1: URI-based merges
    // may find that topics in target should be merged, due to extra
    // information provided by source; in these cases, merge those
    // topics and update mergemap accordingly
    Map mergemapRev = new HashMap();
    Iterator it = source.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF sourceT = (TopicIF) it.next();
      TopicIF targetT;

      // subject locators
      Iterator it2 = new ArrayList(sourceT.getSubjectLocators()).iterator();
      while (it2.hasNext()) {
        LocatorIF loc = (LocatorIF) it2.next();
        targetT = target.getTopicBySubjectLocator(loc);
        if (targetT != null)
          registerMerge(targetT, sourceT, mergemap, mergemapRev);
      }

      // subject identifiers
      it2 = new ArrayList(sourceT.getSubjectIdentifiers()).iterator();
      while (it2.hasNext()) {
        LocatorIF ind = (LocatorIF) it2.next();
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
      it2 = new ArrayList(sourceT.getItemIdentifiers()).iterator();
      while (it2.hasNext()) {
        LocatorIF loc = (LocatorIF) it2.next();
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
    Map merged = new HashMap(mergemap);

    // a) copy unmerged topics
    it = source.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF t2 = (TopicIF) it.next();
      if (!mergemap.containsKey(t2)) 
        copyTopic(target, t2, mergemap);
    }
        
    // b) copy characteristics of merged topics (except roles)
    it = merged.keySet().iterator();
    while (it.hasNext()) {
      TopicIF t2 = (TopicIF) it.next();
      TopicIF t1 = (TopicIF) merged.get(t2);
      copyCharacteristics(t1, t2, mergemap);
    }
        
    // c) copy associations
    Set assocs = getAssociationKeySet(target.getAssociations());
    it = source.getAssociations().iterator();
    while (it.hasNext())
      copyAssociation(target, (AssociationIF) it.next(), mergemap, assocs);

    // d) reifier
    // NOTE: the reifier is *not* to be copied, because if a topic is
    // reifying the source topic map that's a different subject from
    // the target topic map, and so if we copied we'd be changing the
    // subject of the topic.
  }

  private static void registerMerge(TopicIF target, TopicIF source,
                                    Map mergemap, Map mergemapRev) {
    if (target.getTopicMap() == null) 
      throw new IllegalArgumentException("Target " + target + " has no topic map");

    // do the merge
    Set sources = (Set) mergemapRev.get(target);
    if (sources == null) {
      sources = new CompactHashSet();
      mergemapRev.put(target, sources);
    }
    sources.add(source);

    TopicIF origTarget = (TopicIF) mergemap.get(source);
    mergemap.put(source, target);

    if (origTarget != null && !origTarget.equals(target)) {
      Iterator it = ((Set) mergemapRev.get(origTarget)).iterator();
      while (it.hasNext()) {
        TopicIF otherSource = (TopicIF) it.next();
        sources.add(otherSource);
        mergemap.put(otherSource, target);
      }
      mergemapRev.remove(origTarget);
      mergeInto(target, origTarget);
    }
  }
  
  private static void copyAssociation(TopicMapIF targettm,
                                      AssociationIF source,
                                      Map mergemap,
                                      Set assocs) {
    TopicMapBuilderIF builder = targettm.getBuilder();

    AssociationIF target = builder.makeAssociation(resolveTopic(builder.getTopicMap(), source.getType(), mergemap));
    copyScope(target, source, mergemap);

    Iterator it = source.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF sourceRole = (AssociationRoleIF) it.next();
      AssociationRoleIF targetRole = 
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

  private static Set getAssociationKeySet(Collection associations) {
    Set assocs = new CompactHashSet();
    Iterator it = associations.iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();
      assocs.add(KeyGenerator.makeAssociationKey(assoc));
    }
    return assocs;
  }
    
  // FIXME: note: updates mergemap
  private static TopicIF copyTopic(TopicMapIF targettm, TopicIF source,
                                   Map mergemap) {
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
    Iterator it = source.getItemIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF srcloc = (LocatorIF) it.next();
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
                                          Map mergemap) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();

    // copy identifiers
    target = copyIdentifiers(target, source, mergemap);

    // copying types
    Iterator it = source.getTypes().iterator();
    while (it.hasNext()) {
      TopicIF sourceType = (TopicIF) it.next();
      target.addType(resolveTopic(target.getTopicMap(), sourceType, mergemap));
    }
        
    // copying base names
    HashMap map = new HashMap();
    it = target.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();
      String key = KeyGenerator.makeTopicNameKey(bn);
      map.put(key, bn);
    }

    it = source.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bn2 = (TopicNameIF) it.next();

      // first copy the type, fixes #409
      TopicIF nametype = (TopicIF)mergemap.get(bn2.getType());
      if (nametype == null) {
        nametype = copyTopic(builder.getTopicMap(), bn2.getType());
        mergemap.put(bn2.getType(), nametype);
        copyCharacteristics(nametype, bn2.getType(), mergemap);
      }
      TopicNameIF bn1 = builder.makeTopicName(target, nametype, bn2.getValue());
      copyScope(bn1, bn2, mergemap);

      String key = KeyGenerator.makeTopicNameKey(bn1);
      TopicNameIF dupl = (TopicNameIF) map.get(key);
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
    Set keys = new CompactHashSet();
    it = target.getOccurrences().iterator();
    while (it.hasNext())
      keys.add(KeyGenerator.makeOccurrenceKey((OccurrenceIF) it.next()));
        
    it = source.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ2 = (OccurrenceIF) it.next();

      // first copy the type, fixes #409
      TopicIF occtype = (TopicIF)mergemap.get(occ2.getType());
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
                                Map mergemap) {
    Iterator it = source.getScope().iterator();
    while (it.hasNext()) {
      TopicIF replacement = (TopicIF) it.next();
      target.addTheme(resolveTopic(target.getTopicMap(), replacement, mergemap));
    }
  }

  private static TopicIF resolveTopic(TopicMapIF targetTopicMap,
                                      TopicIF sourceTopic,
                                      Map mergemap) {
    if (sourceTopic == null)
      return null;
    if (mergemap.containsKey(sourceTopic))
      return (TopicIF) mergemap.get(sourceTopic);
    else
      return copyTopic(targetTopicMap, sourceTopic, mergemap);
  }

  private static void copyType(TypedIF target, TypedIF source, Map mergemap) {
    TopicIF sourceType = source.getType();
    if (sourceType != null)
      target.setType(resolveTopic(target.getTopicMap(), sourceType, mergemap));
  }
    
  private static void copyVariants(TopicNameIF target, TopicNameIF source,
                                   Map mergemap) {
    TopicMapBuilderIF builder = target.getTopicMap().getBuilder();
        
    Iterator it = source.getVariants().iterator();
    while (it.hasNext()) {
      VariantNameIF sv = (VariantNameIF) it.next();
      VariantNameIF tv = builder.makeVariantName(target, sv.getValue(), sv.getDataType());
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
        ArrayList roles2 = new ArrayList(a2.getRoles());
        Iterator it1 = a1.getRoles().iterator();
        while (it1.hasNext()) {
          AssociationRoleIF role1 = (AssociationRoleIF) it1.next();
          Iterator it2 = roles2.iterator();
          boolean found = false;
          while (it2.hasNext()) {
            AssociationRoleIF role2 = (AssociationRoleIF) it2.next();
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
        ReificationUtils.reify(sreified, target);
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
        ReificationUtils.reify(target, sreifier);
      }
    }
  }
}
