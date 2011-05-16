
// $Id: IdentityNotFoundException.java,v 1.2 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.proxy;


/**
 * INTERNAL: Thrown when an object was not found in the database.</p>
 */

public class IdentityNotFoundException extends PersistenceRuntimeException {

  protected IdentityIF identity;
  
  public IdentityNotFoundException(IdentityIF identity) {
    this("Identity not found in data repository: " + identity, identity);
  }

  public IdentityNotFoundException(String message, IdentityIF identity) {
    super(message);
    this.identity = identity;
  }

  public IdentityIF getIdentity() {
    return identity;
  }

}





