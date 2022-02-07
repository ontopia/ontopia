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

import java.net.MalformedURLException;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL.
 */
@SuppressWarnings("serial")
public class URIFragmentLocator extends AbstractLocator {
  protected String address; // shared
  protected String fragment; // after '#'
  protected short schemeEnd; // the ':' char in the scheme part
  protected short authorityEnd; // last char in authority part
  protected short lastSlash; // last slash in directory path

  protected URIFragmentLocator(String address, String fragment,
      short schemeEnd, short authorityEnd, short lastSlash) {
    this.address = address;
    this.fragment = fragment;
    this.schemeEnd = schemeEnd;
    this.authorityEnd = authorityEnd;
    this.lastSlash = lastSlash;
  }

  // --------------------------------------------------------------------------
  // LocatorIF implementation
  // --------------------------------------------------------------------------

  @Override
  public String getNotation() {
    return "URI";
  }

  @Override
  public String getAddress() {
    return address + "#" + fragment;
  }

  @Override
  public LocatorIF resolveAbsolute(String rel) {
    int length = rel.length();
    if (length == 0)
      return this;

    switch (rel.charAt(0)) {
    case '#':
      return new URIFragmentLocator(address, rel.substring(1), schemeEnd,
          authorityEnd, lastSlash);

    case '/':
      if (length != 1 && rel.charAt(1) == '/') { // begins with "//"
        if (authorityEnd == -1)
          throw new OntopiaRuntimeException(new MalformedURLException(
              "Base URI is not hierarchical"));
        return new URILocator(address.substring(0, schemeEnd + 1) + rel,
            schemeEnd, authorityEnd, lastSlash, (short) address.length());
      } else
        // FIXME: should normalize absolute path
        return new URILocator(address.substring(0, authorityEnd) + rel,
            schemeEnd, authorityEnd, lastSlash, (short) address.length());
    } // no default needed; the rest of the method _is_ the default

    try {
      char[] relative = rel.toCharArray();

      // does the URI have a scheme?
      if (getScheme(relative) != -1)
        return new URILocator(rel);

      // scan for slashes in URI
      int ix;
      for (ix = 0; ix < length && relative[ix] != '/'; ix++)
        ;

      // there were slashes, use constructor for unnormalized URIs,
      // so that the normalizer resolves the directory for us
      // (also do this if rel is "." or "..")
      if (ix < length || ".".equals(rel) || "..".equals(rel)) {
        if (lastSlash == -1) // no directory part
          return new URILocator(address.substring(0, authorityEnd + 1) + rel);
        else
          return new URILocator(address.substring(0, lastSlash + 1) + rel);
      }

      // there were no slashes, so this is a pure file name
      if (lastSlash == -1) // base has no directory part
        return new URILocator(address.substring(0, authorityEnd + 1) + rel,
            schemeEnd, authorityEnd, lastSlash, (short) address.length());
      else
        return new URILocator(address.substring(0, lastSlash + 1) + rel,
            schemeEnd, authorityEnd, lastSlash, (short) address.length());
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public String getExternalForm() {
    return URILocator.toExternalForm(address + "#" + fragment);
  }

  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------
  /**
   * Parses the scheme part of a URI.
   * 
   * @return The index of the last char in the scheme, which will be ':' or -1
   *         if there is no scheme.
   */

  private int getScheme(char[] uri) {
    int index = 0;
    while ((index < uri.length) && (uri[index] != '/') && (uri[index] != '?')
        && (uri[index] != ':') && (uri[index] != '#'))
      index++;

    if (index == 0 || index >= uri.length || uri[index] != ':')
      return -1;

    return index;
  }

  @Override
  public int hashCode() {
    // this hashCode() implementation returns the same value as

    // return (address + "#" + fragment).hashCode();

    // would have done, by carefully mimicking the hash code algorithm
    // for java.lang.String objects given in the JDK javadoc. using
    // it gives a 10% speed increase on import of topic maps

    int hash = address.hashCode(); // base hash code

    hash = 31 * hash + '#';

    String frag = fragment;
    int len = frag.length();
    for (int i = 0; i < len; i++)
      hash = 31 * hash + frag.charAt(i);

    return hash;
  }

  @Override
  public boolean equals(Object object) {
    try {
      if (object instanceof URIFragmentLocator) {
        URIFragmentLocator locator = (URIFragmentLocator) object;
        return fragment.equals(locator.fragment)
            && address.equals(locator.address);
      } else {
        LocatorIF locator = (LocatorIF) object;
        return (address + "#" + fragment).equals(locator.getAddress())
            && locator.getNotation().equals("URI");
      }
    } catch (ClassCastException e) {
      return false; // In case the object is not a locator
    } catch (NullPointerException e) {
      return false; // In case the object is null
    }
  }

}
