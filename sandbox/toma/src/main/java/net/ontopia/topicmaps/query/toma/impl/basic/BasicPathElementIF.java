package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Specialization of the PathElementIF interface for the
 * BasicQueryProcessor implementation.
 */
public interface BasicPathElementIF {

  /**
   * Evaluate an path element based on the current context and the input value.
   * The result is a collection of values, as one input can generate multiple
   * outputs (e.g. names of a topic).
   * 
   * @param context the current processing context.
   * @param input the input value to be evaluated.
   * @return a Collection of results.
   * @throws InvalidQueryException if the path element could not be evaluated
   *           because of syntactic or semantic error in the query definition.
   */
  public Collection<?> evaluate(LocalContext context, Object input)
      throws InvalidQueryException;

  /**
   * Get the number of columns this path element will generate in the
   * evaluation.
   * 
   * @return the number of result columns.
   */
  public int getResultSize();
  
  /**
   * Get the names of the result columns this path element will generate.
   *  
   * @return an array containing the names of the result columns.
   */
  public String[] getColumnNames();
}
