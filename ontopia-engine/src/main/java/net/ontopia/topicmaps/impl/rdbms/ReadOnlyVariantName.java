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
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public int _p_getFieldCount() {
    return VariantName.fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public String getClassIndicator() {
    return VariantName.CLASS_INDICATOR;
  }

  @Override
  public String getObjectId() {
    return (id == null ? null : VariantName.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // TopicNameIF implementation
  // ---------------------------------------------------------------------------
  
  @Override
  public TopicIF getTopic() {
    TopicNameIF name = getTopicName();
    if (name == null) {
      return null;
    }
    return name.getTopic();
  }

  @Override
  public TopicNameIF getTopicName() {
    return this.<TopicNameIF>loadField(VariantName.LF_name);
  }

  @Override
  public LocatorIF getDataType() {
    return this.<LocatorIF>loadField(VariantName.LF_datatype);    
  }

  public void setDataType(LocatorIF datatype) {
    throw new ReadOnlyException();
  }

  @Override
  public String getValue() {
    return this.<String>loadField(VariantName.LF_value);    
  }

  @Override
  public void setValue(String value, LocatorIF datatype) {
    throw new ReadOnlyException();
  }

  @Override
  public Reader getReader() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setReader(Reader value, long length, LocatorIF datatype) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void setLocator(LocatorIF locator) {
    throw new ReadOnlyException();
  }

  @Override
  public long getLength() {
    Number length = this.<Number>loadField(VariantName.LF_length);
    long len = (length == null ? 0 : length.longValue());
    if (len < 0) {
      return len * -1L;
    } else {
      return len;
    }
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public Collection<TopicIF> getScope() {
    return this.<TopicIF>loadCollectionField(VariantName.LF_scope);
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
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getReifier() {
    return this.<TopicIF>loadField(VariantName.LF_reifier);
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
    return ObjectStrings.toString("rdbms.ReadOnlyVariantName", (VariantNameIF)this);
  }

}





