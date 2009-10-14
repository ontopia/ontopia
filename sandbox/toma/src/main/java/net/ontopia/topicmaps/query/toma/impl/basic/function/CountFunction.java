package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Calculates the number of outputs from an expression.
 */
public class CountFunction extends AbstractAggregateFunction {
  
  public CountFunction() {
    super("COUNT", 0);
  }

  public Object aggregate(Collection<?> values) throws InvalidQueryException {
    return new Integer(values.size());
  }
}
