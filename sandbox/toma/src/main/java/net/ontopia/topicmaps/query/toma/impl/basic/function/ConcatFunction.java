package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Concatenate a collection to a string.
 */
public class ConcatFunction extends AbstractAggregateFunction {
  private String concatStr;
  
  public ConcatFunction() {
    super("CONCAT", 1);
  }

  public Object aggregate(Collection<?> values) throws InvalidQueryException {
    StringBuffer sb = new StringBuffer();
    
    int cnt = values.size();
    for (Object val : values) {
      sb.append(Stringifier.toString(val));
      if (--cnt > 0) {
        sb.append(concatStr);
      }
    }
   
    return sb.toString();
  }

  @Override
  public boolean validate() throws AntlrWrapException {
    super.validate();
    
    if (parameters.size() != 1) {
      throw new AntlrWrapException(new InvalidQueryException(
          "The 'concat' function needs to have 1 parameter."));
    }

    concatStr = parameters.get(0);
    return true;
  }
}
