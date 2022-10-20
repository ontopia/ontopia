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
import java.util.Arrays;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used for representing data store object identities
 * with more than a single key. See {@link IdentityIF}.
 */

public final class Identity implements IdentityIF, Externalizable {

  private static final long serialVersionUID = 5662829503505256457L;

  private Class<?> type;
  private Object[] keys;
  private int hashcode;

  /**
   * INTERNAL: Constructor that is used for externalization purposes
   * only.
   */
  public Identity() {
  }
  
  /**
   * INTERNAL: Creates an identity instance of the given type with the
   * given keys.
   */
  public Identity(Class<?> type, Object[] keys) {
    this.type = type;
    this.keys = keys;
    this.hashcode = computeHashCode();
  }

  @Override
  public Class<?> getType() {
    return type;
  }

  @Override
  public int getWidth() {
    return keys.length;
  }

  @Override
  public Object getKey(int index) {
    return keys[index];
  }

  @Override
  public Object createInstance() throws Exception {
    return ((Class)type).newInstance();
  }
  
  @Override
  public int hashCode() {
    return hashcode;
  }
  
  public int computeHashCode() {
    // Note: This is the same implementation as in java.util.List
    int hashcode = 1 + type.hashCode();
    for (int i=0; i < keys.length; i++) {
      hashcode = 31*hashcode + keys[i].hashCode();
    }
    return hashcode;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof IdentityIF) {
      // Compare types    
      IdentityIF other = (IdentityIF)object;
      
      // FIXME: Use Arrays.compare(Object[], Object[]) instead.
      
      // Compare array length
      int width = keys.length;
      if (width != other.getWidth()) {
        return false;
      }
      
      // Compare array elements
      for (int i=0; i < width; i++) {
	Object okey = other.getKey(i);
	if (!keys[i].equals(okey)) {
    return false;
  }
      }
      // Compare type
      return type.equals(other.getType());

    } else {
      return false;
    }
  }
  
  @Override
  public String toString() {
    return "<Identity " + Arrays.asList(keys) + " " + type + ">";
  }

  // -----------------------------------------------------------------------------
  // Externalization
  // -----------------------------------------------------------------------------
  
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(type);
    out.writeObject(keys);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    type = (Class<?>)in.readObject();
    keys = (Object[])in.readObject();
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
