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

  public Class<?> getType() {
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
