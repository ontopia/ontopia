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
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;

/**
 * INTERNAL: AND expression, returns the intersection of the ResultSets 
 * of its child expressions. 
 */
public class AndExpression extends AbstractBinaryExpression {
  
  public AndExpression() {
    super("AND");
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 2)
      return null;

    BasicExpressionIF left = (BasicExpressionIF) getChild(0);
    BasicExpressionIF right = (BasicExpressionIF) getChild(1);

    left.evaluate(context);
    ResultSet rs2 = right.evaluate(context);
    return rs2;
  }
}
