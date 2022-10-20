/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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

  private static final long serialVersionUID = 5662829503505256457L;

  private Class<?> type;
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
  public AtomicIdentity(Class<?> type, Object key) {
    this.type = type;
    //! if (key == null) throw new NullPointerException("AtomicIdentity key cannot be null.");
    this.key = key;
    this.hashcode = computeHashCode();
  }

  @Override
  public Class<?> getType() {
    return type;
  }

  @Override
  public int getWidth() {
    return 1;
  }

  @Override
  public Object getKey(int index) {
    return key;
  }

  @Override
  public Object createInstance() throws Exception {
    return ((Class)type).newInstance();
  }
  
  @Override
  public int hashCode() {
    return hashcode;
  }

  private int computeHashCode() {
    // Note: This is the same implementation as in java.util.List
    int hashcode = 1 + type.hashCode();
    return 31*hashcode + key.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof LongIdentity) {
      if (!(key instanceof Long)) {
        return false;
      }
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
  
  @Override
  public String toString() {
    return "<AtomicIdentity [" + key + "] " + type + ">";
  }

  // -----------------------------------------------------------------------------
  // Externalization
  // -----------------------------------------------------------------------------
  
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(type);
    out.writeObject(key);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    type = (Class<?>)in.readObject();
    key = in.readObject();
    this.hashcode = computeHashCode();
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      // Ignore
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
