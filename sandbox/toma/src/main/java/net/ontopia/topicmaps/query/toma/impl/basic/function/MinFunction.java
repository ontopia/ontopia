package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: 
 */
public class MinFunction extends AbstractAggregateFunction {
  
  public MinFunction() {
    super("MIN", 0);
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
