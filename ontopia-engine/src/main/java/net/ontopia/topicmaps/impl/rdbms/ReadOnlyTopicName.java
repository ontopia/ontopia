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
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;

/**
 * INTERNAL: The read-only rdbms topic name implementation.
 */
public class ReadOnlyTopicName extends ReadOnlyTMObject implements TopicNameIF {

  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public ReadOnlyTopicName() {
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return TopicName.fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return TopicName.CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : TopicName.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // NameIF implementation
  // ---------------------------------------------------------------------------
  
  public TopicIF getTopic() {
    return (TopicIF)loadField(TopicName.LF_topic);
  }
  
  public String getValue() {
    return (String)loadField(TopicName.LF_value);    
  }
  
  public void setValue(String value) {
    throw new ReadOnlyException();
  }

  public Collection getVariants() {
    return loadCollectionField(TopicName.LF_variants);
  }

  void addVariant(VariantNameIF variant) {
    throw new ReadOnlyException();
  }

  void removeVariant(VariantNameIF variant) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  public Collection<TopicIF> getScope() {
    return (Collection<TopicIF>) loadCollectionField(TopicName.LF_scope);
  }

  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getType() {
    return (TopicIF)loadField(TopicName.LF_type);
  }

  public void setType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    return (TopicIF)loadField(TopicName.LF_reifier);
  }
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyTopicName", (TopicNameIF)this);
  }

}
