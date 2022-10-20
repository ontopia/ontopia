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
 * with only a single long key. See {@link IdentityIF}.
 */

public final class LongIdentity implements IdentityIF, Externalizable {

  private Class<?> type;
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
  public LongIdentity(Class<?> type, long key) {
    this.type = type;
    this.key = key;
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

  public long getLongKey() {
    return key;
  }
  
  @Override
  public Object createInstance() throws Exception {
    return ((Class)type).newInstance();
  }
  
  @Override
  public int hashCode() {
    int hashcode = 1 + type.hashCode();
    return 31*hashcode + (int)(key ^ (key >> 32));
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof LongIdentity) {
      LongIdentity other = (LongIdentity)object;

      return key == other.key &&
             type.equals(other.type);
    } else if (object instanceof AtomicIdentity) {
      AtomicIdentity other = (AtomicIdentity)object;
      
      // compare key
      Object okey = other.getKey(0);
      if (okey instanceof Long) {
        return ((Long)okey).longValue() == key &&
          type.equals(other.getType());
      } else {
        return false;
      }
      
    } else if (object instanceof IdentityIF) {
      IdentityIF other = (IdentityIF)object;      
      return (other.getWidth() == 1 &&
          getKey(0).equals(other.getKey(0)) &&
          type.equals(other.getType()));

    } else {
      return false;
    }
  }
  
  @Override
  public String toString() {
    return "<LongIdentity [" + key + "] " + type + ">";
  }

  // -----------------------------------------------------------------------------
  // Externalization
  // -----------------------------------------------------------------------------
  
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(type);
    out.writeLong(key);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    type = (Class<?>) in.readObject();
    key = in.readLong();
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
