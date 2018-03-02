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
 * INTERNAL: Represents an expression in a JDO query. An expression
 * will return a boolean value when evaluated.
 */

public interface JDOExpressionIF {

  // Boolean expressions

  /**
   * INTERNAL: Constant referring to the {@link JDOAnd} class.
   */
  int AND = 1;
  /**
   * INTERNAL: Constant referring to the {@link JDOOr} class.
   */
  int OR = 2;
  /**
   * INTERNAL: Constant referring to the {@link JDONot} class.
   */
  int NOT = 3;
  /**
   * INTERNAL: Constant referring to the {@link JDOBoolean} class.
   */
  int BOOLEAN = 4;
  /**
   * INTERNAL: Constant referring to the {@link JDOValueExpression} class.
   */
  int VALUE_EXPRESSION = 5;

  // Operators

  /**
   * INTERNAL: Constant referring to the {@link JDOEquals} class.
   */
  int EQUALS = 101;
  /**
   * INTERNAL: Constant referring to the {@link JDONotEquals} class.
   */
  int NOT_EQUALS = 102;

  // Collection methods

  /**
   * INTERNAL: Constant referring to the {@link JDOContains} class.
   */
  int CONTAINS = 110;
  /**
   * INTERNAL: Constant referring to the {@link JDOIsEmpty} class.
   */
  int IS_EMPTY = 111;

  // String methods

  /**
   * INTERNAL: Constant referring to the {@link JDOStartsWith} class.
   */
  int STARTS_WITH = 201;
  /**
   * INTERNAL: Constant referring to the {@link JDOEndsWith} class.
   */
  int ENDS_WITH = 202;
  /**
   * INTERNAL: Constant referring to the {@link JDOLike} class.
   */
  int LIKE = 203;

  // Set operations

  /**
   * INTERNAL: Constant referring to the {@link JDOSetOperation} class.
   */
  int SET_OPERATION = 501;
  
  /**
   * INTERNAL: Returns the type of JDO expression indicated by one of
   * the constants in the {@link JDOExpressionIF} interface.
   */
  int getType();

  /**
   * INTERNAL: Allows the value to be visited. This method is used for
   * retrieval of nested data in expressions.
   */
  void visit(JDOVisitorIF visitor);
  
  //! /**
  //!  * INTERNAL: Returns the nested expression if any. If no subexpression
  //!  * null is returned.
  //!  */
  //! public JDOExpressionIF[] getNested();

  //! public int getValueArity();
  //! public JDOValueIF[] getValues();
  
}






