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
import net.ontopia.persistence.proxy.ContentReader;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.OnDemandValue;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.io.IOUtils;
  
/**
 * INTERNAL: The rdbms variant name implementation.
 */
public class VariantName extends TMObject implements VariantNameIF {
  
  public static final String CLASS_INDICATOR = "N";

  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------

  protected static final int LF_name = 2;
  protected static final int LF_scope = 3;
  protected static final int LF_datatype = 4;
  protected static final int LF_length = 5;
  protected static final int LF_hashcode = 6;
  protected static final int LF_value = 7;
  protected static final int LF_reifier = 8;
  protected static final String[] fields = {"sources", "topicmap", "name", "scope", "datatype", "length", "hashcode", "value", "reifier"};

  @Override
  public void detach() {
    detachCollectionField(LF_sources);
    detachField(LF_topicmap);
    detachField(LF_name);
    detachField(LF_reifier);
    detachCollectionField(LF_scope);
    detachField(LF_datatype);
    detachField(LF_length);
    detachField(LF_hashcode);
    detachField(LF_value);
  }
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public VariantName() {  
  }
  
  public VariantName(TransactionIF txn) {
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
  // TopicNameIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public void remove() {
    TopicName parent = (TopicName)getTopicName();
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      parent.removeVariant(this);
    }
  }
  
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
    return this.<TopicNameIF>loadField(LF_name);
  }

  /**
   * INTERNAL: Set the name that the variant name belongs to. [parent]
   */
  protected void setTopicName(TopicNameIF name) {
    // Set parent topic map
    setTopicMap((name == null ? null : (TopicMap)name.getTopicMap()));
    // Notify transaction
    valueChanged(LF_name, name, true);    
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
    fireEvent(VariantNameIF.EVENT_SET_DATATYPE, _datatype, getDataType());
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
      throw new OntopiaRuntimeException("VariantName value cannot be non-null at this point: " + value);
    } else {
      return null; // FIXME: or possibly something else
    }
  }

  @Override
  public void setValue(String value, LocatorIF datatype) {
    Objects.requireNonNull(value, "Variant value must not be null.");
    Objects.requireNonNull(datatype, "Variant value datatype must not be null.");
    if (!"URI".equals(datatype.getNotation())) {
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    }
    setValue(value, datatype, value.length(), value.hashCode());
  }
  
  private void setValue(Object value, LocatorIF datatype, long length, long hashcode) {
    setDataType(datatype);
    valueChanged(LF_length, length, true);
    valueChanged(LF_hashcode, hashcode, true);
    // Notify listeners
    fireEvent(VariantNameIF.EVENT_SET_VALUE, value, getValue());
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
      throw new OntopiaRuntimeException("VariantName value cannot be non-null at this point: " + value);
    } else {
      return null; // FIXME: or possibly something else
    }
  }

  @Override
  public void setReader(Reader value, long length, LocatorIF datatype) {
    Objects.requireNonNull(value, "Variant value must not be null.");
    Objects.requireNonNull(datatype, "Variant value datatype must not be null.");
    if (length < 0) {
      throw new OntopiaRuntimeException("Length of reader is negative.");
    }
    if (!"URI".equals(datatype.getNotation())) {
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    }
    setValue(new OnDemandValue(new ContentReader(value, length)), datatype, length, length);
  }

  @Override
  public long getLength() {
    Number length = this.<Number>loadField(LF_length);
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
    return this.<TopicIF>loadCollectionField(LF_scope);
  }

  @Override
  public void addTheme(TopicIF theme) {
    _addTheme(theme, true);
  }
  
  protected void _addTheme(TopicIF theme, boolean validate) {
    Objects.requireNonNull(theme, "null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(VariantNameIF.EVENT_ADD_THEME, theme, null);
    // Notify transaction
    valueAdded(LF_scope, theme, true);
  }

  @Override
  public void removeTheme(TopicIF theme) {
    _removeTheme(theme, true);
  }
  
  protected void _removeTheme(TopicIF theme, boolean validate) {
    Objects.requireNonNull(theme, "null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(VariantNameIF.EVENT_REMOVE_THEME, null, theme);
    // Notify transaction
    valueRemoved(LF_scope, theme, true);

    // complain if theme already defined on parent
    if (validate) {
      TopicNameIF parent = getTopicName();
      if (parent != null && parent.getScope().contains(theme)) {
        throw new ConstraintViolationException("Can't remove theme from variant when theme is declared on topic name value.");
      }
    }
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
    Topic reifier = (Topic)_reifier;
    Topic oldReifier = (Topic)getReifier();
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
    return ObjectStrings.toString("rdbms.VariantName", (VariantNameIF)this);
  }

  @Override
  public void syncAfterMerge(IdentityIF source, IdentityIF target) {
    syncFieldsAfterMerge(source, target, LF_name, LF_reifier, LF_scope);
  }
}





