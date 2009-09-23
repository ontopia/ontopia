package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Calculates the number of outputs from an expression.
 */
public class CountFunction extends AbstractAggregateFunction {
  
  public CountFunction() {
    super("COUNT", 0);
  }

  public String evaluate(Object obj) throws InvalidQueryException {
    Collection col = (Collection) obj;
    int size = col.size();
    return String.valueOf(size);
  }

  public boolean validate() throws AntlrWrapException {
    return true;
  }
}
