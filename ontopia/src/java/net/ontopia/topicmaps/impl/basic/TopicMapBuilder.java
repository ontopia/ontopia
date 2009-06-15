
// $Id: TopicMapBuilder.java,v 1.14 2008/11/03 12:24:40 lars.garshol Exp $

package net.ontopia.topicmaps.impl.basic;

import java.io.Reader;
import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.*;
import net.ontopia.utils.ObjectUtils;

/**
 * INTERNAL: The default topic map builder implementation.
 */
public class TopicMapBuilder implements TopicMapBuilderIF, java.io.Serializable {

  static final long serialVersionUID = 5405384048878296268L;

  protected TopicMap tm;
  
  public TopicMapBuilder(TopicMap tm) {
    this.tm = tm;
  }

  public TopicMapIF getTopicMap() {
		return tm;
	}

  protected TopicIF createTopic() {
    TopicIF topic = new Topic(tm);
    tm.addTopic(topic);
    return topic;
  }
  
  public TopicIF makeTopic() {
    return createTopic();
  }

  public TopicIF makeTopic(TopicIF topic_type) {
		if (topic_type == null) throw new NullPointerException("Topic type must not be null.");
		CrossTopicMapException.check(topic_type, this.tm);
    TopicIF topic = createTopic();
    topic.addType(topic_type);
    return topic;
  }


  public TopicIF makeTopic(Collection topic_types) {
		checkCollection(topic_types);
    TopicIF topic = createTopic();
    Iterator types = topic_types.iterator();
    while (types.hasNext())
      topic.addType((TopicIF) types.next());
    return topic;
  }

  public TopicNameIF makeTopicName(TopicIF topic, String value) {
		if (topic == null) throw new NullPointerException("Topic must not be null.");
		if (value == null) throw new NullPointerException("Topic name value must not be null.");
		CrossTopicMapException.check(topic, this.tm);
    TopicNameIF name = new TopicName(tm);
    ((Topic)topic).addTopicName(name);
    name.setValue(value);
    return name;
  }

  public TopicNameIF makeTopicName(TopicIF topic, TopicIF bntype, String value) {
		if (topic == null) throw new NullPointerException("Topic must not be null.");
		if (value == null) throw new NullPointerException("Topic name value must not be null.");
		CrossTopicMapException.check(topic, this.tm);
		if (bntype != null) CrossTopicMapException.check(bntype, this.tm);
    TopicNameIF name = new TopicName(tm);
    ((Topic)topic).addTopicName(name);
    name.setType(bntype);
    name.setValue(value);
    return name;
  }

  public VariantNameIF makeVariantName(TopicNameIF name, String variant_name) {
		if (name == null) throw new NullPointerException("Topic name must not be null.");
		if (variant_name == null) throw new NullPointerException("Variant value must not be null.");
		CrossTopicMapException.check(name, this.tm);
    VariantNameIF vname = new VariantName(tm);
    ((TopicName)name).addVariant(vname);
    vname.setValue(variant_name);
    return vname;
  }

  public VariantNameIF makeVariantName(TopicNameIF name, LocatorIF locator) {
		if (name == null) throw new NullPointerException("Topic name must not be null.");
		if (locator == null) throw new NullPointerException("Variant locator must not be null.");
		CrossTopicMapException.check(name, this.tm);
    VariantNameIF vname = new VariantName(tm);
    ((TopicName)name).addVariant(vname);
    vname.setLocator(locator);
    return vname;
  }
  
  public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, String value) {
		if (topic == null) throw new NullPointerException("Topic must not be null.");
		if (occurs_type == null) throw new NullPointerException("Occurrence type must not be null.");
		if (value == null) throw new NullPointerException("Occurrence value must not be null.");
		CrossTopicMapException.check(topic, this.tm);
		CrossTopicMapException.check(occurs_type, this.tm);
    OccurrenceIF occurs = new Occurrence(tm);
    ((Topic)topic).addOccurrence(occurs);
    occurs.setType(occurs_type);
    occurs.setValue(value, DataTypes.TYPE_STRING);
    return occurs;
  }
  
  public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, LocatorIF locator) {
		if (topic == null) throw new NullPointerException("Topic must not be null.");
		if (occurs_type == null) throw new NullPointerException("Occurrence type must not be null.");
		if (locator == null) throw new NullPointerException("Occurrence locator must not be null.");
		CrossTopicMapException.check(topic, this.tm);
		CrossTopicMapException.check(occurs_type, this.tm);
    OccurrenceIF occurs = new Occurrence(tm);
    ((Topic)topic).addOccurrence(occurs);
    occurs.setType(occurs_type);
    occurs.setValue(locator.getAddress(), DataTypes.TYPE_URI);
    return occurs;
  }
  
  public AssociationIF makeAssociation() {
    AssociationIF assoc = new Association(tm);    
    tm.addAssociation(assoc);
    return assoc;
  }

  public AssociationIF makeAssociation(TopicIF assoc_type) {
		if (assoc_type == null) throw new NullPointerException("Association type must not be null.");
		CrossTopicMapException.check(assoc_type, this.tm);
    AssociationIF assoc = new Association(tm);    
    tm.addAssociation(assoc);
    assoc.setType(assoc_type);
    return assoc;
  }
  
  public AssociationRoleIF makeAssociationRole(AssociationIF assoc, TopicIF role_type, TopicIF player) {
		if (assoc == null) throw new NullPointerException("Association must not be null.");
		if (role_type == null) throw new NullPointerException("Association role type must not be null.");
		if (player == null) throw new NullPointerException("Association role player must not be null.");
		CrossTopicMapException.check(assoc, this.tm);
		CrossTopicMapException.check(role_type, this.tm);
		CrossTopicMapException.check(player, this.tm);
    AssociationRoleIF assocrl = new AssociationRole(tm);
    ((Association)assoc).addRole(assocrl);
    assocrl.setType(role_type);
    assocrl.setPlayer(player);
		return assocrl;
  }
  
	// New builder methods in OKS 4.0

	protected void checkCollection(Collection objects) {
    Iterator iter = objects.iterator();
    while (iter.hasNext())
			CrossTopicMapException.check((TMObjectIF)iter.next(), this.tm);
	}

	protected void addScope(ScopedIF scoped, Collection scope) {
		if (!scope.isEmpty()) {
			Iterator iter = scope.iterator();
			while (iter.hasNext()) {
				scoped.addTheme((TopicIF)iter.next());
			}
		}
	}

  public VariantNameIF makeVariantName(TopicNameIF name, String value, Collection scope) {
		if (name == null) throw new NullPointerException("Topic name must not be null.");
		if (value == null) throw new NullPointerException("Variant value must not be null.");
		CrossTopicMapException.check(name, this.tm);
		checkCollection(scope);
    VariantNameIF vname = new VariantName(tm);
    ((TopicName)name).addVariant(vname);
    vname.setValue(value);
		addScope(vname, scope);
    return vname;
	}

  public VariantNameIF makeVariantName(TopicNameIF name, LocatorIF locator, Collection scope) {
		if (name == null) throw new NullPointerException("Topic name must not be null.");
		if (locator == null) throw new NullPointerException("Variant locator must not be null.");
		CrossTopicMapException.check(name, this.tm);
		checkCollection(scope);
    VariantNameIF vname = new VariantName(tm);
    ((TopicName)name).addVariant(vname);
    vname.setLocator(locator);
		addScope(vname, scope);
    return vname;
	}

  public VariantNameIF makeVariantName(TopicNameIF name, String variant_name, LocatorIF datatype) {
		if (name == null) throw new NullPointerException("Topic name must not be null.");
		if (variant_name == null) throw new NullPointerException("Variant value must not be null.");
		if (datatype == null) throw new NullPointerException("Variant value datatype must not be null.");
		CrossTopicMapException.check(name, this.tm);
    VariantNameIF vname = new VariantName(tm);
    ((TopicName)name).addVariant(vname);
    vname.setValue(variant_name, datatype);
    return vname;
  }

  public VariantNameIF makeVariantName(TopicNameIF name, String value, LocatorIF datatype, Collection scope) {
		if (name == null) throw new NullPointerException("Topic name must not be null.");
		if (value == null) throw new NullPointerException("Variant value must not be null.");
		if (datatype == null) throw new NullPointerException("Variant value datatype must not be null.");
		CrossTopicMapException.check(name, this.tm);
		checkCollection(scope);
    VariantNameIF vname = new VariantName(tm);
    ((TopicName)name).addVariant(vname);
    vname.setValue(value, datatype);
		addScope(vname, scope);
    return vname;
	}

  public VariantNameIF makeVariantName(TopicNameIF name, Reader value, long length, LocatorIF datatype) {
		if (name == null) throw new NullPointerException("Topic name must not be null.");
		if (value == null) throw new NullPointerException("Variant value must not be null.");
		if (datatype == null) throw new NullPointerException("Variant value datatype must not be null.");
		CrossTopicMapException.check(name, this.tm);
    VariantNameIF vname = new VariantName(tm);
    ((TopicName)name).addVariant(vname);
    vname.setReader(value, length, datatype);
    return vname;
	}

  public VariantNameIF makeVariantName(TopicNameIF name, Reader value, long length, LocatorIF datatype, Collection scope) {
		if (name == null) throw new NullPointerException("Topic name must not be null.");
		if (value == null) throw new NullPointerException("Variant value must not be null.");
		if (datatype == null) throw new NullPointerException("Variant value datatype must not be null.");
		CrossTopicMapException.check(name, this.tm);
		checkCollection(scope);
    VariantNameIF vname = new VariantName(tm);
    ((TopicName)name).addVariant(vname);
    vname.setReader(value, length, datatype);
		addScope(vname, scope);
    return vname;
	}

  public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, String value, LocatorIF datatype) {
    if (topic == null) throw new NullPointerException("Topic must not be null.");
    if (occurs_type == null) throw new NullPointerException("Occurrence type must not be null.");
    if (value == null) throw new NullPointerException("Occurrence value must not be null.");
    if (datatype == null) throw new NullPointerException("Occurrence value datatype must not be null.");
    CrossTopicMapException.check(topic, this.tm);
    CrossTopicMapException.check(occurs_type, this.tm);
    OccurrenceIF occurs = new Occurrence(tm);
    ((Topic)topic).addOccurrence(occurs);
    occurs.setType(occurs_type);
    occurs.setValue(value, datatype);
    return occurs;
  }

  public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, Reader value, long length, LocatorIF datatype) {
		if (topic == null) throw new NullPointerException("Topic must not be null.");
		if (occurs_type == null) throw new NullPointerException("Occurrence type must not be null.");
		if (value == null) throw new NullPointerException("Occurrence value must not be null.");
		if (datatype == null) throw new NullPointerException("Occurrence value datatype must not be null.");
		CrossTopicMapException.check(topic, this.tm);
		CrossTopicMapException.check(occurs_type, this.tm);
    OccurrenceIF occurs = new Occurrence(tm);
    ((Topic)topic).addOccurrence(occurs);
    occurs.setType(occurs_type);
    occurs.setReader(value, length, datatype);
    return occurs;
	}

  public AssociationIF makeAssociation(TopicIF assoc_type, TopicIF role_type, TopicIF player) {
		if (assoc_type == null) throw new NullPointerException("Association type must not be null.");
		if (role_type == null) throw new NullPointerException("Association role type must not be null.");
		if (player == null) throw new NullPointerException("Association role player must not be null.");
		CrossTopicMapException.check(assoc_type, this.tm);
		CrossTopicMapException.check(role_type, this.tm);
		CrossTopicMapException.check(player, this.tm);
    AssociationIF assoc = new Association(tm);    
    tm.addAssociation(assoc);
    assoc.setType(assoc_type);

    AssociationRoleIF assocrl = new AssociationRole(tm);
    ((Association)assoc).addRole(assocrl);
    assocrl.setType(role_type);
    assocrl.setPlayer(player);

    return assoc;
	}

}
