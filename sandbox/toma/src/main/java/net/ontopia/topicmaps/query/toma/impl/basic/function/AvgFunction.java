package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
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

  public String evaluate(Object obj) throws InvalidQueryException {
    Collection col = (Collection) obj;
    
    double sum = 0.0;
    for (Object val : col) {
      try {
        if (val != null) {
          sum += Double.parseDouble(Stringifier.toString(val));
        }
      } catch (NumberFormatException e) {
        //e.printStackTrace()
      }
    }
    
    double avg = sum / Math.max(col.size(), 1);
    return String.valueOf(avg);
  }

  public boolean validate() throws AntlrWrapException {
    return true;
  }
}
