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
import java.util.Iterator;
import java.util.Objects;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.topicmaps.utils.PSI;

/**
 * INTERNAL: The rdbms topic name implementation.
 */
public class TopicName extends TMObject implements TopicNameIF {

  public static final String CLASS_INDICATOR = "B";

  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------

  protected static final int LF_topic = 2;
  protected static final int LF_scope = 3;
  protected static final int LF_type = 4;
  protected static final int LF_value = 5;
  protected static final int LF_variants = 6;
  protected static final int LF_reifier = 7;
  protected static final String[] fields = {"sources", "topicmap", "topic",
                                            "scope", "type", "value",
                                            "variants", "reifier"};

  @Override
  public void detach() {
    detachCollectionField(LF_sources);
    detachField(LF_topicmap);
    detachField(LF_topic);
    detachField(LF_reifier);
    detachCollectionField(LF_scope);
    detachField(LF_type);
    detachField(LF_value);
    detachCollectionField(LF_variants);
  }

  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public TopicName() {
  }

  public TopicName(TransactionIF txn) {
    super(txn);
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public int _p_getFieldCount() {
    return fields.length;
  }

  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public String getClassIndicator() {
    return CLASS_INDICATOR;
  }

  @Override
  public String getObjectId() {
    return (id == null ? null : CLASS_INDICATOR + id.getKey(0));
  }

  // ---------------------------------------------------------------------------
  // NameIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getTopic() {
    return this.<TopicIF>loadField(LF_topic);
  }

  /**
   * INTERNAL: Set the topic that the topic name belongs to. [parent]
   */
  protected void setTopic(TopicIF topic) {
    // Set parent topic map
    setTopicMap((topic == null ? null : (TopicMap) topic.getTopicMap()));
    // Notify transaction
    valueChanged(LF_topic, topic, true);
  }

  protected void setTopicMap(TopicMap topicmap) {
    // Notify transaction
    transactionChanged(topicmap);
    valueChanged(LF_topicmap, topicmap, true);

    // Inform variants
    for (VariantName variantname : this.<VariantName>loadCollectionField(LF_variants)) {
      variantname.setTopicMap(topicmap);
    }
  }

  @Override
  public String getValue() {
    return this.<String>loadField(LF_value);
  }

  @Override
  public void setValue(String value) {
    Objects.requireNonNull(value, "Topic name value must not be null.");
    // Notify listeners
    fireEvent(TopicNameIF.EVENT_SET_VALUE, value, getValue());
    // Notify transaction
    valueChanged(LF_value, value, true);
  }

  @Override
  public Collection<VariantNameIF> getVariants() {
    return this.<VariantNameIF>loadCollectionField(LF_variants);
  }

  protected void addVariant(VariantNameIF variant) {
    Objects.requireNonNull(variant, MSG_NULL_ARGUMENT);
    // Check to see if variant is already a member of this topic name
    if (variant.getTopicName() == this) {
      return;
    }
    // Check if used elsewhere.
    if (variant.getTopicName() != null) {
      throw new ConstraintViolationException("Moving objects is not allowed.");
    }
    
    // Notify listeners
    fireEvent(TopicNameIF.EVENT_ADD_VARIANT, variant, null);
    // Set parent name property
    ((VariantName) variant).setTopicName(this);
    // Notify transaction
    valueAdded(LF_variants, variant, false);

    // Add inherited themes to variant name
    for (TopicIF theme : getScope()) {
      ((VariantName)variant)._addTheme(theme, false);
    }
  }

  protected void removeVariant(VariantNameIF variant) {
    Objects.requireNonNull(variant, MSG_NULL_ARGUMENT);
    // Check to see if variant is not a member of this topic name
    if (variant.getTopicName() != this) {
      return;
    }
    
    // Notify listeners
    fireEvent(TopicNameIF.EVENT_REMOVE_VARIANT, null, variant);

    // Remove inherited themes from variant name
    for (TopicIF theme : getScope()) {
      ((VariantName)variant)._removeTheme(theme, false);
    }
    
    // Unset parent name property
    ((VariantName) variant).setTopicName(null);
    // Notify transaction
    valueRemoved(LF_variants, variant, false);
  }

  @Override
  public void remove() {
    Topic parent = (Topic) getTopic();
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      parent.removeTopicName(this);
    }
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public Collection<TopicIF> getScope() {
    return this.<TopicIF>loadCollectionField(LF_scope);
  }

  @Override
  public void addTheme(TopicIF theme) {
    Objects.requireNonNull(theme, MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(theme, this);    
    // Notify listeners
    fireEvent(TopicNameIF.EVENT_ADD_THEME, theme, null);
    // Notify transaction
    valueAdded(LF_scope, theme, true);

    // add theme to variants
    Collection variants = getVariants();
    if (!variants.isEmpty()) {
      Iterator iter = variants.iterator();
      while (iter.hasNext()) {
        VariantName v = (VariantName) iter.next();
        v._addTheme(theme, false);
      }
    }
  }

  @Override
  public void removeTheme(TopicIF theme) {
    Objects.requireNonNull(theme, MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(TopicNameIF.EVENT_REMOVE_THEME, null, theme);

    // remove theme from variants
    Collection variants = getVariants();
    if (!variants.isEmpty()) {
      Iterator iter = variants.iterator();
      while (iter.hasNext()) {
        VariantName v = (VariantName) iter.next();
        v._removeTheme(theme, false);
      }
    }

    // Notify transaction
    valueRemoved(LF_scope, theme, true);
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getType() {
    return this.<TopicIF>loadField(LF_type);
  }

  @Override
  public void setType(TopicIF type) {
    if (type == null) {
      type = getDefaultNameType();
    } else {
      CrossTopicMapException.check(type, this);
    }

    // Notify listeners
    fireEvent(TopicNameIF.EVENT_SET_TYPE, type, getType());
    // Notify transaction
    valueChanged(LF_type, type, true);
  }

  private TopicIF getDefaultNameType() {
    TopicMapIF tm = getTopicMap();
    TopicIF nameType = tm.getTopicBySubjectIdentifier(PSI.getSAMNameType());
    if (nameType == null) {
      nameType = tm.getBuilder().makeTopic();
      nameType.addSubjectIdentifier(PSI.getSAMNameType());
    }
    return nameType;
  }

  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getReifier() {
    return this.<TopicIF>loadField(LF_reifier);
  }

  @Override
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null) {
      CrossTopicMapException.check(_reifier, this);
    }
    if (DuplicateReificationException.check(this, _reifier)) { return; }
    // Notify listeners
    Topic reifier = (Topic) _reifier;
    Topic oldReifier = (Topic) getReifier();
    fireEvent(ReifiableIF.EVENT_SET_REIFIER, reifier, oldReifier);
    valueChanged(LF_reifier, reifier, true);
    if (oldReifier != null) {
      oldReifier.setReified(null);
    }
    if (reifier != null) {
      reifier.setReified(this);
    }
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  @Override
  public String toString() {
    return ObjectStrings.toString("rdbms.TopicName", (TopicNameIF) this);
  }

  @Override
  public void syncAfterMerge(IdentityIF source, IdentityIF target) {
    syncFieldsAfterMerge(source, target, LF_topic, LF_type, LF_reifier, LF_scope);
  }
}
