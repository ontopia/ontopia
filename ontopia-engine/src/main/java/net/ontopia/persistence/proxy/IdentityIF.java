
package net.ontopia.persistence.proxy;


/**
 * INTERNAL: Interface used to represent data store object
 * identity. The identity has two parts; the type of object and an
 * ordered list of primary key components. Note that an identity
 * instance should always be immutable.
 * 
 * Warning: Implementations of this class must all have the same 
 * hashCode() behaviour.
 */

public interface IdentityIF extends Cloneable {

  /**
   * INTERNAL: Returns the type of object. The returned value
   * indicates the classification of the identified object. See also
   * {@link PersistentIF#_p_getType()}.
   */
  public Object getType();

  /**
   * INTERNAL: Returns the number of primary key components that the
   * identity has.
   */
  public int getWidth();

  /**
   * INTERNAL: Returns the primary key component with the specified index.
   */
  public Object getKey(int index);

  /**
   * INTERNAL: Creates an object instance of the type defined by this
   * identity.
   */
  public Object createInstance() throws Exception;
  // FIXME: Consider moving this method elsewhere.

  public Object clone();

}
