package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: 
 */
public class ConcatFunction extends AbstractAggregateFunction {
  
  public ConcatFunction() {
    super("CONCAT", 0);
  }

  public String evaluate(Object obj) throws InvalidQueryException {
    // TODO: implement
    Collection col = (Collection) obj;
    return "";
  }

  public boolean validate() throws AntlrWrapException {
    return true;
  }
}
