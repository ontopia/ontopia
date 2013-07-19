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

package net.ontopia.topicmaps.impl.basic;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

import net.ontopia.utils.UniqueSet;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
  
/**
 * INTERNAL: The basic topic implementation.
 */

public class Topic extends TMObject implements TopicIF {

  static final long serialVersionUID = 6846760964906826812L;

  protected Set<LocatorIF> subjects;
  protected Set<LocatorIF> indicators;
  protected ReifiableIF reified;

  protected UniqueSet<TopicIF> scope;
  protected UniqueSet<TopicIF> types;
  protected Set<TopicNameIF> names;
  protected Set<OccurrenceIF> occurs;
  protected Set<AssociationRoleIF> roles;

  private static final Comparator<AssociationRoleIF> rolecomp = new RoleComparator();

  protected Topic(TopicMap tm) {
    super(tm);
	Set<TopicIF> empty = Collections.emptySet();
    types = topicmap.setpool.get(empty);
    names = topicmap.cfactory.makeSmallSet();
    occurs = topicmap.cfactory.makeSmallSet();
    roles = topicmap.cfactory.makeSmallSet();
  }
  
  // -----------------------------------------------------------------------------
  // TopicIF implementation
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Sets the topic map that the object belongs to. [parent]
   */
  void setTopicMap(TopicMap parent) {
    // (De)reference pooled sets
    if (parent == null) {
      if (scope != null)
        topicmap.setpool.dereference(scope);
      topicmap.setpool.dereference(types);
    } else {
      if (scope != null)
        scope = topicmap.setpool.get(scope);
      types = topicmap.setpool.get(types);
    }
    // Set parent
    this.parent = parent;
  }

  public Collection<LocatorIF> getSubjectLocators() {
    if (subjects == null)
      return Collections.emptySet();
    else
      return Collections.unmodifiableSet(subjects);
  }

  public void addSubjectLocator(LocatorIF subject_locator) throws ConstraintViolationException {
    if (subject_locator == null)
      throw new NullPointerException("null is not a valid argument.");
    // Notify topic map
    if (!isConnected())
      throw new ConstraintViolationException("Cannot modify subject locator when topic isn't attached to a topic map.");
    if (subjects == null)
      subjects = topicmap.cfactory.makeSmallSet();    
    // Check to see if subject is already a subject locator of this topic.
    else if (subjects.contains(subject_locator))
      return;
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_SUBJECTLOCATOR, subject_locator, null);    
    // Modify property
    subjects.add(subject_locator);
  }

  public void removeSubjectLocator(LocatorIF subject_locator) {
    if (subject_locator == null)
      throw new NullPointerException("null is not a valid argument.");
    // Notify topic map
    if (!isConnected())
      throw new ConstraintViolationException("Cannot modify subject locator when topic isn't attached to a topic map.");
    // Check to see if subject locator is a subject locator of this topic.
    if (subjects == null || !subjects.contains(subject_locator))
      return;
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_SUBJECTLOCATOR, null, subject_locator);    
    // Modify property
    subjects.remove(subject_locator);
  }

  public Collection<LocatorIF> getSubjectIdentifiers() {
    if (indicators == null)
      return Collections.emptySet();
    else
      return Collections.unmodifiableSet(indicators);
  }

  public void addSubjectIdentifier(LocatorIF subject_indicator) throws ConstraintViolationException {
    if (subject_indicator == null)
      throw new NullPointerException("null is not a valid argument.");
    // Notify topic map
    if (!isConnected())
      throw new ConstraintViolationException("Cannot modify subject indicator when topic isn't attached to a topic map.");
    if (indicators == null)
      indicators = topicmap.cfactory.makeSmallSet();    
    // Check to see if subject is already a subject indicator of this topic.
    else if (indicators.contains(subject_indicator))
      return;
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_SUBJECTIDENTIFIER, subject_indicator, null);    
    // Modify property
    indicators.add(subject_indicator);
  }

  public void removeSubjectIdentifier(LocatorIF subject_indicator) {
    if (subject_indicator == null)
      throw new NullPointerException("null is not a valid argument.");
    // Notify topic map
    if (!isConnected())
      throw new ConstraintViolationException("Cannot modify subject indicator when topic isn't attached to a topic map.");
    // Check to see if subject indicator is a subject indicator of this topic.
    if (indicators == null || !indicators.contains(subject_indicator))
      return;
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_SUBJECTIDENTIFIER, null, subject_indicator);    
    // Modify property
    indicators.remove(subject_indicator);
  }
  
  public Collection<TopicNameIF> getTopicNames() {
    // Return names
    return Collections.unmodifiableSet(names);
  }

  protected void addTopicName(TopicNameIF _name) {
    TopicName name = (TopicName)_name;
    if (name == null)
      throw new NullPointerException("null is not a valid argument.");
    // Check to see if name is already a member of this topic
    if (name.parent == this)
      return;
    // Check if used elsewhere.
    if (name.parent != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_TOPICNAME, name, null);    
    // Set topic property
    name.setTopic(this);
    // Add name to list of names
    names.add(name);
  }

  protected void removeTopicName(TopicNameIF _name) {
    TopicName name = (TopicName)_name;
    if (name == null)
      throw new NullPointerException("null is not a valid argument.");
    // Check to see if name is not a member of this topic
    if (name.parent != this)
      return;
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_TOPICNAME, null, name);    
    // Unset topic property
    name.setTopic(null);
    // Remove name from list of names
    names.remove(name);
  }
  
  public Collection<TopicNameIF> getTopicNamesByType(TopicIF type) {
    Set<TopicNameIF> namesbytype = topicmap.cfactory.makeSmallSet();
    for (TopicNameIF name : names) {
      if (name.getType().equals(type)) {
        namesbytype.add(name);
      }
    }
    return Collections.unmodifiableSet(namesbytype);
  }

  public Collection<OccurrenceIF> getOccurrences() {
    return Collections.unmodifiableSet(occurs);
  }

  public Collection<OccurrenceIF> getOccurrencesByType(TopicIF type) {
    Set<OccurrenceIF> occsbytype = topicmap.cfactory.makeSmallSet();
    for (OccurrenceIF occ : occurs) {
      if (occ.getType().equals(type)) {
        occsbytype.add(occ);
      }
    }
    return Collections.unmodifiableSet(occsbytype);
  }

  protected void addOccurrence(OccurrenceIF _occurrence) {
    Occurrence occurrence = (Occurrence)_occurrence;
    if (occurrence == null)
      throw new NullPointerException("null is not a valid argument.");
    // Check to see if occurrence is already a member of this topic
    if (occurrence.parent == this)
      return;
    // Check if used elsewhere.
    if (occurrence.parent != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_OCCURRENCE, occurrence, null);    
    // Set topic property
    occurrence.setTopic(this);
    // Add occurrence to list of occurrences
    occurs.add(occurrence);
  }

  protected void removeOccurrence(OccurrenceIF _occurrence) {
    Occurrence occurrence = (Occurrence)_occurrence;
    if (occurrence == null)
      throw new NullPointerException("null is not a valid argument.");
    // Check to see if occurrence is not a member of this topic
    if (occurrence.parent != this)
      return;
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_OCCURRENCE, null, occurrence);    
    // Unset topic property
    occurrence.setTopic(null);
    // Remove occurrence from list of occurrences
    occurs.remove(occurrence);
  }

  public Collection<AssociationRoleIF> getRoles() {
    return Collections.unmodifiableSet(roles);
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    if (roletype == null) throw new NullPointerException("Role type cannot be null.");
    // see below for rationale for next line
    Collection<AssociationRoleIF> result = new ArrayList<AssociationRoleIF>();
    synchronized (roles) {    
      Iterator<AssociationRoleIF> iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = iter.next();
        if (role.getType() == roletype)
          result.add(role);
      }
    }
    return result;
  }

  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype, TopicIF assoc_type) {
    if (roletype == null) throw new NullPointerException("Role type cannot be null.");
    if (assoc_type == null) throw new NullPointerException("Association type cannot be null.");

    synchronized (roles) {
      if (roles instanceof edu.emory.mathcs.backport.java.util.TreeSet) {
        AssociationRoleIF lowrole = new FakeRole(roletype, assoc_type,
                                                 Integer.MIN_VALUE);
        AssociationRoleIF highrole = new FakeRole(roletype, assoc_type,
                                                  Integer.MAX_VALUE);        
        return ((edu.emory.mathcs.backport.java.util.TreeSet)
                roles).subSet(lowrole, true, highrole, true);
      } else {
        // below are timing results from running a big query with
        // different data structures for the result collection. used
        // TologQuery --timeit and results indicate that uninitialized
        // CompactHashSet is the fastest.  however, this is likely a
        // special case, so using uninitialized ArrayList.
    
        // ArrayList(size)      816 804 804     -> 808
        // HashSet()            732 764 763  
        // ArrayList()          756 739 712 745 -> 738.0
        // CompactHashSet()     733 712 726 730 -> 725.25
        // CompactHashSet(size) 838 856 842     -> 845.33

        Collection<AssociationRoleIF> result = new ArrayList<AssociationRoleIF>();
        Iterator<AssociationRoleIF> iter = roles.iterator();
        while (iter.hasNext()) {
          AssociationRoleIF role = iter.next();
          if (role.getType() == roletype) {
            AssociationIF assoc = role.getAssociation();
            if (assoc != null && assoc.getType() == assoc_type)
              result.add(role);
          }
        }
        return result;
      }
    }    
  }

  public Collection<AssociationIF> getAssociations() {
    Set<AssociationIF> assocs = new HashSet<AssociationIF>();
    for (AssociationRoleIF role : roles) {
      assocs.add(role.getAssociation());
    }
    return Collections.unmodifiableSet(assocs);
  }

  public Collection<AssociationIF> getAssociationsByType(TopicIF type) {
    Set<AssociationIF> assocs = new HashSet<AssociationIF>();
    for (AssociationRoleIF role : roles) {
      AssociationIF assoc = role.getAssociation();
      if (assoc.getType().equals(type)) {
        assocs.add(assoc);
      }
    }
    return Collections.unmodifiableSet(assocs);
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
    // Add association role to list of association roles
    if (roles.size() > 100 && roles instanceof CompactHashSet) {
      Set<AssociationRoleIF> new_roles = new edu.emory.mathcs.backport.java.util.TreeSet(rolecomp);
      new_roles.addAll(roles);
      roles = new_roles;
    }
    roles.add(assoc_role);
  }

  /**
   * INTERNAL: Removes the association role from the set of
   * association roles in which the topic participates.
   */
  protected void removeRole(AssociationRoleIF assoc_role) {
    // Remove association from list of associations
    roles.remove(assoc_role);
  }  

  public void remove() {
    if (topicmap != null)
      topicmap.removeTopic(this);
  }
  
  public Collection<TopicIF> getTypes() {
    return types;
  }

  public void addType(TopicIF type) {
    if (type == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent(TopicIF.EVENT_ADD_TYPE, type, null);
    // Add type to list of types
    types = topicmap.setpool.add(types, type, true);
  }

  public void removeType(TopicIF type) {
    if (type == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent(TopicIF.EVENT_REMOVE_TYPE, null, type);
    // Remove type from list of types
    types = topicmap.setpool.remove(types, type, true);
  }

  public ReifiableIF getReified() {
    return reified;
  }

  void setReified(ReifiableIF reified) {
    ReifiableIF oldReified = getReified();
    if (ObjectUtils.different(oldReified, reified)) {
      // remove reifier from old reifiable
      this.reified = reified;
    }
  }

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("basic.Topic", (TopicIF)this);
  }

  static class RoleComparator implements Comparator<AssociationRoleIF> {
    public int compare(AssociationRoleIF role1, AssociationRoleIF role2) {

      int c = role1.getType().hashCode() - role2.getType().hashCode();
      if (c == 0)
        c = role1.getAssociation().getType().hashCode() -
            role2.getAssociation().getType().hashCode();
      if (c == 0) {
        // have to do this the long-winded way, because of overflow issues
        int hc1 = role1.getAssociation().hashCode();
        int hc2 = role2.getAssociation().hashCode();
        if (hc1 < hc2)
          c = -1;
        else if (hc1 > hc2)
          c = 1;
        else
          c = 0;
      }
      return c;
    }
  }

  static abstract class AbstractFake implements TMObjectIF {
    public String getObjectId() { return null; }
    public boolean isReadOnly() { return true; }
    public TopicMapIF getTopicMap() { return null; }
    public Collection<LocatorIF> getItemIdentifiers() { return null; }
    public void addItemIdentifier(LocatorIF item_identifier) { }
    public void removeItemIdentifier(LocatorIF item_identifier) { }
    public void remove() { }
  }
  
  static class FakeRole extends AbstractFake implements AssociationRoleIF {
    private AssociationIF assoc;
    private TopicIF assoctype;
    private TopicIF roletype;
    private int assochc;

    public FakeRole(TopicIF roletype, TopicIF assoctype, int assochc) {
      this.roletype = roletype;
      this.assoctype = assoctype;
      this.assochc = assochc;
    }
    
    public AssociationIF getAssociation() {
      if (assoc == null)
        assoc = new FakeAssociation(assoctype, assochc);
      return assoc;
    }
    public TopicIF getType() {
      return roletype;
    }
    public TopicIF getPlayer() { return null; }
    public void setPlayer(TopicIF player) {}
    public TopicIF getReifier() { return null; }
    public void setReifier(TopicIF reifier) { }
    public void setType(TopicIF type) { }
  }

  static class FakeAssociation extends AbstractFake implements AssociationIF {
    private TopicIF type;
    private int hashcode;

    public FakeAssociation(TopicIF type, int hashcode) {
      this.type = type;
      this.hashcode = hashcode;
    }
    
    public TopicIF getType() {
      return type;
    }
    public int hashCode() {
      return hashcode;
    }
    public TopicIF getReifier() { return null; }
    public void setReifier(TopicIF reifier) { }
    public void setType(TopicIF type) { }
    public Collection<TopicIF> getRoleTypes() { return null; }
    public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) { return null; }
    public Collection<AssociationRoleIF> getRoles() { return null; }
    public Collection<TopicIF> getScope() { return null; }
    public void addTheme(TopicIF theme) { }
    public void removeTheme(TopicIF theme) { }
  }
}
