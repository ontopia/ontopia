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

package net.ontopia.topicmaps.impl.rdbms;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.AbstractLocator;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * INTERNAL: An abstract locator implementation used for representing
 * locators within the rdbms implementation.<p>
 *
 * No normalization or absolutization is done.<p>
 */

public class RDBMSLocator extends AbstractLocator implements Externalizable {
  protected String address;

  public RDBMSLocator() {
  }

  public RDBMSLocator(LocatorIF locator) {
    this(locator.getAddress());
  }

  private RDBMSLocator(String address) {
    if (address == null)
      throw new NullPointerException("The locator address cannot be null.");
    this.address = address;
  }

  // ---------------------------------------------------------------------------
  // LocatorIF implementation
  // ---------------------------------------------------------------------------
  
  public String getNotation() {
    return "URI";
  }

  public String getAddress() {
    return address;
  }
  
  public LocatorIF resolveAbsolute(String address) {
    // FIXME: should use static method instead of creating URILocator instance
    try {
      return new URILocator(this.address).resolveAbsolute(address);
    } catch (java.net.MalformedURLException e) {
      // use RDBMS locator
    }
    // Since this locator is general we cannot make the address
    // absolute, so we'll just return a new locator with the same
    // address instead.
    return new RDBMSLocator(address);
  }

  public String getExternalForm() {
    // FIXME: should use static method instead of creating URILocator instance
    try {
      return new URILocator(this.address).getExternalForm();
    } catch (java.net.MalformedURLException e) {
      // use existing address
    }
    // this locator is general so we don't know how to do this
    return address;
  }

  // ---------------------------------------------------------------------------
  // Object implementation
  // ---------------------------------------------------------------------------

  public int hashCode() {
    return address.hashCode();
  }

  public boolean equals(Object object) {
    try {
      LocatorIF locator = (LocatorIF)object;
      return address.equals(locator.getAddress());
    } catch (ClassCastException e) {
      return false; // In case the object is not a locator
    } catch (NullPointerException e) {
      return false; // In case the object is null
    }
  }
  
  // ---------------------------------------------------------------------------
  // Materialization
  // ---------------------------------------------------------------------------

  public String _getAddress() {
    return address;
  }

  public void _setAddress(String address) {
    this.address = address;
  }

  // ---------------------------------------------------------------------------
  // Externalization
  // ---------------------------------------------------------------------------
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(address);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    address = in.readUTF();
  }  
}
