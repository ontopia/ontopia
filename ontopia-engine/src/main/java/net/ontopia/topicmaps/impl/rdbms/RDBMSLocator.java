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
import java.net.URI;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * INTERNAL: An abstract locator implementation used for representing
 * locators within the rdbms implementation.<p>
 *
 * No normalization or absolutization is done.<p>
 */

public class RDBMSLocator extends URILocator implements Externalizable {

  public RDBMSLocator() {
  }

  public RDBMSLocator(LocatorIF locator) {
    Objects.requireNonNull(locator, "The locator address cannot be null.");
    if (locator instanceof URILocator) {
      address = ((URILocator) locator).getUri();
    } else {
      address = URI.create(locator.getAddress()).normalize();
    }
  }

  // ---------------------------------------------------------------------------
  // Materialization
  // ---------------------------------------------------------------------------

  public String _getAddress() {
    return address.toString();
  }

  public void _setAddress(String address) {
    this.address = URI.create(address).normalize();
  }  
}
