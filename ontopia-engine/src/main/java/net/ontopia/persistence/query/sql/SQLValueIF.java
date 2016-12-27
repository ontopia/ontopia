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

package net.ontopia.persistence.query.sql;

import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: Represents a SQL value.
 */

public interface SQLValueIF {

  /**
   * INTERNAL: Constant referring to the {@link SQLNull} class.
   */
  int NULL = 0;
  
  /**
   * INTERNAL: Constant referring to the {@link SQLTuple} class.
   */
  int TUPLE = 1;
  /**
   * INTERNAL: Constant referring to the {@link SQLColumns} class.
   */
  int COLUMNS = 2;
  /**
   * INTERNAL: Constant referring to the {@link SQLPrimitive} class.
   */
  int PRIMITIVE = 3;
  /**
   * INTERNAL: Constant referring to the {@link SQLParameter} class.
   */
  int PARAMETER = 4;
  /**
   * INTERNAL: Constant referring to the {@link SQLVerbatim} class.
   */
  int VERBATIM = 5;
  /**
   * INTERNAL: Constant referring to the {@link SQLFunction} class.
   */
  int FUNCTION = 6;

  /**
   * INTERNAL: Returns the value class type. The type is represented
   * by one of the constants in the {@link SQLValueIF} interface.
   */
  int getType();

  /**
   * INTERNAL: Returns the [column] arity of the value. The number
   * represents the number of "columns" the value spans, i.e. its
   * composite width.
   */
  int getArity();

  /**
   * INTERNAL: Returns the value arity of the value. This number
   * refers to the number of nested values this value contains
   * including itself. Most values therefore have a value arity of
   * 1. Nested values may have an arity higher than 1. SQLTuple is
   * currently the only nested value type.
   */
  int getValueArity();

  /**
   * INTERNAL: The <i>column</i> alias to use if this value is
   * included in the projection. The SQL select syntax is typically
   * like "select value as <calias> from foo".
   */
  String getAlias();

  /**
   * INTERNAL: Sets the column alias.
   */
  void setAlias(String alias);

  /**
   * INTERNAL: Returns true if this value is a reference to another.
   */
  boolean isReference();

  /**
   * INTERNAL: Returns the referenced value if one exists.
   */
  SQLValueIF getReference();

  /**
   * INTERNAL: Returns the value type.
   */
  Class getValueType();

  /**
   * INTERNAL: Sets the value type.
   */
  void setValueType(Class vtype);

  /**
   * INTERNAL: Returns the field handler for the columns.
   */
  FieldHandlerIF getFieldHandler();

  /**
   * INTERNAL: Sets the field handler for the value.
   */
  void setFieldHandler(FieldHandlerIF fhandler);
  
}
