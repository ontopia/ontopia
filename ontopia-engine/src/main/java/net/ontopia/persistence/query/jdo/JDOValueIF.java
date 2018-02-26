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

package net.ontopia.persistence.query.jdo;

/**
 * INTERNAL: Represents a value in a JDO query. A value will return
 * the value it represents when evaluated.
 */

public interface JDOValueIF {

  /**
   * INTERNAL: Constant referring to the {@link JDONull} class.
   */
  int NULL = 0;

  /**
   * INTERNAL: Constant referring to the {@link JDOField} class.
   */
  int FIELD = 1;
  /**
   * INTERNAL: Constant referring to the {@link JDOVariable} class.
   */
  int VARIABLE = 2;
  /**
   * INTERNAL: Constant referring to the {@link JDOParameter} class.
   */
  int PARAMETER = 3;
  /**
   * INTERNAL: Constant referring to the {@link JDOPrimitive} class.
   */
  int PRIMITIVE = 4;
  /**
   * INTERNAL: Constant referring to the {@link JDOObject} class.
   */
  int OBJECT = 5;
  /**
   * INTERNAL: Constant referring to the {@link JDOCollection} class.
   */
  int COLLECTION = 6;
  
  /**
   * INTERNAL: Constant referring to the {@link JDOString} class.
   */
  int STRING = 7;

  /**
   * INTERNAL: Constant referring to the {@link JDONativeValue} class.
   */
  int NATIVE_VALUE = 8;
  /**
   * INTERNAL: Constant referring to the {@link JDOFunction} class.
   */
  int FUNCTION = 9;

  /**
   * INTERNAL: Returns the type of JDO value indicated by one of the
   * constants in the {@link JDOValueIF} interface.
   */
  int getType();

  /**
   * INTERNAL: Allows the value to be visited. This method is used for
   * retrieval of nested data in expressions.
   */
  void visit(JDOVisitorIF visitor);
  
}
