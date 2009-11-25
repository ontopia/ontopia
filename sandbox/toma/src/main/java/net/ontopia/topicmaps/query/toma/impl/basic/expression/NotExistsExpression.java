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
import net.ontopia.topicmaps.query.toma.impl.basic.Row;

/**
 * INTERNAL: Not exists expression, returns all non-valid (null) results of
 * a specified child expression.  
 */
public class NotExistsExpression extends AbstractUnaryExpression {

  public NotExistsExpression() {
    super("NOTEXISTS");
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 1)
      return null;

    BasicExpressionIF child = (BasicExpressionIF) getChild(0);
    ResultSet rs = child.evaluate(context);

    ResultSet result = new ResultSet(rs);

    for (Row row : rs) {
      Object val = row.getValue(rs.getLastIndex());
      if (val == null) {
        result.addRow(row);
      }
    }

    context.addResultSet(result);
    return result;
  }
}
