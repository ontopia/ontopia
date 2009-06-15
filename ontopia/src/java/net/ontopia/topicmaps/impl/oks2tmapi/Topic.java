
// $Id: Topic.java,v 1.18 2008/06/13 08:17:51 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class Topic extends TopicMapObject implements org.tmapi.core.Topic {

  TopicIF other;

  Topic(TopicMap tm, TopicIF other) {
    super(tm);
    this.other = other;
  }

  public TMObjectIF getWrapped() {
    return other;
  }

  public void remove()
    throws org.tmapi.core.TopicInUseException {
    try {
      other.remove();
    } catch (NotRemovableException e) {
      throw new org.tmapi.core.TopicInUseException(e);
    }
  }

  public Set getTopicNames() {
    return tm.wrapSet(other.getTopicNames());
  }

  public org.tmapi.core.TopicName createTopicName(String value, Collection scope) {
    TopicMapIF otm = other.getTopicMap();
    TopicNameIF basename = otm.getBuilder().makeTopicName(other, value);
    if (scope != null && !scope.isEmpty()) {
      Iterator iter = scope.iterator();
      while (iter.hasNext()) {
	basename.addTheme(tm.unwrapTopic((org.tmapi.core.Topic)iter.next()));
      }
    }
    return tm.wrapTopicName(basename);
  }

  public org.tmapi.core.TopicName createTopicName(String value, org.tmapi.core.Topic type, Collection scope) {
    throw new UnsupportedOperationException("XTM 1.1 not yet supported.");
  }

  public org.tmapi.core.Occurrence createOccurrence(String value, org.tmapi.core.Topic type, Collection scope) {
    TopicMapIF otm = other.getTopicMap();
    OccurrenceIF occurrence = otm.getBuilder().makeOccurrence(other, tm.unwrapTopic(type), value);
    if (scope != null && !scope.isEmpty()) {
      Iterator iter = scope.iterator();
      while (iter.hasNext()) {
	occurrence.addTheme(tm.unwrapTopic((org.tmapi.core.Topic)iter.next()));
      }
    }
    return tm.wrapOccurrence(occurrence);
  }

  public org.tmapi.core.Occurrence createOccurrence(org.tmapi.core.Locator locator, org.tmapi.core.Topic type, Collection scope) {
    TopicMapIF otm = other.getTopicMap();
    OccurrenceIF occurrence = otm.getBuilder().makeOccurrence(other, tm.unwrapTopic(type), tm.unwrapLocator(locator));
    if (scope != null && !scope.isEmpty()) {
      Iterator iter = scope.iterator();
      while (iter.hasNext()) {
	occurrence.addTheme(tm.unwrapTopic((org.tmapi.core.Topic)iter.next()));
      }
    }
    return tm.wrapOccurrence(occurrence);
  }

  public Set getOccurrences() {
    return tm.wrapSet(other.getOccurrences());
  }
  
  public Set getSubjectLocators() {
    return tm.wrapSet(other.getSubjectLocators());
  }

  public void addSubjectLocator(org.tmapi.core.Locator subjectLocator) {
    try {
      other.addSubjectLocator(tm.unwrapLocator(subjectLocator));
    } catch (UniquenessViolationException e) {
      org.tmapi.core.Topic o = tm._getTopicBySubjectLocator(subjectLocator);
      throw new org.tmapi.core.TopicsMustMergeException(this, o, "Another topic already has this subject locator: " + subjectLocator);
    }
  }

  public void removeSubjectLocator(org.tmapi.core.Locator subjectLocator) {
    other.removeSubjectLocator(tm.unwrapLocator(subjectLocator));
  }
  
  public Set getSubjectIdentifiers() {
    return tm.wrapSet(other.getSubjectIdentifiers());
  }

  public void addSubjectIdentifier(org.tmapi.core.Locator subjectIdentifier) {
    try {
      other.addSubjectIdentifier(tm.unwrapLocator(subjectIdentifier));
    } catch (UniquenessViolationException e) {
      org.tmapi.core.Topic o = tm._getTopicBySubjectIdentifier(subjectIdentifier);
      throw new org.tmapi.core.TopicsMustMergeException(this, o, "Another topic already has this subject identifier: " + subjectIdentifier);
    }
  }

  public void removeSubjectIdentifier(org.tmapi.core.Locator subjectIdentifier) {
    other.removeSubjectIdentifier(tm.unwrapLocator(subjectIdentifier));
  }

  public Set getTypes() {
    return tm.wrapSet(other.getTypes());
  }

  public void addType(org.tmapi.core.Topic type) {
    other.addType(tm.unwrapTopic(type));
  }

  public void removeType(org.tmapi.core.Topic type) {
    other.removeType(tm.unwrapTopic(type));
  }

  public Set getRolesPlayed() {
    return tm.wrapSet(other.getRoles());
  }

  public void mergeIn(org.tmapi.core.Topic o)
    throws org.tmapi.core.MergeException {

    // WARNING: this method assumes that the other topic is in the
    // same topic map as this one.
    if (!getTopicMap().equals(o.getTopicMap())) 
      throw new org.tmapi.core.TMAPIRuntimeException("Cannot merge topics from different topic maps.");

    // Ignore if same as this topic
    if (this.equals(o)) return;

    // copy topic types
    Object[] topicTypes = o.getTypes().toArray();
    for (int i=0; i < topicTypes.length; i++) {
      addType((org.tmapi.core.Topic)topicTypes[i]);
    }

    // copy topic names and variants
    Object[] topicNames = o.getTopicNames().toArray();
    for (int i1=0; i1 < topicNames.length; i1++) {
      org.tmapi.core.TopicName otn = (org.tmapi.core.TopicName)topicNames[i1];
      org.tmapi.core.TopicName topicName = createTopicName(otn.getValue(), otn.getType(), otn.getScope());
      Object[] variants = otn.getVariants().toArray();
      for (int i2=0; i2 < variants.length; i2++) {
	org.tmapi.core.Variant ovn = (org.tmapi.core.Variant)variants[i2];
	if (ovn.getValue() != null)
	  topicName.createVariant(ovn.getValue(), ovn.getScope());
	else
	  topicName.createVariant(ovn.getResource(), ovn.getScope());
      }      
    }
    
    // copy occurrences
    Object[] occurrences = o.getTopicNames().toArray();
    for (int i=0; i < occurrences.length; i++) {
      org.tmapi.core.Occurrence occ = (org.tmapi.core.Occurrence)occurrences[i];
      if (occ.getValue() != null)
	createOccurrence(occ.getValue(), occ.getType(), occ.getScope());
      else
	createOccurrence(occ.getResource(), occ.getType(), occ.getScope());
    }

    // update roles
    Object[] roles = o.getRolesPlayed().toArray();
    for (int i=0; i < roles.length; i++) {
      ((org.tmapi.core.AssociationRole)roles[i]).setPlayer(this);
    }

    // make a copy of identities
    Object[] subjectLocators = o.getSubjectLocators().toArray();
    Object[] subjectIdentifiers = o.getSubjectIdentifiers().toArray();
    Object[] sourceLocators = o.getSourceLocators().toArray();

    // remove other topic
    ((TopicIF)((Topic)o).getWrapped()).remove();

    // add subject locator
    for (int i=0; i < subjectLocators.length; i++) {
      addSubjectLocator((org.tmapi.core.Locator)subjectLocators[i]);
    }

    // add subject identifiers
    for (int i=0; i < subjectIdentifiers.length; i++) {
      addSubjectIdentifier((org.tmapi.core.Locator)subjectIdentifiers[i]);
    }

    // add source locators
    for (int i=0; i < sourceLocators.length; i++) {
      addSourceLocator((org.tmapi.core.Locator)sourceLocators[i]);
    }

  }

  public Set getReified() {
    Set result = null;
    Iterator iter = getSubjectIdentifiers().iterator();
    while (iter.hasNext()) {
      org.tmapi.core.TopicMapObject tmobject = (org.tmapi.core.TopicMapObject)tm._getTopicMapObjectBySourceLocator((org.tmapi.core.Locator)iter.next());
      if (tmobject != null) {
	if (result == null) result = new HashSet(3);
	result.add(tmobject);
      }
    }
    return (result == null ? Collections.EMPTY_SET : result);
  }

    
}
