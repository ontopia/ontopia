
// $Id: WrappedIdentity.java,v 1.1 2005/09/20 11:24:19 grove Exp $

package net.ontopia.persistence.proxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used for wrapping other identities. See {@link IdentityIF}.
 */

public final class WrappedIdentity implements IdentityIF, Externalizable {

  private IdentityIF wrapped;

  /**
   * INTERNAL: Constructor that is used for externalization purposes
   * only.
   */
  public WrappedIdentity() {
  }
  
  /**
   * INTERNAL: Creates an identity instance that wraps another identity.
   */
  public WrappedIdentity(IdentityIF wrapped) {
    this.wrapped = wrapped;
  }

  /**
   * INTERNAL: Factory method that wraps the identity in a
   * WrappedIdentity if the identity itself is not a WrappedIdentity.
   */
  public static final IdentityIF wrap(IdentityIF identity) {
    if (identity instanceof WrappedIdentity)
      return identity;
    else
      return new WrappedIdentity(identity);
  }

  public Object getType() {
    return wrapped.getType();
  }

  public int getWidth() {
    return wrapped.getWidth();
  }

  public Object getKey(int index) {
    return wrapped.getKey(index);
  }
  
  public Object createInstance() throws Exception {
    return wrapped.createInstance();
  }
  
  public int hashCode() {
    return wrapped.hashCode();
  }

  public boolean equals(Object object) {
    return wrapped.equals(object);
  }
  
  public String toString() {
    return wrapped.toString();
  }

  // -----------------------------------------------------------------------------
  // Externalization
  // -----------------------------------------------------------------------------
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(wrapped);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    wrapped = (IdentityIF)in.readObject();
  }

  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      // Ignore
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
