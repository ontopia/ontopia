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

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.topicmaps.impl.utils.LocatorInterningTable;
import net.ontopia.utils.UniqueSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;

/**
 * INTERNAL: The basic occurrence implementation.
 */

public class Occurrence extends TMObject implements OccurrenceIF {

  static final long serialVersionUID = -7364980697913079915L;

  protected TopicIF reifier;
  protected TopicIF type;
  protected UniqueSet<TopicIF> scope;
  protected String value;
  protected LocatorIF datatype;
  
  Occurrence(TopicMap tm) {
    super(tm);
  }
  
  // -----------------------------------------------------------------------------
  // OccurrenceIF implementation
  // -----------------------------------------------------------------------------

  public void remove() {
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      ((Topic)parent).removeOccurrence(this);
    }
  }

  public TopicIF getTopic() {
    return (TopicIF)parent;
  }

  /**
   * INTERNAL: Set the topic that the occurrence belongs to. [parent]
   */
  void setTopic(Topic parent) {
    // Validate topic map
    if (parent != null && parent.topicmap != this.topicmap)
      throw new ConstraintViolationException("Cannot move objects across topic maps: "
                                             + this.topicmap + " and " + parent.topicmap);
    // (De)reference pooled sets
    if (scope != null) {
      if (parent == null)
        topicmap.setpool.dereference(scope);
      else
        scope = topicmap.setpool.get(scope);
    }

    // Set parent topic
    this.parent = parent;
  }

  public LocatorIF getDataType() {
    return datatype;    
  }

  protected void setDataType(LocatorIF datatype) {
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_SET_DATATYPE, value, getDataType());
    this.datatype = LocatorInterningTable.intern(datatype);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    setValue(value, DataTypes.TYPE_STRING);
  }

  public void setValue(String value, LocatorIF datatype) {
    if (value == null) 
      throw new NullPointerException("Occurrence value must not be null.");
    if (datatype == null) 
      throw new NullPointerException("Occurrence value datatype must not be null.");
    if (!"URI".equals(datatype.getNotation()))
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    setDataType(datatype);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_SET_VALUE, value, getValue());
    this.value = value;
  }

  public Reader getReader() {
    return (value == null ? null : new StringReader(value));
  }
  
  //! Public InputStream getInputStream() {
  //!   try {
  //!     return (value == null ? null : new ReaderInputStream(new StringReader(value), "utf-8"));
  //!   } catch (Exception e) {
  //!     throw new OntopiaRuntimeException(e);
  //!   }
  //! }

  public void setReader(Reader value, long length, LocatorIF datatype) {
    if (value == null) 
      throw new NullPointerException("Occurrence value must not be null.");
    if (datatype == null) 
      throw new NullPointerException("Occurrence value datatype must not be null.");
    if (!"URI".equals(datatype.getNotation()))
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    try {
      setValue(StreamUtils.readString(value, length), datatype);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  //! Public void setInputStream(InputStream value, long length, LocatorIF datatype) {
  //!   throw new UnsupportedOperationException();
  //! }
  //! 
  //! public boolean isBinary() {
  //!   return ObjectUtils.equals(getDataType(), DataTypes.TYPE_BINARY);
  //! }
  
  public LocatorIF getLocator() {
    if (!DataTypes.TYPE_URI.equals(getDataType())) return null;
    String value = getValue();
    return (value == null ? null : URILocator.create(value));
  }
  
  public void setLocator(LocatorIF locator) {
    if (locator == null) 
      throw new NullPointerException("Occurrence locator must not be null.");
    if (!"URI".equals(locator.getNotation()))
      throw new ConstraintViolationException("Only locators with notation 'URI' are supported: " + locator);
    setValue(locator.getAddress(), DataTypes.TYPE_URI);
  }

  public long getLength() {
    return (value == null ? 0 : value.length());
  }

  // -----------------------------------------------------------------------------
  // ScopedIF implementation
  // -----------------------------------------------------------------------------

  public Collection<TopicIF> getScope() {
    // Return scope defined on this object
    Collection<TopicIF> empty = Collections.emptySet();
    return (scope == null ? empty : scope);
  }
  public void addTheme(TopicIF theme) {
    if (theme == null) 
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_ADD_THEME, theme, null);
    // Add theme to scope
    if (scope == null) {
      Set<TopicIF> empty = Collections.emptySet();
      scope = topicmap.setpool.get(empty);
    }
    scope = topicmap.setpool.add(scope, theme, true);
  }
  public void removeTheme(TopicIF theme) {
    if (theme == null) 
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_REMOVE_THEME, null, theme);
    // Remove theme from scope
    if (scope == null)
      return;
    scope = topicmap.setpool.remove(scope, theme, true);
  }

  // -----------------------------------------------------------------------------
  // TypedIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getType() {
    return type;
  }

  public void setType(TopicIF type) {
    if (type == null) 
      throw new NullPointerException("Occurrence type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_SET_TYPE, type, getType());
    this.type = type;
  }
  
  // -----------------------------------------------------------------------------
  // ReifiableIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getReifier() {
    return reifier;
  }
  
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null) 
      CrossTopicMapException.check(_reifier, this);
    // Notify listeners
    Topic reifier = (Topic)_reifier;
    Topic oldReifier = (Topic)getReifier();
    fireEvent(ReifiableIF.EVENT_SET_REIFIER, reifier, oldReifier);
    this.reifier = reifier;
    if (oldReifier != null) oldReifier.setReified(null);
    if (reifier != null) reifier.setReified(this);
  }

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  protected void fireEvent(String event, Object new_value, Object old_value) {
    if (parent == null || parent.parent == null) return;
    else topicmap.processEvent(this, event, new_value, old_value);
  }

  protected boolean isConnected() {
    if (parent != null && parent.parent != null)
      return true;
    else
      return false;
  }

  public String toString() {
    return ObjectStrings.toString("basic.Occurrence", (OccurrenceIF)this);
  }
  
}

