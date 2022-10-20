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

package net.ontopia.infoset.impl.basic;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: A generic locator class. Addresses of any notation can be
 * used with this locator class. No normalization or absolutization is
 * done.
 */

public class GenericLocator extends AbstractLocator implements Externalizable {

  private static final long serialVersionUID = 3066225165288209215L;

  protected String notation;
  protected String address;

  /**
   * INTERNAL: No-argument constructor used by serialization. Do not
   * use this constructor in application code.
   */
  public GenericLocator() {    
  }

  public GenericLocator(String notation, String address)
    throws IllegalArgumentException {
    
    if (notation == null) {
      throw new IllegalArgumentException("The notation argument cannot be null.");
    }
    if (address == null) {
      throw new IllegalArgumentException("The address argument cannot be null.");
    }
    this.notation = notation;
    this.address = address;
  }

  // -----------------------------------------------------------------------------
  // LocatorIF implementation
  // -----------------------------------------------------------------------------
  
  @Override
  public String getNotation() {
    return notation;
  }

  @Override
  public String getAddress() {
    return address;
  }

  @Override
  public LocatorIF resolveAbsolute(String address) {
    // Since this locator is general we cannot make the address
    // absolute, so we'll just return a new locator with the same
    // address instead.
    return new GenericLocator(notation, address);
  }

  @Override
  public String getExternalForm() {
    // in a generic locator we don't know the syntax rules, so we
    // have to just return the string as we got it
    return address;
  }

  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return address.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    try {
      LocatorIF locator = (LocatorIF)object;
      return address.equals(locator.getAddress()) &&
        notation.equalsIgnoreCase(locator.getNotation());
    } catch (ClassCastException e) {
      return false; // In case the object is not a locator
    } catch (NullPointerException e) {
      return false; // In case the object is null
    }
  }

  // -----------------------------------------------------------------------------
  // Externalization
  // -----------------------------------------------------------------------------
  
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(notation);
    out.writeUTF(address);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    notation = in.readUTF();
    address = in.readUTF();
  }
  
}
