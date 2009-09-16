package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL:
 */
public interface BasicPathElementIF {

  /**
   * 
   * @param context
   * @param input
   * @return
   * @throws InvalidQueryException
   */
  public Collection<?> evaluate(LocalContext context, Object input)
      throws InvalidQueryException;
  
  public int getResultSize();
  public String[] getColumnNames();
}
