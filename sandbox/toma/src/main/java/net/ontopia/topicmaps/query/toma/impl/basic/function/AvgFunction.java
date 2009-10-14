package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Calculates the average value of the outputs from an expression.
 * <p>
 * <b>Note</b>: This function is only defined for real values. In case the 
 * encapsulated expression returns something else, the result will be 0.  
 * </p>
 */
public class AvgFunction extends AbstractAggregateFunction {
  
  public AvgFunction() {
    super("AVG", 0);
  }

  public Object aggregate(Collection<?> values) throws InvalidQueryException {
    double sum = 0.0;
    for (Object val : values) {
      try {
        sum += Double.parseDouble(ToNumFunction.convertToNumber(val));
      } catch (NumberFormatException e) {
        // TODO: check design decision
        // If the conversion fails, ignore it 
      }
    }
    
    double avg = sum / Math.max(values.size(), 1);
    return new Double(avg);
  }

  public boolean validate() throws AntlrWrapException {
    return true;
  }
}
