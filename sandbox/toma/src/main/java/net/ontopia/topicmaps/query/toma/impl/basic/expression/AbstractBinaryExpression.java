package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;

/**
 * INTERNAL: abstract base class for all binary expressions. 
 */
public abstract class AbstractBinaryExpression extends AbstractExpression 
  implements BasicExpressionIF {

  protected AbstractBinaryExpression(String name) {
    super(name, 2);
  }

  public Collection<?> evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    throw new InvalidQueryException(
        "Internal error, tried to evaluate the expression '" + getName()
            + "' with a given input.");
  }
}
