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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.UniqueSet;

/**
 * INTERNAL: The basic topic name implementation.
 */
public class TopicName extends TMObject implements TopicNameIF {

  private static final long serialVersionUID = -7350019735868904034L;

  protected TopicIF reifier;
  protected String value;
  protected TopicIF type;
  protected UniqueSet<TopicIF> scope;
  protected Set<VariantNameIF> variants;

  TopicName(TopicMap tm) {
    super(tm);
  }

  // ---------------------------------------------------------------------------
  // NameIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getTopic() {
    return (TopicIF) parent;
  }

  /**
   * INTERNAL: Set the topic that the topic name belongs to. [parent]
   */
  protected void setTopic(Topic parent) {
    // Validate topic map
    if (parent != null && parent.topicmap != this.topicmap)
      throw new ConstraintViolationException(
          "Cannot move objects across topic maps: " + this.topicmap + " and "
              + parent.topicmap);

    // (De)reference pooled sets
    if (scope != null) {
      if (parent == null)
        topicmap.setpool.dereference(scope);
      else
        scope = topicmap.setpool.get(scope);
    }

    // Set parent
    this.parent = parent;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value) {
    Objects.requireNonNull(value, "Topic name value must not be null.");
    // Notify listeners
    fireEvent(TopicNameIF.EVENT_SET_VALUE, value, getValue());
    this.value = value;
  }

  @Override
  public Collection<VariantNameIF> getVariants() {
    if (variants == null)
      return Collections.emptyList();
    else
      return Collections.unmodifiableSet(variants);
  }

  protected void addVariant(VariantNameIF _variant) {
    VariantName variant = (VariantName) _variant;
    Objects.requireNonNull(variant, MSG_NULL_ARGUMENT);
    // Check to see if variant is already a member of this topic name
    if (variant.parent == this)
      return;
    // Check if used elsewhere.
    if (variant.parent != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    // Notify listeners
    fireEvent(TopicNameIF.EVENT_ADD_VARIANT, variant, null);
    // Set topic name property
    if (variants == null)
      variants = topicmap.cfactory.makeSmallSet();
    variant.setTopicName(this);
    // Add variant to list of variants
    variants.add(variant);

    // Add inherited themes to variant name
    if (scope != null && !scope.isEmpty())
      for (TopicIF theme : scope)
        variant._addTheme(theme, false);
  }

  protected void removeVariant(VariantNameIF _variant) {
    VariantName variant = (VariantName) _variant;
    Objects.requireNonNull(variant, MSG_NULL_ARGUMENT);
    // Check to see if variant is not a member of this topic name
    if (variant.parent != this)
      return;
    // Notify listeners
    fireEvent(TopicNameIF.EVENT_REMOVE_VARIANT, null, variant);

    // Remove inherited themes from variant name
    if (scope != null && !scope.isEmpty())
      for (TopicIF theme : scope)
        variant._removeTheme(theme, false);
    
    // Unset topic name property
    variant.setTopicName(null);
    // Remove variant from list of variants
    if (variants == null)
      return;
    variants.remove(variant);
  }

  @Override
  public void remove() {
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      ((Topic) parent).removeTopicName(this);
    }
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public Collection<TopicIF> getScope() {
    // Return scope defined on this object
    return (scope == null ? Collections.<TopicIF>emptyList() : scope);
  }

  @Override
  public void addTheme(TopicIF theme) {
    Objects.requireNonNull(theme, MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(TopicNameIF.EVENT_ADD_THEME, theme, null);
    // Add theme to scope
    if (scope == null) {
      scope = topicmap.setpool.get(Collections.<TopicIF>emptySet());
    }
    scope = topicmap.setpool.add(scope, theme, true);

    // add theme to variants
    if (variants != null && !variants.isEmpty()) {
      Iterator<VariantNameIF> iter = variants.iterator();
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
    if (variants != null && !variants.isEmpty()) {
      Iterator<VariantNameIF> iter = variants.iterator();
      while (iter.hasNext()) {
        VariantName v = (VariantName) iter.next();
        v._removeTheme(theme, false);
      }
    }

    // Remove theme from scope
    if (scope == null)
      return;
    scope = topicmap.setpool.remove(scope, theme, true);
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getType() {
    return type;
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
    this.type = type;
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
    return reifier;
  }

  @Override
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null)
      CrossTopicMapException.check(_reifier, this);
    if (DuplicateReificationException.check(this, _reifier)) { return; }
    // Notify listeners
    Topic reifier = (Topic) _reifier;
    Topic oldReifier = (Topic) getReifier();
    fireEvent(ReifiableIF.EVENT_SET_REIFIER, reifier, oldReifier);
    this.reifier = reifier;
    if (oldReifier != null)
      oldReifier.setReified(null);
    if (reifier != null)
      reifier.setReified(this);
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  @Override
  protected void fireEvent(String event, Object new_value, Object old_value) {
    if (parent == null || parent.parent == null)
      return;
    else
      topicmap.processEvent(this, event, new_value, old_value);
  }

  @Override
  protected boolean isConnected() {
    if (parent != null && parent.parent != null)
      return true;
    else
      return false;
  }

  @Override
  public String toString() {
    return ObjectStrings.toString("basic.TopicName", (TopicNameIF) this);
  }

}
