
// $Id: TopicMap.java,v 1.21 2008/06/13 08:36:26 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.topicmaps.impl.oks2tmapi.index.*;
import net.ontopia.topicmaps.utils.MergeUtils;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class TopicMap extends TopicMapObject implements org.tmapi.core.TopicMap {
  
  protected TopicMapSystem tmsystem;
  protected TopicMapIF other;

  TopicMap(TopicMapSystem tmsystem, TopicMapStoreIF store) {
    super(null);
    this.other = store.getTopicMap();
    this.tmsystem = tmsystem;
    this.tm = this;
  }

  public void remove() throws org.tmapi.core.TMAPIException {
    tmsystem.remove(this);
    other.getStore().delete(true);
    //! other.getStore().close();
  }
  
  public TMObjectIF getWrapped() {
    return other;
  }

  public org.tmapi.core.TopicMapSystem getTopicMapSystem() {
    return tmsystem;
  }

  public org.tmapi.core.Locator getBaseLocator() {
    return tm.wrapLocator(other.getStore().getBaseAddress());
  }

  public Set getAssociations() {
    return tm.wrapSet(other.getAssociations());
  }

  public org.tmapi.core.Association createAssociation() {
    AssociationIF assoc = other.getBuilder().makeAssociation(null);
    return new Association(this, assoc);
  }

  public Set getTopics() {
    return tm.wrapSet(other.getTopics());
  }

  public org.tmapi.core.Topic createTopic() {
    TopicIF topic = other.getBuilder().makeTopic();
    return new Topic(this, topic);
  }

  public org.tmapi.core.Locator createLocator(String reference, String notation) {
    return tm.wrapLocator(tmsystem.createLocatorIF(reference, notation));
  }

  public org.tmapi.core.Locator createLocator(String reference) {
    return tm.wrapLocator(tmsystem.createLocatorIF(reference));
  }

  public org.tmapi.core.Topic getReifier() {
    return tm._getReifier(this, tm);
  }

  public org.tmapi.core.TopicMapObject getObjectById(String objectId) {
    TMObjectIF tmobject = other.getObjectById(objectId);
    return tm.wrapTMObject(tmobject);
  }

  public void mergeIn(org.tmapi.core.TopicMap o)
    throws org.tmapi.core.MergeException {
    // Ignore if same topic map
    if (this.equals(o)) return;

    if (o instanceof TopicMap) {
      // Use quick solution if of same implementation
      MergeUtils.mergeInto(this.other, ((TopicMap)o).other);
    } else {
      throw new UnsupportedOperationException("Merging topic maps from different TMAPI implementations not yet supported. ");
    }
  }

  public Object getHelperObject(Class implementationInterface) 
    throws org.tmapi.core.UnsupportedHelperObjectException, org.tmapi.core.HelperObjectInstantiationException, org.tmapi.core.HelperObjectConfigurationException {

    Object index = null;

    if (org.tmapi.index.core.TopicMapObjectsIndex.class.equals(implementationInterface))
      index = new TopicMapObjectsIndex(this);

    else if (org.tmapi.index.core.AssociationRolesIndex.class.equals(implementationInterface))
      index = new AssociationRolesIndex(this, 
					(ClassInstanceIndexIF)other.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF"));

    else if (org.tmapi.index.core.TopicsIndex.class.equals(implementationInterface))
      index = new TopicsIndex(this, 
			      (ClassInstanceIndexIF)other.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF"));

    else if (org.tmapi.index.core.AssociationsIndex.class.equals(implementationInterface))
      index = new AssociationsIndex(this, 
				    (ClassInstanceIndexIF)other.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF"));

    else if (org.tmapi.index.core.OccurrencesIndex.class.equals(implementationInterface))
      index = new OccurrencesIndex(this, 
				   (ClassInstanceIndexIF)other.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF"), 
				   (OccurrenceIndexIF)other.getIndex("net.ontopia.topicmaps.core.index.OccurrenceIndexIF"));

    else if (org.tmapi.index.core.TopicNamesIndex.class.equals(implementationInterface))
      index = new TopicNamesIndex(this, 
				  (ClassInstanceIndexIF)other.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF"), 
				  (NameIndexIF)other.getIndex("net.ontopia.topicmaps.core.index.NameIndexIF"));
    
    else if (org.tmapi.index.core.ScopedObjectsIndex.class.equals(implementationInterface))
      index = new ScopedObjectsIndex(this, 
				     (ScopeIndexIF)other.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF"));
    
    else
      throw new org.tmapi.core.UnsupportedHelperObjectException("Helper type " + implementationInterface + " not supported.");

    if (index instanceof org.tmapi.core.TopicMapSystem.ConfigurableHelperObject) {
      ((org.tmapi.core.TopicMapSystem.ConfigurableHelperObject)index).configure(this);
    }
    return index;
  }

  public void close() {
    tmsystem.close(this);
    other.getStore().close();
  }

  /* ---- wrappers: topic */

  public org.tmapi.core.Topic wrapTopic(TopicIF topic) {
    if (topic == null)
      return null;
    else
      return new Topic(this, topic);
  }

  public TopicIF unwrapTopic(org.tmapi.core.Topic topic) {
    if (topic == null)
      return null;
    else
      return (TopicIF)((Topic)topic).getWrapped();
  }

  /* ---- wrappers: topic name */

  public org.tmapi.core.TopicName wrapTopicName(TopicNameIF topicname) {
    if (topicname == null)
      return null;
    else
      return new TopicName(this, topicname);
  }

  public TopicNameIF unwrapTopicName(org.tmapi.core.TopicName topicname) {
    if (topicname == null)
      return null;
    else
      return (TopicNameIF)((TopicName)topicname).getWrapped();
  }

  /* ---- wrappers: variant */

  public org.tmapi.core.Variant wrapVariant(VariantNameIF variant) {
    if (variant == null)
      return null;
    else
      return new Variant(this, variant);
  }

  public VariantNameIF unwrapVariant(org.tmapi.core.Variant variant) {
    if (variant == null)
      return null;
    else
      return (VariantNameIF)((Variant)variant).getWrapped();
  }

  /* ---- wrappers: occurrence */

  public org.tmapi.core.Occurrence wrapOccurrence(OccurrenceIF occurrence) {
    if (occurrence == null)
      return null;
    else
      return new Occurrence(this, occurrence);
  }

  public OccurrenceIF unwrapOccurrence(org.tmapi.core.Occurrence occurrence) {
    if (occurrence == null)
      return null;
    else
      return (OccurrenceIF)((Occurrence)occurrence).getWrapped();
  }

  /* ---- wrappers: association */

  public org.tmapi.core.Association wrapAssociation(AssociationIF association) {
    if (association == null)
      return null;
    else
      return new Association(this, association);
  }

  public AssociationIF unwrapAssociation(org.tmapi.core.Association association) {
    if (association == null)
      return null;
    else
      return (AssociationIF)((Association)association).getWrapped();
  }

  /* ---- wrappers: association role */

  public org.tmapi.core.AssociationRole wrapAssociationRole(AssociationRoleIF role) {
    if (role == null)
      return null;
    else
      return new AssociationRole(this, role);
  }

  public AssociationRoleIF unwrapAssociationRole(org.tmapi.core.AssociationRole role) {
    if (role == null)
      return null;
    else
      return (AssociationRoleIF)((AssociationRole)role).getWrapped();
  }

  /* ---- wrappers: locator */

  public org.tmapi.core.Locator wrapLocator(LocatorIF locator) {
    if (locator == null)
      return null;
    else if (locator instanceof org.tmapi.core.Locator)
      return (org.tmapi.core.Locator)locator;
    else
      return new Locator(locator);
  }

  public LocatorIF unwrapLocator(org.tmapi.core.Locator locator) {
    if (locator == null)
      return null;
    else if (locator instanceof LocatorIF)
      return (LocatorIF)locator;
    
    else if (locator instanceof Locator)
      return ((Locator)locator).getWrapped();
    else 
      return tmsystem.createLocatorIF(locator.getNotation(), locator.getReference());
  }
  
  /* ---- wrappers: topic map object */

  public org.tmapi.core.TopicMapObject wrapTMObject(Object tmobject) {
    if (tmobject == null) {
      return null;
    } else if (tmobject instanceof AssociationIF) {
      return new Association(this, (AssociationIF)tmobject);
    } else if (tmobject instanceof AssociationRoleIF) {
      return new AssociationRole(this, (AssociationRoleIF)tmobject);
    } else if (tmobject instanceof TopicNameIF) {
      return new TopicName(this, (TopicNameIF)tmobject);
    } else if (tmobject instanceof OccurrenceIF) {
      return new Occurrence(this, (OccurrenceIF)tmobject);
    } else if (tmobject instanceof TopicIF) {
      return new Topic(this, (TopicIF)tmobject);
    } else if (tmobject instanceof TopicMapIF) {
      //! return new TopicMap(this, (TopicMapIF)tmobject);
      return this;
    } else if (tmobject instanceof VariantNameIF) {
      return new Variant(this, (VariantNameIF)tmobject);
    } else {
      throw new org.tmapi.core.TMAPIRuntimeException("Invalid topic map object type: " + tmobject);
    }
  }

  /* ---- wrappers: set */

  public Set wrapSet(Collection coll) {
    return new LazySet(this, coll);
  }

  public boolean equals(Object o) {
    // NOTE: overriding this method because it is slightly different
    // than for other topic map objects.
    if (o == null || !(o instanceof org.tmapi.core.TopicMap))
      return false;
    return this.other == ((TopicMap)o).other;
  }

  /* ---- helpers */

  public org.tmapi.core.Topic _getTopicBySubjectLocator(org.tmapi.core.Locator subjectLocator) {
    return wrapTopic(other.getTopicBySubjectLocator(unwrapLocator(subjectLocator)));
  }

  public org.tmapi.core.Topic _getTopicBySubjectIdentifier(org.tmapi.core.Locator subjectIdentifier) {
    return wrapTopic(other.getTopicBySubjectIdentifier(unwrapLocator(subjectIdentifier)));
  }

  public org.tmapi.core.TopicMapObject _getTopicMapObjectBySourceLocator(org.tmapi.core.Locator sourceLocator) {
    return wrapTMObject(other.getObjectByItemIdentifier(unwrapLocator(sourceLocator)));
  }

  public org.tmapi.core.Topic _getReifier(TopicMapObject o, TopicMap tm) {
    Iterator iter = o.getSourceLocators().iterator();
    while (iter.hasNext()) {
      org.tmapi.core.Topic topic = _getTopicBySubjectIdentifier((org.tmapi.core.Locator)iter.next());
      if (topic != null) return topic;
    }
    return null;
  }
  
}
