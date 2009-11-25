/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.List;

import net.ontopia.topicmaps.query.toma.impl.utils.QueryOptimizerIF;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: represents a TOMA expression in the AST.
 */
public interface ExpressionIF extends ASTElementIF {
  /**
   * Adds an expression as a child to this expression.
   * 
   * @param expr the expression to be added as a child.
   * @throws AntlrWrapException if the expression is not allowed to have child
   *           expressions.
   */
  public void addChild(ExpressionIF expr) throws AntlrWrapException;

  /**
   * Get the number of child expressions.
   * 
   * @return the number of children.
   */
  public int getChildCount();

  /**
   * Get the child at the specified index.
   * 
   * @param idx the index of the child.
   * @return the child at the given index.
   */
  public ExpressionIF getChild(int idx);

  /**
   * Get the children of this expression as a {@link List}.
   * 
   * @return the list of children.
   */
  public List<ExpressionIF> getChilds();
  
  /**
   * Optimize this expression with the given optimizer.
   * 
   * @param optimizer the optimizer to be used.
   * @return the optimized expression to be used afterwards.
   */
  public ExpressionIF optimize(QueryOptimizerIF optimizer);
}
