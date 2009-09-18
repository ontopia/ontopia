package net.ontopia.topicmaps.query.toma.impl.basic.function;

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
    super(name, maxParameters);
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    // all functions need to have exactly one child
    if (getChildCount() != 1)
      return null;

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
}
