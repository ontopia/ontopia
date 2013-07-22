/*
 * #!
 * Ontopoly Editor
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

package ontopoly.model;

import java.util.Collection;

/**
 * Represents a populated field attached to an instance topic.
 */
public final class FieldInstance {
  private Topic instance;
  private FieldAssignment fieldAssignment;

  public FieldInstance(Topic instance, FieldAssignment fieldAssignment) {
    this.instance = instance;
    this.fieldAssignment = fieldAssignment;
  }

  /**
   * Returns the assigned field of which this is an instance.
   */
  public FieldAssignment getFieldAssignment() {
    return fieldAssignment;
  }

  /**
   * Returns the topic this field instance is attached to.
   */
  public Topic getInstance() {
    return instance;
  }

  /**
   * Returns a collection of Objects.
   */
  public Collection<? extends Object> getValues() {
    return getFieldAssignment().getFieldDefinition().getValues(getInstance());
  }

  /**
   * Add a new FieldValue object.
   */
  public void addValue(Object value, LifeCycleListener listener) {
    getFieldAssignment().getFieldDefinition().addValue(getInstance(), value, listener);
  }

  /**
   * Removes the value.
   */
  public void removeValue(Object value, LifeCycleListener listener) {
    getFieldAssignment().getFieldDefinition().removeValue(getInstance(), value, listener);
  }

}
