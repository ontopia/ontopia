
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





