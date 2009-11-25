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
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF.TYPE;

/**
 * INTERNAL: IN expression, checks whether the result of an expression matches
 * any of the specified values.
 * 
 * TODO: sub-select is not yet working.
 */
public class InExpression extends AbstractExpression implements
    BasicExpressionIF {

  public InExpression() {
    // an IN expression can have an arbitrary number of children
    super("IN", -1);
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    // if the value list is empty, we have nothing to do,
    // return an empty result.
    if (getChildCount() < 1) {
      return null;
    }

    // get the expression to be evaluated
    BasicExpressionIF left = (BasicExpressionIF) getChild(0);

    // the merged ResultSet of the expressions in the IN(...)
    ResultSet merged = merge(context);

    // Optimization:
    // If the left side of the expression is a sole variable
    // and the variable has not been bound yet, just bind it to the result of
    // the other expression.
    if (left instanceof PathExpression && ((PathExpression) left).isVariable()) {
      PathExpression path = (PathExpression) left;
      String name = path.getVariableName();
      if (context.getResultSet(name) == null) {
        merged.setColumnName(merged.getLastIndex(), name);
        context.addResultSet(merged);
        return merged;
      }
    }

    // check whether the merged ResultSet contains Strings
    boolean convertToString = false;
    if (merged.iterator().hasNext()) {
      Row r = merged.iterator().next();
      if (r.getLastValue() instanceof String) {
        convertToString = true;
      }
    }

    ResultSet rsLeft = left.evaluate(context);

    ResultSet result = new ResultSet(rsLeft);
    Row compareRow = merged.createRow();
    for (Row row : rsLeft) {
      Object val = row.getLastValue();
      if (convertToString) {
        val = Stringifier.toString(val);
      }
      compareRow.setLastValue(val);

      if (merged.containsRow(compareRow)) {
        result.addRow((Row) row);
      }
    }

    context.addResultSet(result);
    return result;
  }

  private ResultSet merge(LocalContext context) throws InvalidQueryException {
    ResultSet result = new ResultSet(1, false);
    result.setColumnName(0, "MERGE");

    for (int i = 1; i < getChildCount(); i++) {
      BasicExpressionIF expr = (BasicExpressionIF) getChild(i);
      ResultSet rs = expr.evaluate(context);

      for (Row r : rs) {
        Row newRow = result.createRow();
        newRow.setValue(0, r.getLastValue());
        result.addRow(newRow);
      }
    }

    return result;
  }

  @Override
  public boolean validate() throws AntlrWrapException {
    if (!super.validate()) {
      return false;
    }
    
    // check if all expressions return the same type
    PathElementIF.TYPE common = TYPE.UNKNOWN;
    for (int i = 1; i < getChildCount(); i++) {
      BasicExpressionIF expr = (BasicExpressionIF) getChild(i);
      PathElementIF.TYPE output;      
      if (expr instanceof PathExpression) {
        output = ((PathExpression) expr).output();
      } else if (expr instanceof LiteralExpression) {
        output = TYPE.STRING;
      } else {
        // TODO: include other expressions too.
        output = TYPE.UNKNOWN;
      }
      
      if (common == TYPE.UNKNOWN) {
        common = output;
      } else if (output != TYPE.UNKNOWN && common != output) {
        throw new AntlrWrapException(new InvalidQueryException(
            "Expressions used in IN clause have different result types."));
      }
    }
    return true;
  }
}
