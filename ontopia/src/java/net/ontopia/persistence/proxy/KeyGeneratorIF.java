// $Id: KeyGeneratorIF.java,v 1.9 2005/07/12 09:37:39 grove Exp $

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






