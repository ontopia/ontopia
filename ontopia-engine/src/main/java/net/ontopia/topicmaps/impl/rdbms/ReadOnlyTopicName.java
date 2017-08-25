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
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public int _p_getFieldCount() {
    return TopicName.fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public String getClassIndicator() {
    return TopicName.CLASS_INDICATOR;
  }

  @Override
  public String getObjectId() {
    return (id == null ? null : TopicName.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // NameIF implementation
  // ---------------------------------------------------------------------------
  
  @Override
  public TopicIF getTopic() {
    return this.<TopicIF>loadField(TopicName.LF_topic);
  }
  
  @Override
  public String getValue() {
    return this.<String>loadField(TopicName.LF_value);    
  }
  
  @Override
  public void setValue(String value) {
    throw new ReadOnlyException();
  }

  @Override
  public Collection<VariantNameIF> getVariants() {
    return this.<VariantNameIF>loadCollectionField(TopicName.LF_variants);
  }

  protected void addVariant(VariantNameIF variant) {
    throw new ReadOnlyException();
  }

  protected void removeVariant(VariantNameIF variant) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public Collection<TopicIF> getScope() {
    return this.<TopicIF>loadCollectionField(TopicName.LF_scope);
  }

  @Override
  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  @Override
  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getType() {
    return this.<TopicIF>loadField(TopicName.LF_type);
  }

  @Override
  public void setType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getReifier() {
    return this.<TopicIF>loadField(TopicName.LF_reifier);
  }
  
  @Override
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  @Override
  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyTopicName", (TopicNameIF)this);
  }

}
