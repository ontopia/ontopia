package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Identifies the minimum value in a collection. Only works for numbers.
 */
public class MinFunction extends AbstractAggregateFunction {
  
  public MinFunction() {
    super("MIN", 0);
  }

  public Object aggregate(Collection<?> values) throws InvalidQueryException {
    double min = Double.MAX_VALUE;
    for (Object val : values) {
      try {
        if (val != null) {
          min = Math.min(min, Double.parseDouble(ToNumFunction
              .convertToNumber(val)));
        }
      } catch (NumberFormatException e) {
        // TODO: check design decision
        // If the conversion fails, ignore it 
      }
    }
   
    // if no valid value could be found, return 0
    if (min == Double.MAX_VALUE) {
      min = 0.0d;
    }
    
    return new Double(min);
  }

  public boolean validate() throws AntlrWrapException {
    return true;
  }
}
