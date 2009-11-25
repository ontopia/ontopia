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
package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathExpression;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.VariableDecl;

/**
 * INTERNAL: abstract base class for all binary expressions.
 */
public abstract class AbstractBinaryExpression extends AbstractExpression
    implements BasicExpressionIF {

  protected AbstractBinaryExpression(String name) {
    super(name, 2);
  }

  @Override
  public boolean validate() throws AntlrWrapException {
    if (!super.validate()) {
      return false;
    }

    // check the types of variables for cases like:
    // $var = $t.name
    ExpressionIF left = getChild(0);
    ExpressionIF right = getChild(1);
    if (left instanceof AbstractPathExpression
        && right instanceof AbstractPathExpression) {
      AbstractPathExpression leftPath = (AbstractPathExpression) left;
      AbstractPathExpression rightPath = (AbstractPathExpression) right;

      try {
        if (leftPath.isVariable()) {
          VariableDecl leftVar = leftPath.getVariableDeclaration();
          leftVar.constrainTypes(rightPath.output());
        } else if (rightPath.isVariable()) {
          VariableDecl rightVar = rightPath.getVariableDeclaration();
          rightVar.constrainTypes(leftPath.output());
        }
      } catch (InvalidQueryException e) {
        throw new AntlrWrapException(e);
      }
    }

    return true;
  }
}
