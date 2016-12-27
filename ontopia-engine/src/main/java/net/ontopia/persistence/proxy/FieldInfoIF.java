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

package net.ontopia.persistence.proxy;


/**
 * INTERNAL: A field descriptor-like class that is used by the RDBMS
 * proxy implementation to access the information it needs about the
 * object relational field descriptor in an optimized manner.<p>
 *
 * A field info is also able to perform the read and write operations
 * required by field handlers. In addition it contains an optimized
 * representation of the related field descriptor.
 *
 * The field info implementations also knows how to interact with the
 * data repository using the FieldHandlerIF interface.<p>
 */

public interface FieldInfoIF extends FieldHandlerIF {

  /**
   * Flag indicating that the field represents a 1:1 relationship.
   */
  int ONE_TO_ONE = 1;

  /**
   * Flag indicating that the field represents a 1:M relationship.
   */

  int ONE_TO_MANY = 2;

  /**
   * Flag indicating that the field represents a M:M relationship.
   */
  int MANY_TO_MANY = 3;

  /**
   * INTERNAL: Gets the field name.
   */
  String getName();

  /**
   * INTERNAL: Gets the value field index of this field. This is the
   * id (index) used by transactions and persistent instances to refer
   * to this particular object field.
   */
  int getIndex();

  /**
   * INTERNAL: Gets the field cardinality.
   */
  int getCardinality();

  /**
   * INTERNAL: Returns true if this field is read-only field.
   */
  boolean isReadOnly();

  /**
   * INTERNAL: Returns true if the field is a collection field (has a
   * cardinality of 1:1 or 1:M).
   */
  boolean isCollectionField();

  //! NOTE: This method is now part of the FieldHandlerIF interface.
  //! /**
  //!  * INTERNAL: Returns true if the field references an object identity
  //!  * field.
  //!  */
  //! public boolean isIdentityField();

  /**
   * INTERNAL: Returns true if the field is a primitive field. Field
   * value must be of primitive type.
   */
  boolean isPrimitiveField();
  
  /**
   * INTERNAL: Returns true if the field is a reference field. Field
   * value must be of identifiable type.
   */
  boolean isReferenceField();

  /**
   * INTERNAL: Returns true if the field is an aggregate field. Field
   * value must be of aggregate type.
   */
  boolean isAggregateField();
  
  /**
   * INTERNAL: Gets the class info for the field's object type.
   */
  ClassInfoIF getParentClassInfo();

  /**
   * INTERNAL: Gets the class info for the field's value type. Note
   * that primitive value classes don't have a class info.
   */
  ClassInfoIF getValueClassInfo();

  /**
   * INTERNAL: Gets the field value class. For primitive fields the
   * primitive wrapper class is returned.
   */
  Class<?> getValueClass();
  
  /**
   * INTERNAL: Gets the table in which the field value is stored (aka
   * the master table).
   */
  String getTable();

  /**
   * INTERNAL: Returns the number of columns that the field spans.
   */
  //! public int getColumnCount();
  
  /**
   * INTERNAL: Returns the names of the columns that the field spans.
   */
  String[] getValueColumns();

  /**
   * INTERNAL: Gets the field value from the given object.
   */
  Object getValue(Object object) throws Exception;

  /**
   * INTERNAL: Sets the field value for the given object.
   */
  void setValue(Object object, Object value) throws Exception;

  /// -- Copied from FieldDescriptor:
  
  /**
   * INTERNAL: Gets the name of the table which needs to be joined to
   * order to access the field value from the master table.
   */
  String getJoinTable();

  /**
   * INTERNAL: Gets the columns in the join table that contains the
   * keys of the referencing object.
   */
  String[] getJoinKeys();

  /**
   * INTERNAL: Gets the columns in the join table that contains the
   * keys of the referenced object.
   */
  String[] getManyKeys();
  
}
