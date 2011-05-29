
package net.ontopia.persistence.proxy;

  
/**
 * INTERNAL: Interface for generating new object identities.
 */

public interface KeyGeneratorIF {

  /**
   * INTERNAL: Generates a new object identity of the specified object
   * type.
   */
  public IdentityIF generateKey(Object type);
  
}






