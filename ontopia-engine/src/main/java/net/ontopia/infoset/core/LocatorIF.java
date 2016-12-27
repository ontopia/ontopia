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

package net.ontopia.infoset.core;

/**
 * PUBLIC: Locators that refer to resources. Implementations of this
 * interface must implement the toString(), equals() and hashCode()
 * methods. Note that the result of toString() must include not just
 * the locator itself but also the notation name. Implementations
 * should always be immutable. 
 */
public interface LocatorIF {
  
  /**
   * PUBLIC: Gets the locator notation. The default notation is URI.
   * Ontopia will never use notation names which begin with 'x-'.
   * Notation names are case-insensitive.<p>
	 * 
	 * Note that only the URI notation is supported in release OKS 4.0
   * and newer.
   */
  String getNotation();
  
  /**
   * PUBLIC: Returns the locator address in absolute and normalized
   * form. Whether addresses are case-sensitive or not depends on the
   * locator notation.
   */
  String getAddress();
  
  /**
   * PUBLIC: Given a locator address string that is relative to this
   * locator, return an absolute locator. If the input locator address
   * is absolute the returned locator object will simply contain that
   * absolute locator address.
   */
  LocatorIF resolveAbsolute(String address);

  /**
   * PUBLIC: Returns the address of the locator in external form; that
   * is, with special characters that need to be escaped escaped using
   * the escape syntax of the locator notation.
   *
   * @since 2.0.3
   */
  String getExternalForm();
}
