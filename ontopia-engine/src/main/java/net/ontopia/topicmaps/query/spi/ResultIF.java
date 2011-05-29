
package net.ontopia.topicmaps.query.spi;

import java.util.Map;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * EXPERIMENTAL: Interface used by process predicates to add new
 * result rows.
 *
 * @since 4.0
 */
public interface ResultIF {
  
  /**
   * EXPERIMENTAL: Add the given row to the result.
   */
  public void add(Object[] row);
  
}
