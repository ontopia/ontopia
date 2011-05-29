
package net.ontopia.utils;

import java.util.Set;

/**
 * INTERNAL: Factory interface used by SetPoolIF to create new
 * PoolableSetIF instances.
 *
 * @since 2.0
 */

public interface PoolableSetFactoryIF {

  /**
   * INTERNAL: Returns a new empty poolable set.
   */
  public PoolableSetIF createSet();

  /**
   * INTERNAL: Returns a new poolable set that contains the given
   * elements.
   */
  public PoolableSetIF createSet(Set set);
  
  /**
   * INTERNAL: Returns a new poolable set that contains the given
   * elements plus the single object.
   */
  public PoolableSetIF createSetAdd(Set set, Object o);
  
  /**
   * INTERNAL: Returns a new poolable set that contains the given
   * elements minus the single object.
   */
  public PoolableSetIF createSetRemove(Set set, Object o);
  
}
