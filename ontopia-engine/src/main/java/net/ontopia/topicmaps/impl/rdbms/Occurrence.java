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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.ContentReader;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.OnDemandValue;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.io.IOUtils;

/**
 * INTERNAL: The rdbms occurrence implementation.
 */

public class Occurrence extends TMObject implements OccurrenceIF {
  
  public static final String CLASS_INDICATOR = "O";

  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------

  protected static final int LF_topic = 2;
  protected static final int LF_scope = 3;
  protected static final int LF_type = 4;
  protected static final int LF_datatype = 5;
  protected static final int LF_length = 6;
  protected static final int LF_hashcode = 7;
  protected static final int LF_value = 8;
  protected static final int LF_reifier = 9;
  protected static final String[] fields = {"sources", "topicmap", "topic", "scope", "type", "datatype", "length", "hashcode", "value", "reifier"};

  @Override
  public void detach() {
    detachCollectionField(LF_sources);
    detachField(LF_topicmap);
    detachField(LF_topic);
    detachField(LF_reifier);
    detachCollectionField(LF_scope);
    detachField(LF_type);
    detachField(LF_datatype);
    detachField(LF_length);
    detachField(LF_hashcode);
    detachField(LF_value);
  }
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public Occurrence() {  
  }

  public Occurrence(TransactionIF txn) {
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
  // OccurrenceIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public void remove() {
    Topic parent = (Topic)getTopic();
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      parent.removeOccurrence(this);
    }
  }

  @Override
  public TopicIF getTopic() {
    return this.<TopicIF>loadField(LF_topic);
  }

  /**
   * INTERNAL: Set the topic that the occurrence belongs to. [parent]
   */
  protected void setTopic(TopicIF topic) {
    // Set parent topic map
    setTopicMap((topic == null ? null : (TopicMap)topic.getTopicMap()));
    // Notify transaction
    valueChanged(LF_topic, topic, true);    
  }

  protected void setTopicMap(TopicMap topicmap) {
    // Notify transaction
    transactionChanged(topicmap);
    valueChanged(LF_topicmap, topicmap, true);
  }

  @Override
  public LocatorIF getDataType() {
    return this.<LocatorIF>loadField(LF_datatype);    
  }

  protected void setDataType(LocatorIF datatype) {
    LocatorIF _datatype = new DataTypeLocator(datatype);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_SET_DATATYPE, _datatype, getDataType());
    // Notify transaction
    valueChanged(LF_datatype, _datatype, true);
  }

  @Override
  public String getValue() {
    Object value = loadField(LF_value);
    if (value instanceof String) {
      return (String) value;
    } else if (value instanceof OnDemandValue) {
      OnDemandValue odv = (OnDemandValue)value;
      try {
        Reader r = (Reader)odv.getValue(_p_getTransaction());
        try {
          return IOUtils.toString(r);
        } finally {
          r.close();
        }
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    } else if (value != null) {
      throw new OntopiaRuntimeException("Occurrence value cannot be non-null at this point: " + value);
    } else {
      return null; // FIXME: or possibly something else
    }
  }

  @Override
  public void setValue(String value) {
    setValue(value, DataTypes.TYPE_STRING);
  }

  @Override
  public void setValue(String value, LocatorIF datatype) {
    Objects.requireNonNull(value, "Occurrence value must not be null.");
    Objects.requireNonNull(datatype, "Occurrence value datatype must not be null.");
    if (!"URI".equals(datatype.getNotation()))
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    setValue(value, datatype, value.length(), value.hashCode());
  }
  
  private void setValue(Object value, LocatorIF datatype, long length, long hashcode) {
    setDataType(datatype);
    valueChanged(LF_length, new Long(length), true);
    valueChanged(LF_hashcode, new Long(hashcode), true);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_SET_VALUE, value, getValue());
    // Notify transaction
    valueChanged(LF_value, value, true);
  }

  @Override
  public Reader getReader() {
    Object value = loadField(LF_value);
    if (value instanceof String) {
      return new StringReader((String)value);
    } else if (value instanceof OnDemandValue) {
      OnDemandValue odv = (OnDemandValue)value;
      return (Reader)odv.getValue(_p_getTransaction());
    } else if (value != null) {
      throw new OntopiaRuntimeException("Occurrence value cannot be non-null at this point: " + value);
    } else {
      return null; // FIXME: or possibly something else
    }
  }

  @Override
  public void setReader(Reader value, long length, LocatorIF datatype) {
    Objects.requireNonNull(value, "Occurrence value must not be null.");
    Objects.requireNonNull(datatype, "Occurrence value datatype must not be null.");
    if (length < 0)
      throw new OntopiaRuntimeException("Length of reader is negative.");
    if (!"URI".equals(datatype.getNotation()))
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    setValue(new OnDemandValue(new ContentReader(value, length)), datatype, length, length);
  }

  @Override
  public LocatorIF getLocator() {
    if (!DataTypes.TYPE_URI.equals(getDataType()))
      return null;
    String value = getValue();
    return (value == null ? null : URILocator.create(value));
  }
  
  @Override
  public void setLocator(LocatorIF locator) {
    Objects.requireNonNull(locator, "Occurrence locator must not be null.");
    if (!"URI".equals(locator.getNotation()))
      throw new ConstraintViolationException("Only locators with notation 'URI' are supported: " + locator);
    setValue(locator.getAddress(), DataTypes.TYPE_URI);
  }

  @Override
  public long getLength() {
    Number length = this.<Number>loadField(LF_length);
    long len = (length == null ? 0 : length.longValue());
    if (len < 0)
      return len * -1L;
    else
      return len;
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
    Objects.requireNonNull(theme, "null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_ADD_THEME, theme, null);
    // Notify transaction
    valueAdded(LF_scope, theme, true);
  }

  @Override
  public void removeTheme(TopicIF theme) {
    Objects.requireNonNull(theme, "null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_REMOVE_THEME, null, theme);
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
    Objects.requireNonNull(type, "Occurrence type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_SET_TYPE, type, getType());
    // Notify transaction
    valueChanged(LF_type, type, true);
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
    if (_reifier != null)
      CrossTopicMapException.check(_reifier, this);
    if (DuplicateReificationException.check(this, _reifier)) { return; }
    // Notify listeners
    Topic reifier = (Topic) _reifier;
    Topic oldReifier = (Topic) getReifier();
    fireEvent(ReifiableIF.EVENT_SET_REIFIER, reifier, oldReifier);
    valueChanged(LF_reifier, reifier, true);
    if (oldReifier != null)
      oldReifier.setReified(null);
    if (reifier != null)
      reifier.setReified(this);
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  @Override
  public String toString() {
    return ObjectStrings.toString("rdbms.Occurrence", (OccurrenceIF)this);
  }

  @Override
  public void syncAfterMerge(IdentityIF source, IdentityIF target) {
    super.syncFieldsAfterMerge(source, target, LF_topic, LF_type, LF_reifier, LF_scope);
  }
}
