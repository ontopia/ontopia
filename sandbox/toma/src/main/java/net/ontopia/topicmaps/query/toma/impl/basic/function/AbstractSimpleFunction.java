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
package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicFunctionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractFunction;

/**
 * INTERNAL: Abstract base class for normal functions used for the
 * {@link BasicQueryProcessor}.
 */
public abstract class AbstractSimpleFunction extends AbstractFunction implements
    BasicFunctionIF {

  public AbstractSimpleFunction(String name, int maxParameters) {
    super(name, maxParameters, false);
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    // all functions need to have exactly one child
    if (getChildCount() != 1) {
      throw new InvalidQueryException(
          "Function '" + getName() + "' does not have a child.");
    }

    // get the child and evaluate it
    BasicExpressionIF child = (BasicExpressionIF) getChild(0);
    ResultSet rs = child.evaluate(context);

    // for each row, execute the function on the last column
    for (Object r : rs) {
      Row row = (Row) r;
      row.setLastValue(evaluate(row.getLastValue()));
    }

    return rs;
  }
  
  public Object aggregate(Collection<?> values) throws InvalidQueryException {
    throw new InvalidQueryException(
        "Function '" + getName() + "' is not an aggregate function.");
  }
}
