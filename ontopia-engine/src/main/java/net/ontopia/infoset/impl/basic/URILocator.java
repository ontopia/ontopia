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
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * PUBLIC: A Uniform Resource Identifier locator. Only URI locators
 * should be used with this locator class. The notation is 'URI'.<p>
 *
 * The address is always normalized by the constructor. The address
 * given to the constructor <b>must</b> be absolute.<p>
 */
public class URILocator implements LocatorIF, Externalizable {
  public static final String NOTATION = "URI";

  private static final long serialVersionUID = 201705011133L;
  
  protected URI address;
  
  /**
   * INTERNAL: No-argument constructor used by serialization. Do not
   * use this constructor in application code.
   */
  public URILocator() {
  }
  
  private URILocator(URI uri) {
    this.address = uri.normalize();
  }
  
  /**
   * PUBLIC: Creates a URILocator representing the URI given. Note
   * that the URI string should be in external form, and that it
   * must be absolute.
   */
  public URILocator(String address) throws URISyntaxException {
    this(new URI(address));
    
    if (this.address.toString().isEmpty()) {
      throw new URISyntaxException(address, "URILocator's address cannot be empty");
    }
    
    if (!this.address.isAbsolute()) {
      throw new URISyntaxException(address, "URILocator's address must be absolute");
    }
  }

  /**
   * PUBLIC: Creates a URILocator representing the URL given.
   */
  public URILocator(URL url) {
    try {
      this.address = url.toURI().normalize();
    } catch (URISyntaxException u) {
      throw new OntopiaRuntimeException(u); // very unlikely
    }
  }

  /**
   * PUBLIC: Creates a URILocator containing a file URL referring
   * to the file represented by the File object.<p>
   *
   * @since 1.3.4
   */
  public URILocator(File file) {
    this(file.toURI());
  }

  // --------------------------------------------------------------------------
  // LocatorIF implementation
  // --------------------------------------------------------------------------
  
  @Override
  public String getNotation() {
    return NOTATION;
  }

  @Override
  public String getAddress() {
    return address.toString();
  }
  
  @Override
  public LocatorIF resolveAbsolute(String rel) {
    try {
      // empty relative means start of the document as per rfc2396
      if (StringUtils.isEmpty(rel)) {
        return new URILocator(new URI(address.getScheme(), address.getSchemeSpecificPart(), null));
      }

      URI part = new URI(rel);
      
      // specific case: 'foo:bar'.resolve(#foo)
      if (!part.isAbsolute() && part.getSchemeSpecificPart().isEmpty() && (part.getFragment() != null)) {
        return new URILocator(new URI(address.getScheme(), address.getSchemeSpecificPart(), part.getFragment()));
      }
    } catch (URISyntaxException e) {
      // ignore and let fallback handle it
    }
    
    return new URILocator(address.resolve(rel));
  }

  @Override
  public String getExternalForm() {
    return getAddress();
  }

  // --------------------------------------------------------------------------
  // Misc
  // --------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return address.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof URILocator) {
      return address.equals( ((URILocator) object).address);
    } else if (object instanceof LocatorIF) {
      LocatorIF l = (LocatorIF) object;
      return NOTATION.equals(l.getNotation()) && getAddress().equals(l.getAddress());
    }
    return false;
  }
  
  @Override
  public String toString() {
    return getNotation() + "|" + getAddress();
  }
  
  /**
   * INTERNAL: returns the internal URI state of this locator.
   */
  public URI getUri() {
    return address;
  }

  // --------------------------------------------------------------------------
  // Externalization
  // --------------------------------------------------------------------------
  
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(getAddress());
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    try {
      address = new URI(in.readUTF());
    } catch (URISyntaxException ex) {
      throw new IOException(ex);
    }
  }

  // --------------------------------------------------------------------------
  // Utility method
  // --------------------------------------------------------------------------

  /**
   * PUBLIC: Parses the URI and returns an instance of URILocator if
   * the URI is valid. If the URI is invalid null is returned.
   *
   * @since 3.0
   */
  public static URILocator create(String uriAddress) {
    try {
      return new URILocator(uriAddress);
    } catch (URISyntaxException e) {
      return null;
    }
  }
}
