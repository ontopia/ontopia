
package net.ontopia.topicmaps.query.utils;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * EXPERIMENTAL: Interface to be implemented by row mappers used by
 * the queryForList method on QueryWrapper. 
 * @since 3.4.4
 */
public interface RowMapperIF<T> {

  /**
   * EXPERIMENTAL: This method is called once for each row in query
   * results, and the returned object is added to the list returned by
   * queryForList.
   * @param result the query result object
   * @param rowno the number of this row in the current query result
   * (in zero-based counting)
   * @return an object to put into the query result list
   */
  public T mapRow(QueryResultIF result, int rowno);
  
}
