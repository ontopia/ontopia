
package net.ontopia.utils;

import java.util.Set;

/**
 * INTERNAL: Interface implemented by sets that can be pooled by a
 * SetPoolIF. This interface is used byte SetPoolIFs to manage the
 * life cycle of the sets that they manage.
 *
 * @since 2.0
 */
public interface PoolableSetIF extends Set {

  /**
   * INTERNAL: Return the current reference count. The reference count
   * is used to control the life cycle of the pooled set. The pool
   * instance that manages this set will use the reference count to
   * figure out when to drop the set from its pool.
   */
  public int getReferenceCount();
  
  /**
   * INTERNAL: Increment and return reference count.
   */
  public int referenced(SetPoolIF pool); // +1 and return reference count
  
  /**
   * INTERNAL: Decrement and return reference count.
   */
  public int dereferenced(SetPoolIF pool); // -1 and return reference count

  /**
   * INTERNAL: Returns true if the given set will be equal to this set
   * if the given object had been added to this set.
   */
  public boolean equalsAdd(Set set, Object add);

  /**
   * INTERNAL: Returns true if the given set will be equal to this set
   * if the given object had been removed from this set.
   */
  public boolean equalsRemove(Set set, Object remove);
  
}
