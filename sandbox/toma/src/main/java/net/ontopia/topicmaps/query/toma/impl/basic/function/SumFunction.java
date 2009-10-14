package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Calculates the sum of a collection.
 * <p>
 * <b>Note</b>: This function is only defined for numbers. In case the 
 * encapsulated expression returns something else, the result will be 0.  
 * </p>
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
}
