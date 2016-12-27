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

/**
 * INTERNAL: Represents an expression in a SQL query.
 */

public interface SQLExpressionIF {
  
  /**
   * INTERNAL: Constant referring to the {@link SQLAnd} class.
   */
  int AND = 1;
  /**
   * INTERNAL: Constant referring to the {@link SQLOr} class.
   */
  int OR = 2;
  /**
   * INTERNAL: Constant referring to the {@link SQLNot} class.
   */
  int NOT = 3;

  /**
   * INTERNAL: Constant referring to the {@link SQLEquals} class.
   */
  int EQUALS = 101;
  /**
   * INTERNAL: Constant referring to the {@link SQLNotEquals} class.
   */
  int NOT_EQUALS = 102;
  /**
   * INTERNAL: Constant referring to the {@link SQLIsNull} class.
   */
  int IS_NULL = 103;
  /**
   * INTERNAL: Constant referring to the {@link SQLLike} class.
   */
  int LIKE = 104;
  /**
   * INTERNAL: Constant referring to the {@link SQLVerbatimExpression} class.
   */
  int VERBATIM = 106;
  
  /**
   * INTERNAL: Constant referring to the {@link SQLExists} class.
   */
  int EXISTS = 201;  
  /**
   * INTERNAL: Constant referring to the {@link SQLIn} class.
   */
  int IN = 202;
  /**
   * INTERNAL: Constant referring to the {@link SQLFalse} class.
   */
  int FALSE = 203;

  /**
   * INTERNAL: Constant referring to the {@link SQLJoin} class.
   */
  int JOIN = 301;

  /**
   * INTERNAL: Constant referring to the {@link SQLValueExpression} class.
   */
  int VALUE_EXPRESSION = 401;

  /**
   * INTERNAL: Constant referring to the {@link SQLSetOperation} class.
   */
  int SET_OPERATION = 501;
  
  /**
   * INTERNAL: Returns the expression type. The type is represented by
   * one of the constants in the {@link SQLExpressionIF} interface.
   */
  int getType();

  //! /**
  //!  * INTERNAL: Returns the nested expression if any. If no
  //!  * subexpression null is returned.
  //!  */
  //! public SQLExpressionIF[] getNested();
  
}
