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
import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
  
/**
 * INTERNAL: The read-only rdbms variant name implementation.
 */
public class ReadOnlyVariantName extends ReadOnlyTMObject implements VariantNameIF {
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public ReadOnlyVariantName() {  
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return VariantName.fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return VariantName.CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : VariantName.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // TopicNameIF implementation
  // ---------------------------------------------------------------------------
  
  public TopicIF getTopic() {
    TopicNameIF name = getTopicName();
    if (name == null)
      return null;
    return name.getTopic();
  }

  public TopicNameIF getTopicName() {
    return (TopicNameIF)loadField(VariantName.LF_name);
  }

  public LocatorIF getDataType() {
    return (LocatorIF)loadField(VariantName.LF_datatype);    
  }

  public void setDataType(LocatorIF datatype) {
    throw new ReadOnlyException();
  }

  public String getValue() {
    return (String)loadField(VariantName.LF_value);    
  }

  public void setValue(String value) {
    setValue(value, DataTypes.TYPE_STRING);
  }

  public void setValue(String value, LocatorIF datatype) {
    throw new ReadOnlyException();
  }

  public Reader getReader() {
    throw new UnsupportedOperationException();
  }

  public void setReader(Reader value, long length, LocatorIF datatype) {
    throw new UnsupportedOperationException();
  }
  
  public LocatorIF getLocator() {
    if (!DataTypes.TYPE_URI.equals(getDataType())) return null;
    String value = getValue();
    return (value == null ? null : URILocator.create(value));
  }
  
  public void setLocator(LocatorIF locator) {
    throw new ReadOnlyException();
  }

  public long getLength() {
    Number length = (Number)loadField(VariantName.LF_length);
    long len = (length == null ? 0 : length.longValue());
    if (len < 0)
      return len * -1L;
    else
      return len;
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  public Collection<TopicIF> getScope() {
    return (Collection<TopicIF>) loadCollectionField(VariantName.LF_scope);
  }

  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    return (TopicIF)loadField(VariantName.LF_reifier);
  }
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyVariantName", (VariantNameIF)this);
  }

}





