package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Identifies the maximum of a collection. Only works for numbers.
 */
public class MaxFunction extends AbstractAggregateFunction {
  
  public MaxFunction() {
    super("MAX", 0);
  }

  public Object aggregate(Collection<?> values) throws InvalidQueryException {
    double max = Double.MIN_VALUE;
    for (Object val : values) {
      try {
        if (val != null) {
          max = Math.max(max, Double.parseDouble(ToNumFunction
              .convertToNumber(val)));
        }
      } catch (NumberFormatException e) {
        // TODO: check design decision
        // If the conversion fails, ignore it 
      }
    }
    
    // if no valid value could be found, return 0
    if (max == Double.MIN_VALUE) {
      max = 0.0d;
    }
    
    return new Double(max);
  }
  
  public boolean validate() throws AntlrWrapException {
    return true;
  }
}
