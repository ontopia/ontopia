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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.CompactHashSet;

/**
 * INTERNAL: The rdbms topic implementation.
 */
public class Topic extends TMObject implements TopicIF {
  
  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------
  
  protected static final int LF_subjects = 2;
  protected static final int LF_indicators = 3;
  protected static final int LF_types = 4;
  protected static final int LF_names = 5;
  protected static final int LF_occurrences = 6;
  protected static final int LF_roles = 7; // Query field?
  protected static final int LF_reified = 8;
  protected static final String[] fields = {"sources", "topicmap", "subjects",
                                            "indicators", "types", "names",
                                            "occurs", "roles", "reified"};
  
  public void detach() {
    detachCollectionField(LF_sources);
    detachField(LF_topicmap);
    detachField(LF_reified);
    detachCollectionField(LF_subjects);
    detachCollectionField(LF_indicators);
    detachCollectionField(LF_types);
    detachCollectionField(LF_names);
    detachCollectionField(LF_occurrences);
    detachCollectionField(LF_roles);
  }
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------
  
  public static final String CLASS_INDICATOR = "T";
  
  public Topic() {
  }
  
  public Topic(TransactionIF txn) {
    super(txn);
  }
  
  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------
  
  public int _p_getFieldCount() {
    return fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------
  
  public String getClassIndicator() {
    return CLASS_INDICATOR;
  }
  
  public String getObjectId() {
    return (id == null ? null : CLASS_INDICATOR + id.getKey(0));
  }
  
  /**
   * INTERNAL: Sets the topic map that the object belongs to. [parent]
   */
  protected void setTopicMap(TopicMap topicmap) {
    // Notify transaction
    transactionChanged(topicmap);
    valueChanged(LF_topicmap, topicmap, true);
    
    // Inform topic names
    for (TopicName topicname : this.<TopicName>loadCollectionField(LF_names)) {
      topicname.setTopicMap(topicmap);
    }
    // Inform occurrences
    for (Occurrence occurrence : this.<Occurrence>loadCollectionField(LF_occurrences)) {
      occurrence.setTopicMap(topicmap);
    }
  }
  
  // ---------------------------------------------------------------------------
  // TopicIF implementation
  // ---------------------------------------------------------------------------

  public Collection<LocatorIF> getSubjectLocators() {
    return this.<LocatorIF>loadCollectionField(LF_subjects);
  }

  public void addSubjectLocator(LocatorIF subject_locator)
    throws ConstraintViolationException {
    if (subject_locator == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Notify topic map
    TopicMap tm = (TopicMap)getTopicMap();
    if (tm == null)
      throw new ConstraintViolationException("Cannot modify subject locator when topic isn't attached to a topic map.");
    
    // Check to see if subject is already a subject locator of this topic.
    Collection<LocatorIF> subjects = this.<LocatorIF>loadCollectionField(LF_subjects);
    if (subjects.contains(subject_locator)) return;
    
    if (!(subject_locator instanceof SubjectLocator))
      subject_locator = new SubjectLocator(subject_locator);
    
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_SUBJECTLOCATOR, subject_locator, null);    
    // Notify transaction
    valueAdded(LF_subjects, subject_locator, true);
  }

  public void removeSubjectLocator(LocatorIF subject_locator) {
    if (subject_locator == null) throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Notify topic map
    TopicMap tm = (TopicMap)getTopicMap();
    if (tm == null)
      throw new ConstraintViolationException("Cannot modify subject locator when topic isn't attached to a topic map.");
    
    // Check to see if subject locator is a subject locator of this topic.
    Collection<LocatorIF> subjects = this.<LocatorIF>loadCollectionField(LF_subjects);
    if (!subjects.contains(subject_locator)) return;
    
    if (!(subject_locator instanceof SubjectLocator))
      subject_locator = new SubjectLocator(subject_locator);
    
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_SUBJECTLOCATOR, null, subject_locator);    
    // Notify transaction
    valueRemoved(LF_subjects, subject_locator, true);
  }

  public Collection<LocatorIF> getSubjectIdentifiers() {
    return this.<LocatorIF>loadCollectionField(LF_indicators);
  }

  public void addSubjectIdentifier(LocatorIF subject_indicator)
    throws ConstraintViolationException {
    if (subject_indicator == null) throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Notify topic map
    TopicMap tm = (TopicMap)getTopicMap();
    if (tm == null)
      throw new ConstraintViolationException("Cannot modify subject indicator when topic isn't attached to a topic map.");
    
    // Check to see if subject is already a subject indicator of this topic.
    Collection<LocatorIF> indicators = this.<LocatorIF>loadCollectionField(LF_indicators);
    if (indicators.contains(subject_indicator)) return;
    
    if (!(subject_indicator instanceof SubjectIndicatorLocator))
      subject_indicator = new SubjectIndicatorLocator(subject_indicator);
    
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_SUBJECTIDENTIFIER, subject_indicator, null);    
    // Notify transaction
    valueAdded(LF_indicators, subject_indicator, true);
  }

  public void removeSubjectIdentifier(LocatorIF subject_indicator) {
    if (subject_indicator == null) throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Notify topic map
    TopicMap tm = (TopicMap)getTopicMap();
    if (tm == null)
      throw new ConstraintViolationException("Cannot modify subject indicator when topic isn't attached to a topic map.");
    
    // Check to see if subject indicator is a subject indicator of this topic.
    Collection<LocatorIF> indicators = this.<LocatorIF>loadCollectionField(LF_indicators);
    if (!indicators.contains(subject_indicator)) return;
    
    if (!(subject_indicator instanceof SubjectIndicatorLocator))
      subject_indicator = new SubjectIndicatorLocator(subject_indicator);
    
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_SUBJECTIDENTIFIER, null, subject_indicator);    
    // Notify transaction
    valueRemoved(LF_indicators, subject_indicator, true);
  }
  
  public Collection<TopicNameIF> getTopicNames() {
    return this.<TopicNameIF>loadCollectionField(LF_names);
  }
  
  public Collection<TopicNameIF> getTopicNamesByType(TopicIF type) {
    return ((TopicMap)getTopicMap()).getTopicNamesByType(this, type);
  }
  
  protected void addTopicName(TopicNameIF name) {
    if (name == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Check to see if name is already a member of this topic
    if (name.getTopic() == this) return;
    // Check if used elsewhere.
    if (name.getTopic() != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_TOPICNAME, name, null);    
    // Set topic property
    ((TopicName)name).setTopic(this);
    // Notify transaction
    valueAdded(LF_names, name, false);
  }
  
  protected void removeTopicName(TopicNameIF name) {
    if (name == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Check to see if name is not a member of this topic
    if (name.getTopic() != this) return;
    
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_TOPICNAME, null, name);    
    // Unset topic property
    ((TopicName)name).setTopic(null);
    // Notify transaction
    valueRemoved(LF_names, name, false);
  }
  
  public Collection<OccurrenceIF> getOccurrences() {
    return this.<OccurrenceIF>loadCollectionField(LF_occurrences);
  }
  
  public Collection<OccurrenceIF> getOccurrencesByType(TopicIF type) {
    return ((TopicMap)getTopicMap()).getOccurrencesByType(this, type);
  }
  
  protected void addOccurrence(OccurrenceIF occurrence) {
    if (occurrence == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Check to see if occurrence is already a member of this topic
    if (occurrence.getTopic() == this)
      return;
    // Check if used elsewhere.
    if (occurrence.getTopic() != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_OCCURRENCE, occurrence, null);    
    // Set topic property
    ((Occurrence)occurrence).setTopic(this);
    // Notify transaction
    valueAdded(LF_occurrences, occurrence, false);
  }
  
  protected void removeOccurrence(OccurrenceIF occurrence) {
    if (occurrence == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Check to see if occurrence is not a member of this topic
    if (occurrence.getTopic() != this) return;
    
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_OCCURRENCE, null, occurrence);    
    // Unset topic property
    ((Occurrence)occurrence).setTopic(null);
    // Notify transaction
    valueRemoved(LF_occurrences, occurrence, false);
  }
  
  public Collection<AssociationRoleIF> getRoles() {
    return this.<AssociationRoleIF>loadCollectionField(LF_roles);
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    if (roletype == null)
      throw new NullPointerException("Role type cannot be null.");
    // if roles already loaded filter by role type
    if (isLoaded(LF_roles)) {
      Collection<AssociationRoleIF> roles = (Collection<AssociationRoleIF>)
        getValue(LF_roles);
      if (roles.isEmpty())
        return (Collection<AssociationRoleIF>) Collections.EMPTY_SET;
      
      Collection<AssociationRoleIF> result =
        new CompactHashSet<AssociationRoleIF>();
      for (AssociationRoleIF role : roles) {
        if (role.getType() == roletype)
          result.add(role);
      }
      return result;
    } else {
      // lookup roles by type
      TopicMap tm = (TopicMap)getTopicMap();
      if (tm == null)
        throw new ConstraintViolationException("Cannot retrieve roles by type when topic isn't attached to a topic map.");
      return tm.getRolesByType(this, roletype);
    }
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype,
                                                      TopicIF assoc_type) {
    if (roletype == null)
      throw new NullPointerException("Role type cannot be null.");
    if (assoc_type == null)
      throw new NullPointerException("Association type cannot be null.");
    // if roles already loaded filter by role type
    if (isLoaded(LF_roles)) {
      Collection<AssociationRoleIF> roles = (Collection<AssociationRoleIF>)
        getValue(LF_roles);
      if (roles.isEmpty())
        return (Collection<AssociationRoleIF>) Collections.EMPTY_SET;
      
      Collection<AssociationRoleIF> result =
        new CompactHashSet<AssociationRoleIF>();
      for (AssociationRoleIF role : roles) {
        if (role.getType() == roletype) {
          AssociationIF assoc = role.getAssociation();
          if (assoc != null && assoc.getType() == assoc_type)
            result.add(role);
        }
      }
      return result;
    } else {
      // lookup roles by type
      if (roletype == null) {
        TopicMap tm = (TopicMap) getTopicMap();
        if (tm == null)
          throw new ConstraintViolationException("Cannot retrieve roles by type when topic isn't attached to a topic map.");
        return tm.getRolesByType(this, roletype, assoc_type);
        
      } else {
        TopicMap tm = (TopicMap)roletype.getTopicMap();
        return tm.getRolesByType(this, roletype, assoc_type);
      }
    }
  }
  
  public Collection<AssociationIF> getAssociations() {
    return ((TopicMap)getTopicMap()).getAssocations(this);
  }

  public Collection<AssociationIF> getAssociationsByType(TopicIF type) {
    return ((TopicMap)getTopicMap()).getAssociationsByType(this, type);
  }
  
  public void merge(TopicIF topic) {
    CrossTopicMapException.check(topic, this);
    net.ontopia.topicmaps.utils.MergeUtils.mergeInto(this, topic);
  } 
  
  /**
   * INTERNAL: Adds the association role to the set of association
   * roles in which the topic participates.
   */
  protected void addRole(AssociationRoleIF assoc_role) {    
    // Notify transaction
    valueAdded(LF_roles, assoc_role, false);
  }
  
  /**
   * INTERNAL: Removes the association role from the set of
   * association roles in which the topic participates.
   */
  protected void removeRole(AssociationRoleIF assoc_role) {
    // Notify transaction
    valueRemoved(LF_roles, assoc_role, false);
  }

  public void remove() {
    TopicMap topicmap = (TopicMap)getTopicMap();
    if (topicmap != null)
      topicmap.removeTopic(this);
  }
  
  public Collection<TopicIF> getTypes() {
    return this.<TopicIF>loadCollectionField(LF_types);
  }
  
  public void addType(TopicIF type) {
    if (type == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(type, this);    
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_TYPE, type, null);
    // Notify transaction
    valueAdded(LF_types, type, true);
  }
  
  public void removeType(TopicIF type) {
    if (type == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(type, this);    
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_TYPE, null, type);
    // Notify transaction
    valueRemoved(LF_types, type, true);
  }

  public ReifiableIF getReified() {
    String reifiedId = this.<String>loadField(Topic.LF_reified);
    if (reifiedId == null)
      return null;
    return (ReifiableIF) getTopicMap().getObjectById(reifiedId);
  }

  protected void setReified(ReifiableIF reified) {
    ReifiableIF oldReified = getReified();
    if (!Objects.equals(oldReified, reified)) {
      String reifiedId = (reified == null ? null : reified.getObjectId());
      valueChanged(LF_reified, reifiedId, true);
    }
  }
  
  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------
  
  public String toString() {
    return ObjectStrings.toString("rdbms.Topic", (TopicIF) this);
  }

  @Override
  public void syncAfterMerge(IdentityIF source, IdentityIF target) {
    syncFieldsAfterMerge(source, target, LF_types, LF_names, LF_occurrences, LF_roles, LF_reified);
  }
}
