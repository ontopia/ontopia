
// $Id: Topic.java,v 1.86 2008/06/13 08:17:51 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.basic;

import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;

import net.ontopia.utils.UniqueSet;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
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

  protected Set subjects;
  protected Set indicators;
  protected ReifiableIF reified;

  protected UniqueSet scope;
  protected UniqueSet types;
  protected Set names;
  protected Set occurs;
  protected Set roles;

  protected Topic(TopicMap tm) {
    super(tm);
    types = topicmap.setpool.get(Collections.EMPTY_SET);
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
	
  public Collection getSubjectLocators() {
		if (subjects == null)
      return Collections.EMPTY_SET;
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
    fireEvent("TopicIF.addSubjectLocator", subject_locator, null);    
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
    fireEvent("TopicIF.removeSubjectLocator", null, subject_locator);    
    // Modify property
    subjects.remove(subject_locator);
	}

  public Collection getSubjectIdentifiers() {
    if (indicators == null)
      return Collections.EMPTY_SET;
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
    fireEvent("TopicIF.addSubjectIdentifier", subject_indicator, null);    
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
    fireEvent("TopicIF.removeSubjectIdentifier", null, subject_indicator);    
    // Modify property
    indicators.remove(subject_indicator);
  }
  
  public Collection getTopicNames() {
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
    fireEvent("TopicIF.addTopicName", name, null);    
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
    fireEvent("TopicIF.removeTopicName", null, name);    
    // Unset topic property
    name.setTopic(null);
    // Remove name from list of names
    names.remove(name);
  }
  
  public Collection getOccurrences() {
    return Collections.unmodifiableSet(occurs);
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
    fireEvent("TopicIF.addOccurrence", occurrence, null);    
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
    fireEvent("TopicIF.removeOccurrence", null, occurrence);    
    // Unset topic property
    occurrence.setTopic(null);
    // Remove occurrence from list of occurrences
    occurs.remove(occurrence);
  }

  public Collection getRoles() {
    return Collections.unmodifiableSet(roles);
  }
  
  public Collection getRolesByType(TopicIF roletype) {
    if (roletype == null) throw new NullPointerException("Role type cannot be null.");
    // see below for rationale for next line
    Collection result = new CompactHashSet();
    synchronized (roles) {    
      Iterator iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF)iter.next();
        if (role.getType() == roletype)
          result.add(role);
      }
    }
    return result;
  }

  public Collection getRolesByType(TopicIF roletype, TopicIF assoc_type) {
    if (roletype == null) throw new NullPointerException("Role type cannot be null.");
    if (assoc_type == null) throw new NullPointerException("Association type cannot be null.");

    // below are timing results from running a big query with different
    // data structures for the result collection. used TologQuery --timeit
    // and results indicate that uninitialized CompactHashSet is the fastest.
    
    // ArrayList(size)      816 804 804     -> 808
    // HashSet()            732 764 763  
    // ArrayList()          756 739 712 745 -> 738.0
    // CompactHashSet()     733 712 726 730 -> 725.25
    // CompactHashSet(size) 838 856 842     -> 845.33

    Collection result = new CompactHashSet();
    synchronized (roles) {    
      Iterator iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF)iter.next();
        if (role.getType() == roletype) {
          AssociationIF assoc = role.getAssociation();
          if (assoc != null && assoc.getType() == assoc_type)
            result.add(role);
        }
      }
    }
    return result;
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
  
  public Collection getTypes() {
    return types;
  }

  public void addType(TopicIF type) {
    if (type == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent("TopicIF.addType", type, null);
    // Add type to list of types
    types = topicmap.setpool.add(types, type, true);
  }

  public void removeType(TopicIF type) {
    if (type == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent("TopicIF.removeType", null, type);
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
  
}
