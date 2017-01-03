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

import java.io.Reader;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: The default topic map builder implementation.
 */
public class TopicMapBuilder implements TopicMapBuilderIF, Serializable {
  private static final long serialVersionUID = 5405384048878296268L;

  protected TransactionIF txn;
  protected TopicMapIF tm;
  
  public TopicMapBuilder(TransactionIF txn, TopicMapIF tm) {
    this.txn = txn;
    this.tm = tm;
  }

  public TopicMapIF getTopicMap() {
    return tm;
  }
  
  public TopicIF makeTopic() {
    TopicIF topic = new Topic(txn);
    ((TopicMap)tm).addTopic(topic);
    return topic;
  }

  public TopicIF makeTopic(TopicIF topic_type) {
    if (topic_type == null)
      throw new NullPointerException(MSG_TOPIC_TYPE_NOT_NULL);
    CrossTopicMapException.check(topic_type, this.tm);
    TopicIF topic = new Topic(txn);
    ((TopicMap)tm).addTopic(topic);
    topic.addType(topic_type);
    return topic;
  }


  public TopicIF makeTopic(Collection topic_types) {
		checkCollection(topic_types);
    TopicIF topic = new Topic(txn);
    ((TopicMap)tm).addTopic(topic);
    Iterator types = topic_types.iterator();
    while (types.hasNext())
      topic.addType((TopicIF) types.next());
    return topic;
  }

  public TopicNameIF makeTopicName(TopicIF topic, String value) {
    if (topic == null) throw new NullPointerException(MSG_TOPIC_NOT_NULL);
    if (value == null) throw new NullPointerException(MSG_TOPIC_NAME_VALUE_NOT_NULL);
    CrossTopicMapException.check(topic, this.tm);
		
    TopicIF bntype = getDefaultNameType();
    TopicNameIF name = new TopicName(txn);
    ((Topic)topic).addTopicName(name);
    name.setValue(value);
    name.setType(bntype);
    return name;
  }

  public TopicNameIF makeTopicName(TopicIF topic, TopicIF bntype, String value) {
    if (topic == null)
      throw new NullPointerException(MSG_TOPIC_NOT_NULL);
    if (value == null)
      throw new NullPointerException(MSG_TOPIC_NAME_VALUE_NOT_NULL);
    CrossTopicMapException.check(topic, this.tm);
    if (bntype != null)
      CrossTopicMapException.check(bntype, this.tm);
    // if type has not been specified, use the default name type
    if (bntype == null)
      bntype = getDefaultNameType();
    else
      CrossTopicMapException.check(bntype, this.tm);		  
		
    TopicNameIF name = new TopicName(txn);
    ((Topic)topic).addTopicName(name);
    name.setType(bntype);
    name.setValue(value);
    return name;
  }

  private TopicIF getDefaultNameType() {
    TopicIF nameType = tm.getTopicBySubjectIdentifier(PSI.getSAMNameType());
    if (nameType == null) {
      nameType = makeTopic();
      nameType.addSubjectIdentifier(PSI.getSAMNameType());
    }
    return nameType;
  }
  
  public VariantNameIF makeVariantName(TopicNameIF name, String variant_name) {
    if (name == null)
      throw new NullPointerException(MSG_TOPIC_NAME_NOT_NULL);
    if (variant_name == null)
      throw new NullPointerException(MSG_VARIANT_VALUE_NOT_NULL);
    CrossTopicMapException.check(name, this.tm);
    VariantNameIF vname = new VariantName(txn);
    ((TopicName)name).addVariant(vname);
    vname.setValue(variant_name);
    return vname;
  }

  public VariantNameIF makeVariantName(TopicNameIF name, LocatorIF locator) {
    if (name == null)
      throw new NullPointerException(MSG_TOPIC_NAME_NOT_NULL);
    if (locator == null)
      throw new NullPointerException(MSG_VARIANT_LOCATOR_NOT_NULL);
    CrossTopicMapException.check(name, this.tm);
    VariantNameIF vname = new VariantName(txn);
    ((TopicName)name).addVariant(vname);
    vname.setLocator(locator);
    return vname;
  }
  
  public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, String value) {
    if (topic == null)
      throw new NullPointerException(MSG_TOPIC_NOT_NULL);
    if (occurs_type == null)
      throw new NullPointerException(MSG_OCCURRENCE_TYPE_NOT_NULL);
    if (value == null)
      throw new NullPointerException(MSG_OCCURRENCE_VALUE_NOT_NULL);
    CrossTopicMapException.check(topic, this.tm);
    CrossTopicMapException.check(occurs_type, this.tm);
    OccurrenceIF occurs = new Occurrence(txn);
    ((Topic)topic).addOccurrence(occurs);
    occurs.setType(occurs_type);
    occurs.setValue(value);
    return occurs;
  }
  
  public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, LocatorIF locator) {
    if (topic == null)
      throw new NullPointerException(MSG_TOPIC_NOT_NULL);
    if (occurs_type == null)
      throw new NullPointerException(MSG_OCCURRENCE_TYPE_NOT_NULL);
    if (locator == null)
      throw new NullPointerException(MSG_OCCURRENCE_LOCATOR_NOT_NULL);
    CrossTopicMapException.check(topic, this.tm);
    CrossTopicMapException.check(occurs_type, this.tm);
    OccurrenceIF occurs = new Occurrence(txn);
    ((Topic)topic).addOccurrence(occurs);
    occurs.setType(occurs_type);
    occurs.setLocator(locator);
    return occurs;
  }

  public AssociationIF makeAssociation(TopicIF assoc_type) {
    if (assoc_type == null)
      throw new NullPointerException(MSG_ASSOCIATION_TYPE_NOT_NULL);
    CrossTopicMapException.check(assoc_type, this.tm);
    AssociationIF assoc = new Association(txn);    
    ((TopicMap)tm).addAssociation(assoc);
    assoc.setType(assoc_type);
    return assoc;
  }
  
  public AssociationRoleIF makeAssociationRole(AssociationIF assoc, TopicIF role_type, TopicIF player) {
    if (assoc == null)
      throw new NullPointerException(MSG_ASSOCATION_NOT_NULL);
    if (role_type == null)
      throw new NullPointerException(MSG_ASSOCIATION_ROLE_TYPE_NOT_NULL);
    if (player == null)
      throw new NullPointerException(MSG_ASSOCIATION_ROLE_PLAYER_NOT_NULL);
    CrossTopicMapException.check(assoc, this.tm);
    CrossTopicMapException.check(role_type, this.tm);
    CrossTopicMapException.check(player, this.tm);
    AssociationRoleIF assocrl = new AssociationRole(txn);
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
    if (name == null)
      throw new NullPointerException(MSG_TOPIC_NAME_NOT_NULL);
    if (value == null)
      throw new NullPointerException(MSG_VARIANT_VALUE_NOT_NULL);
    CrossTopicMapException.check(name, this.tm);
    checkCollection(scope);
    VariantNameIF vname = new VariantName(txn);
    ((TopicName)name).addVariant(vname);
    vname.setValue(value);
    addScope(vname, scope);
    return vname;
  }

  public VariantNameIF makeVariantName(TopicNameIF name, LocatorIF locator, Collection scope) {
    if (name == null)
      throw new NullPointerException(MSG_TOPIC_NAME_NOT_NULL);
    if (locator == null)
      throw new NullPointerException(MSG_VARIANT_LOCATOR_NOT_NULL);
    CrossTopicMapException.check(name, this.tm);
    checkCollection(scope);
    VariantNameIF vname = new VariantName(txn);
    ((TopicName)name).addVariant(vname);
    vname.setLocator(locator);
    addScope(vname, scope);
    return vname;
  }

  public VariantNameIF makeVariantName(TopicNameIF name, String value, LocatorIF datatype) {
    if (name == null)
      throw new NullPointerException(MSG_TOPIC_NAME_NOT_NULL);
    if (value == null)
      throw new NullPointerException(MSG_VARIANT_VALUE_NOT_NULL);
    if (datatype == null)
      throw new NullPointerException(MSG_VARIANT_DATATYPE_NOT_NULL);
    CrossTopicMapException.check(name, this.tm);
    VariantNameIF vname = new VariantName(txn);
    ((TopicName)name).addVariant(vname);
    vname.setValue(value, datatype);
    return vname;
  }

  public VariantNameIF makeVariantName(TopicNameIF name, String value, LocatorIF datatype, Collection scope) {
    if (name == null)
      throw new NullPointerException(MSG_TOPIC_NAME_NOT_NULL);
    if (value == null)
      throw new NullPointerException(MSG_VARIANT_VALUE_NOT_NULL);
    if (datatype == null)
      throw new NullPointerException(MSG_VARIANT_DATATYPE_NOT_NULL);
    CrossTopicMapException.check(name, this.tm);
    checkCollection(scope);
    VariantNameIF vname = new VariantName(txn);
    ((TopicName)name).addVariant(vname);
    vname.setValue(value, datatype);
    addScope(vname, scope);
    return vname;
  }

  public VariantNameIF makeVariantName(TopicNameIF name, Reader value, long length, LocatorIF datatype) {
    if (name == null)
      throw new NullPointerException(MSG_TOPIC_NAME_NOT_NULL);
    if (value == null)
      throw new NullPointerException(MSG_VARIANT_VALUE_NOT_NULL);
    if (datatype == null)
      throw new NullPointerException(MSG_VARIANT_DATATYPE_NOT_NULL);
    CrossTopicMapException.check(name, this.tm);
    VariantNameIF vname = new VariantName(txn);
    ((TopicName)name).addVariant(vname);
    vname.setReader(value, length, datatype);
    return vname;
  }

  public VariantNameIF makeVariantName(TopicNameIF name, Reader value, long length, LocatorIF datatype, Collection scope) {
    if (name == null)
      throw new NullPointerException(MSG_TOPIC_NAME_NOT_NULL);
    if (value == null)
      throw new NullPointerException(MSG_VARIANT_VALUE_NOT_NULL);
    if (datatype == null)
      throw new NullPointerException(MSG_VARIANT_DATATYPE_NOT_NULL);
    CrossTopicMapException.check(name, this.tm);
    checkCollection(scope);
    VariantNameIF vname = new VariantName(txn);
    ((TopicName)name).addVariant(vname);
    vname.setReader(value, length, datatype);
    addScope(vname, scope);
    return vname;
  }

  public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, String value, LocatorIF datatype) {
    if (topic == null)
      throw new NullPointerException(MSG_TOPIC_NOT_NULL);
    if (occurs_type == null)
      throw new NullPointerException(MSG_OCCURRENCE_TYPE_NOT_NULL);
    if (value == null)
      throw new NullPointerException(MSG_OCCURRENCE_VALUE_NOT_NULL);
    if (datatype == null)
      throw new NullPointerException(MSG_OCCURRENCE_DATATYPE_NOT_NULL);
    CrossTopicMapException.check(topic, this.tm);
    CrossTopicMapException.check(occurs_type, this.tm);
    OccurrenceIF occurs = new Occurrence(txn);
    ((Topic)topic).addOccurrence(occurs);
    occurs.setType(occurs_type);
    occurs.setValue(value, datatype);
    return occurs;
  }

  public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, Reader value, long length, LocatorIF datatype) {
    if (topic == null)
      throw new NullPointerException(MSG_TOPIC_NOT_NULL);
    if (occurs_type == null)
      throw new NullPointerException(MSG_OCCURRENCE_TYPE_NOT_NULL);
    if (value == null)
      throw new NullPointerException(MSG_OCCURRENCE_VALUE_NOT_NULL);
    if (datatype == null)
      throw new NullPointerException(MSG_OCCURRENCE_DATATYPE_NOT_NULL);
    CrossTopicMapException.check(topic, this.tm);
    CrossTopicMapException.check(occurs_type, this.tm);
    OccurrenceIF occurs = new Occurrence(txn);
    ((Topic)topic).addOccurrence(occurs);
    occurs.setType(occurs_type);
    occurs.setReader(value, length, datatype);
    return occurs;
  }

  public AssociationIF makeAssociation(TopicIF assoc_type, TopicIF role_type, TopicIF player) {
    if (assoc_type == null)
      throw new NullPointerException(MSG_ASSOCIATION_TYPE_NOT_NULL);
    if (role_type == null)
      throw new NullPointerException(MSG_ASSOCIATION_ROLE_TYPE_NOT_NULL);
    if (player == null)
      throw new NullPointerException(MSG_ASSOCIATION_ROLE_PLAYER_NOT_NULL);
    CrossTopicMapException.check(assoc_type, this.tm);
    CrossTopicMapException.check(role_type, this.tm);
    CrossTopicMapException.check(player, this.tm);
    AssociationIF assoc = new Association(txn);
    ((TopicMap)tm).addAssociation(assoc);
    assoc.setType(assoc_type);

    AssociationRoleIF assocrl = new AssociationRole(txn);
    ((Association)assoc).addRole(assocrl);
    assocrl.setType(role_type);
    assocrl.setPlayer(player);

    return assoc;
  }
  
}
