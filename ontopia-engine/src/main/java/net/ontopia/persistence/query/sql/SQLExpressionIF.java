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
  public static final int AND = 1;
  /**
   * INTERNAL: Constant referring to the {@link SQLOr} class.
   */
  public static final int OR = 2;
  /**
   * INTERNAL: Constant referring to the {@link SQLNot} class.
   */
  public static final int NOT = 3;

  /**
   * INTERNAL: Constant referring to the {@link SQLEquals} class.
   */
  public static final int EQUALS = 101;
  /**
   * INTERNAL: Constant referring to the {@link SQLNotEquals} class.
   */
  public static final int NOT_EQUALS = 102;
  /**
   * INTERNAL: Constant referring to the {@link SQLIsNull} class.
   */
  public static final int IS_NULL = 103;
  /**
   * INTERNAL: Constant referring to the {@link SQLLike} class.
   */
  public static final int LIKE = 104;
  /**
   * INTERNAL: Constant referring to the {@link SQLVerbatimExpression} class.
   */
  public static final int VERBATIM = 106;
  
  /**
   * INTERNAL: Constant referring to the {@link SQLExists} class.
   */
  public static final int EXISTS = 201;  
  /**
   * INTERNAL: Constant referring to the {@link SQLIn} class.
   */
  public static final int IN = 202;
  /**
   * INTERNAL: Constant referring to the {@link SQLFalse} class.
   */
  public static final int FALSE = 203;

  /**
   * INTERNAL: Constant referring to the {@link SQLJoin} class.
   */
  public static final int JOIN = 301;

  /**
   * INTERNAL: Constant referring to the {@link SQLValueExpression} class.
   */
  public static final int VALUE_EXPRESSION = 401;

  /**
   * INTERNAL: Constant referring to the {@link SQLSetOperation} class.
   */
  public static final int SET_OPERATION = 501;
  
  /**
   * INTERNAL: Returns the expression type. The type is represented by
   * one of the constants in the {@link SQLExpressionIF} interface.
   */
  public int getType();

  //! /**
  //!  * INTERNAL: Returns the nested expression if any. If no
  //!  * subexpression null is returned.
  //!  */
  //! public SQLExpressionIF[] getNested();
  
}
