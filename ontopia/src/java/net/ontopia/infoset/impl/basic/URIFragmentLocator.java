
// $Id: URIFragmentLocator.java,v 1.7 2004/03/08 09:21:27 larsga Exp $

package net.ontopia.infoset.impl.basic;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;
import java.net.*;

/**
 * INTERNAL.
 */
public class URIFragmentLocator extends AbstractLocator {
  protected String address;       // shared
  protected String fragment;      // after '#'
  protected short  schemeEnd;     // the ':' char in the scheme part
  protected short  authorityEnd;  // last char in authority part
  protected short  lastSlash;     // last slash in directory path  
  
  protected URIFragmentLocator(String address, String fragment,
                               short schemeEnd, short authorityEnd,
                               short lastSlash) {
    this.address = address;
    this.fragment = fragment;
    this.schemeEnd = schemeEnd;
    this.authorityEnd = authorityEnd;
    this.lastSlash = lastSlash;
  }
  
  /**
   * INTERNAL: Returns the URI as a URL object.
   * @deprecated Because not all URIs can be represented as URL objects.
   */
  
  public URL getURL() throws MalformedURLException {
    return new URL(address + "#" + fragment);
  }
  
  // --------------------------------------------------------------------------
  // LocatorIF implementation
  // --------------------------------------------------------------------------
  
  public String getNotation() {
    return "URI";
  }

  public String getAddress() {
    return address + "#" + fragment;
  }
  
  public LocatorIF resolveAbsolute(String rel) {
    int length = rel.length();
    if (length == 0)
      return this;

    switch(rel.charAt(0)) {
    case '#':
      return new URIFragmentLocator(address, rel.substring(1), schemeEnd,
                                    authorityEnd, lastSlash);

    case '/':
      if (length != 1 && rel.charAt(1) == '/') { // begins with "//"
	if (authorityEnd == -1)
	  throw new OntopiaRuntimeException(new MalformedURLException("Base URI is not hierarchical"));
	return new URILocator(address.substring(0, schemeEnd+1) + rel,
			      schemeEnd, authorityEnd, lastSlash,
			      (short) address.length());
      } else
	// FIXME: should normalize absolute path
	return new URILocator(address.substring(0, authorityEnd) + rel,
			      schemeEnd, authorityEnd, lastSlash,
			      (short) address.length());
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
      if (ix < length || rel.equals(".") || rel.equals("..")) {
	if (lastSlash == -1) // no directory part
	  return new URILocator(address.substring(0, authorityEnd + 1) + rel);
	else
	  return new URILocator(address.substring(0, lastSlash + 1) + rel);
      }
      
      // there were no slashes, so this is a pure file name
      if (lastSlash == -1) // base has no directory part
	return new URILocator(address.substring(0, authorityEnd + 1) + rel,
			      schemeEnd, authorityEnd, lastSlash,
			      (short) address.length());
      else
	return new URILocator(address.substring(0, lastSlash + 1) + rel,
			      schemeEnd, authorityEnd, lastSlash,
			      (short) address.length());
    }
    catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public String getExternalForm() {
    return URILocator.toExternalForm(address + "#" + fragment);
  }

  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------
  /**
   * Parses the scheme part of a URI.
   * @return The index of the last char in the scheme, which will be ':' or
   *         -1 if there is no scheme.
   */
  
  private int getScheme(char[] uri) {
    int index = 0;
    while((index < uri.length) &&
          (uri[index] != '/') &&
          (uri[index] != '?') &&
          (uri[index] != ':') &&
          (uri[index] != '#'))
      index++;

    if (index == 0 || index >= uri.length || uri[index] != ':')
      return -1;
      
    return index;
  }

  public int hashCode() {
    // this hashCode() implementation returns the same value as
    
    //   return (address + "#" + fragment).hashCode();
    
    // would have done, by carefully mimicking the hash code algorithm
    // for java.lang.String objects given in the JDK javadoc. using
    // it gives a 10% speed increase on import of topic maps
    
    int hash = address.hashCode(); // base hash code

    hash = 31*hash + '#';

    String frag = fragment;
    int len = frag.length();
    for (int i = 0; i < len; i++)
      hash = 31*hash + frag.charAt(i);
    
    return hash;
  }

  public boolean equals(Object object) {
    try {
      if (object instanceof URIFragmentLocator) {
        URIFragmentLocator locator = (URIFragmentLocator)object;
        return fragment.equals(locator.fragment) && address.equals(locator.address);
      } else {
        LocatorIF locator = (LocatorIF)object;
        return (address + "#" + fragment).equals(locator.getAddress()) &&
          locator.getNotation().equals("URI");
      }
    } catch (ClassCastException e) {
      return false; // In case the object is not a locator
    } catch (NullPointerException e) {
      return false; // In case the object is null
    }
  }

}
