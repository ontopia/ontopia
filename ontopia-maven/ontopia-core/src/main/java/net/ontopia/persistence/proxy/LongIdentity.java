
// $Id: LongIdentity.java,v 1.1 2005/09/20 11:24:19 grove Exp $

package net.ontopia.persistence.proxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used for representing data store object identities
 * with only a single long key. See {@link IdentityIF}.
 */

public final class LongIdentity implements IdentityIF, Externalizable {

  private Object type;
  private long key;

  /**
   * INTERNAL: Constructor that is used for externalization purposes
   * only.
   */
  public LongIdentity() {
  }
  
  /**
   * INTERNAL: Creates an identity instance of the given type with the
   * given key.
   */
  public LongIdentity(Object type, long key) {
    this.type = type;
    this.key = key;
  }

  public Object getType() {
    return type;
  }

  public int getWidth() {
    return 1;
  }

  public Object getKey(int index) {
    return new Long(key);
  }

  public long getLongKey() {
    return key;
  }
  
  public Object createInstance() throws Exception {
    return ((Class)type).newInstance();
  }
  
  public int hashCode() {
    int hashcode = 1 + type.hashCode();
    return 31*hashcode + (int)(key ^ (key >> 32));
  }

  public boolean equals(Object object) {
    if (object instanceof LongIdentity) {
      LongIdentity other = (LongIdentity)object;

      return key == other.key &&
             type.equals(other.type);
    } else if (object instanceof AtomicIdentity) {
      AtomicIdentity other = (AtomicIdentity)object;
      
      // compare key
      Object okey = other.getKey(0);
      if (okey instanceof Long)
        return ((Long)okey).longValue() == key &&
          type.equals(other.getType());
      else
        return false;
      
    } else if (object instanceof IdentityIF) {
      IdentityIF other = (IdentityIF)object;      
      return (other.getWidth() == 1 &&
          getKey(0).equals(other.getKey(0)) &&
          type.equals(other.getType()));

    } else {
      return false;
    }
  }
  
  public String toString() {
    return "<LongIdentity [" + key + "] " + type + ">";
  }

  // -----------------------------------------------------------------------------
  // Externalization
  // -----------------------------------------------------------------------------
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(type);
    out.writeLong(key);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    type = in.readObject();
    key = in.readLong();
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
