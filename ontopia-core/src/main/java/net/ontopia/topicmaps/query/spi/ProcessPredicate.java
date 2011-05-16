
// $Id: ProcessPredicate.java,v 1.3 2008/06/11 20:08:09 geir.gronmo Exp $

package net.ontopia.topicmaps.query.spi;

import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;

/**
 * EXPERIMENTAL: Base predicate that provides a simple interface for
 * implementing predicates. The process method lets one filter out,
 * pass through and/or produce new rows.<p>
 *
 * @since 4.0
 */
public abstract class ProcessPredicate extends JavaPredicate {

  public final QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {
    // validate arguments
    PredicateSignature sign = PredicateSignature.getSignature(this);
    sign.verifyBound(matches, arguments, this);

    // do actual work
    Result result = new Result(matches, arguments);
    result.process(this);
    return result.getResult();
  }

  static class Result implements ResultIF {

    QueryMatches input;
    QueryMatches output;
    
    int[] argindexes;
    boolean[] boundparams;

    int rownum;
    
    Result(QueryMatches matches, Object[] arguments) {
      this.input = matches;
      this.output = new QueryMatches(this.input);
      
      this.argindexes = new int[arguments.length];
      this.boundparams = new boolean[arguments.length];
      
      for (int i=0; i < arguments.length; i++) {
        this.argindexes[i] = this.input.getIndex(arguments[i]); 
        this.boundparams[i] = this.input.bound(argindexes[i]);
      }        
    }

    public QueryMatches getResult() {
      return output;
    }
      
    public void process(ProcessPredicate pred) throws InvalidQueryException {
      Object[] row = new Object[argindexes.length];
      for (rownum = 0; rownum <= input.last; rownum++) {
        
        for (int i=0; i < argindexes.length; i++) {
          row[i] = input.data[rownum][argindexes[i]];
        }
        
        pred.process(row, boundparams, this);
      }
    }
      
    public void add(Object[] row) {
      if (output.last+1 == output.size) 
        output.increaseCapacity();
      output.last++;

      // clone existing row
      Object[] newRow = (Object[]) input.data[rownum].clone();

      // update argument columns
      for (int i=0; i < row.length; i++) {
        if (!boundparams[i])
          newRow[this.argindexes[i]] = row[i];
      }
      
      output.data[output.last] = newRow;
    }
  }

  /**
   * EXPERIMENTAL: Processes the input row and pushes result rows to the result.
   */
  public abstract void process(Object[] row, boolean[] boundparams, ResultIF result) throws InvalidQueryException;
  
}
