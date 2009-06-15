
// $Id: AtomicIdentity.java,v 1.11 2005/09/20 11:24:19 grove Exp $

package net.ontopia.persistence.proxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used for representing data store object identities
 * with only a single key. See {@link IdentityIF}.
 */

public final class AtomicIdentity implements IdentityIF, Externalizable {

  static final long serialVersionUID = 5662829503505256457L;

  private Object type;
  private Object key;
  private int hashcode;

  /**
   * INTERNAL: Constructor that is used for externalization purposes
   * only.
   */
  public AtomicIdentity() {
  }
  
  /**
   * INTERNAL: Creates an identity instance of the given type with the
   * given key.
   */
  public AtomicIdentity(Object type, Object key) {
    this.type = type;
    //! if (key == null) throw new NullPointerException("AtomicIdentity key cannot be null.");
    this.key = key;
    this.hashcode = computeHashCode();
  }

  public Object getType() {
    return type;
  }

  public int getWidth() {
    return 1;
  }

  public Object getKey(int index) {
    return key;
  }

  public Object createInstance() throws Exception {
    return ((Class)type).newInstance();
  }
  
  public int hashCode() {
    return hashcode;
  }

  private int computeHashCode() {
    // Note: This is the same implementation as in java.util.List
    int hashcode = 1 + type.hashCode();
    return 31*hashcode + key.hashCode();
  }

  public boolean equals(Object object) {
    if (object instanceof LongIdentity) {
      if (!(key instanceof Long)) return false;
      LongIdentity other = (LongIdentity)object;
      return ((Long)key).longValue() == other.getLongKey() &&
             type.equals(other.getType());

    } else if (object instanceof AtomicIdentity) {
      AtomicIdentity other = (AtomicIdentity)object;
      return hashcode == other.hashcode &&
             key.equals(other.key) &&
             type.equals(other.type);
      
    } else if (object instanceof IdentityIF) {
      IdentityIF other = (IdentityIF)object;      
      return (other.getWidth() == 1 &&
          key.equals(other.getKey(0)) &&
          type.equals(other.getType()));

    } else {
      return false;
    }
  }
  
  public String toString() {
    return "<AtomicIdentity [" + key + "] " + type + ">";
  }

  // -----------------------------------------------------------------------------
  // Externalization
  // -----------------------------------------------------------------------------
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(type);
    out.writeObject(key);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    type = in.readObject();
    key = in.readObject();
    this.hashcode = computeHashCode();
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
