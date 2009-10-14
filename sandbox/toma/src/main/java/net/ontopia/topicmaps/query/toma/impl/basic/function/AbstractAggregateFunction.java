package net.ontopia.topicmaps.query.toma.impl.basic.function;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicFunctionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractFunction;

/**
 * INTERNAL: Abstract base class for normal functions used for the
 * {@link BasicQueryProcessor}.
 */
public abstract class AbstractAggregateFunction extends AbstractFunction
    implements BasicFunctionIF {

  public AbstractAggregateFunction(String name, int maxParameters) {
    super(name, maxParameters, true);
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    // all functions need to have exactly one child
    if (getChildCount() != 1) {
      throw new InvalidQueryException("Function '" + getName()
          + "' does not have a child.");
    }

    // get the child and evaluate it
    BasicExpressionIF child = (BasicExpressionIF) getChild(0);
    return child.evaluate(context);
  }

  public String evaluate(Object obj) throws InvalidQueryException {
    throw new InvalidQueryException("Function '" + getName()
        + "' is an aggregate function.");
  }
}
