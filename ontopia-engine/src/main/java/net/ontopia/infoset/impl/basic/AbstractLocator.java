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

import java.io.Serializable;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: The abstract locator class. All locator implementations should
 * extend this class.
 */
@SuppressWarnings("serial")
public abstract class AbstractLocator implements LocatorIF, Serializable  {

  // -------------------------------------------------------------------------
  // LocatorIF implementation
  // -------------------------------------------------------------------------

  /**
   * PUBLIC: Two LocatorIFs are considered equal if they have the same
   * address and notation properties.
   */
  @Override
  public boolean equals(Object object) {
    try {
      LocatorIF locator = (LocatorIF)object;
      return getAddress().equals(locator.getAddress()) &&
	getNotation().equalsIgnoreCase(locator.getNotation());
    } catch (ClassCastException e) {
      return false; // In case the object is not a locator
    } catch (NullPointerException e) {
      return false; // In case the object is null
    }
  }

  /**
   * PUBLIC: Returns the hashcode of the address property. All
   * subclasses of AbstractLocator must use the same hashCode
   * implementation in order to guarantee interoperability. E.g. when
   * looking up LocatorIFs in Maps.
   */
  @Override
  public int hashCode() {
    return getAddress().hashCode();
  }
  
  @Override
  public String toString() {
    return getNotation() + "|" + getAddress();
  }
  
}





