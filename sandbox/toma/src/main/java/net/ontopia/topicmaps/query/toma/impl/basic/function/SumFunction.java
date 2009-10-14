package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Calculates the sum of a collection. Only works for numbers.
 */
public class SumFunction extends AbstractAggregateFunction {
  
  public SumFunction() {
    super("SUM", 0);
  }

  public Object aggregate(Collection<?> values) throws InvalidQueryException {
    double sum = 0.0d;
    for (Object val : values) {
      try {
        if (val != null) {
          sum += Double.parseDouble(ToNumFunction.convertToNumber(val));
        }
      } catch (NumberFormatException e) {
        // TODO: check design decision
        // If the conversion fails, ignore it 
      }
    }
   
    return new Double(sum);
  }

  public boolean validate() throws AntlrWrapException {
    return true;
  }
}
