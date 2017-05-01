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

package net.ontopia.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * INTERNAL: Utilities for working with URIs.
 * @since 1.3.2
 */
public class URIUtils {

  /**
   * INTERNAL: Given a URILocator in the file: URI scheme, produce the
   * corresponding File object.
   * @since 1.4
   */
  public static File getURIFile(LocatorIF file)
    throws java.net.MalformedURLException {
    
    String address = file.getAddress();
    if (!file.getNotation().equals("URI"))
      throw new java.net.MalformedURLException("Not a URI: " + file);
    if (!address.substring(0, 5).equals("file:"))
      throw new java.net.MalformedURLException("Not a file URI: " + file);

    // FIXME: this method is not complete, since it does not support Windows!
    return new File(address.substring(5));
  }

  /**
   * INTERNAL: Return URILocator from uri string.
   */
  public static URILocator getURILocator(String uri) {
    try {
      return new URILocator(uri);
    } catch (MalformedURLException e) {
      // it wasn't a URI, so probably it was a reference to a non-existent file
      throw new OntopiaRuntimeException("Malformed URI:" + uri, e);
    }    
  }

  /**
   * INTERNAL: Use this method instead of File.toURL() to get URLs for files.
   */
  public static URL toURL(File file) {
    try {
      return file.toURI().toURL();
    } catch (MalformedURLException mufe) {
      throw new OntopiaRuntimeException(mufe); // impossible
    }
  }


}
