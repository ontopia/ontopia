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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.LocatorInterningTable;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.UniqueSet;
import org.apache.commons.io.IOUtils;

/**
 * INTERNAL: The basic occurrence implementation.
 */

public class Occurrence extends TMObject implements OccurrenceIF {

  private static final long serialVersionUID = -7364980697913079915L;

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

  @Override
  public void remove() {
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      ((Topic)parent).removeOccurrence(this);
    }
  }

  @Override
  public TopicIF getTopic() {
    return (TopicIF)parent;
  }

  /**
   * INTERNAL: Set the topic that the occurrence belongs to. [parent]
   */
  protected void setTopic(Topic parent) {
    // Validate topic map
    if (parent != null && parent.topicmap != this.topicmap) {
      throw new ConstraintViolationException("Cannot move objects across topic maps: "
                                             + this.topicmap + " and " + parent.topicmap);
    }
    // (De)reference pooled sets
    if (scope != null) {
      if (parent == null) {
        topicmap.setpool.dereference(scope);
      } else {
        scope = topicmap.setpool.get(scope);
      }
    }

    // Set parent topic
    this.parent = parent;
  }

  @Override
  public LocatorIF getDataType() {
    return datatype;    
  }

  protected void setDataType(LocatorIF datatype) {
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_SET_DATATYPE, value, getDataType());
    this.datatype = LocatorInterningTable.intern(datatype);
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value, LocatorIF datatype) {
    Objects.requireNonNull(value, "Occurrence value must not be null.");
    Objects.requireNonNull(datatype, "Occurrence value datatype must not be null.");
    if (!"URI".equals(datatype.getNotation())) {
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    }
    setDataType(datatype);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_SET_VALUE, value, getValue());
    this.value = value;
  }

  @Override
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

  @Override
  public void setReader(Reader value, long length, LocatorIF datatype) {
    Objects.requireNonNull(value, "Occurrence value must not be null.");
    Objects.requireNonNull(datatype, "Occurrence value datatype must not be null.");
    if (!"URI".equals(datatype.getNotation())) {
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    }
    try {
      setValue(IOUtils.toString(value), datatype);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  //! Public void setInputStream(InputStream value, long length, LocatorIF datatype) {
  //!   throw new UnsupportedOperationException();
  //! }
  //! 
  //! public boolean isBinary() {
  //!   return Objects.equals(getDataType(), DataTypes.TYPE_BINARY);
  //! }

  @Override
  public long getLength() {
    return (value == null ? 0 : value.length());
  }

  // -----------------------------------------------------------------------------
  // ScopedIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public Collection<TopicIF> getScope() {
    // Return scope defined on this object
    return (scope == null ? Collections.<TopicIF>emptySet() : scope);
  }
  @Override
  public void addTheme(TopicIF theme) {
    Objects.requireNonNull(theme, "null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_ADD_THEME, theme, null);
    // Add theme to scope
    if (scope == null) {
      scope = topicmap.setpool.get(Collections.<TopicIF>emptySet());
    }
    scope = topicmap.setpool.add(scope, theme, true);
  }
  @Override
  public void removeTheme(TopicIF theme) {
    Objects.requireNonNull(theme, "null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_REMOVE_THEME, null, theme);
    // Remove theme from scope
    if (scope == null) {
      return;
    }
    scope = topicmap.setpool.remove(scope, theme, true);
  }

  // -----------------------------------------------------------------------------
  // TypedIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public TopicIF getType() {
    return type;
  }

  @Override
  public void setType(TopicIF type) {
    Objects.requireNonNull(type, "Occurrence type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent(OccurrenceIF.EVENT_SET_TYPE, type, getType());
    this.type = type;
  }
  
  // -----------------------------------------------------------------------------
  // ReifiableIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public TopicIF getReifier() {
    return reifier;
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
    this.reifier = reifier;
    if (oldReifier != null) {
      oldReifier.setReified(null);
    }
    if (reifier != null) {
      reifier.setReified(this);
    }
  }

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  @Override
  protected void fireEvent(String event, Object new_value, Object old_value) {
    if (parent == null || parent.parent == null) {
      return;
    } else {
      topicmap.processEvent(this, event, new_value, old_value);
    }
  }

  @Override
  protected boolean isConnected() {
    if (parent != null && parent.parent != null) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return ObjectStrings.toString("basic.Occurrence", (OccurrenceIF)this);
  }
  
}

