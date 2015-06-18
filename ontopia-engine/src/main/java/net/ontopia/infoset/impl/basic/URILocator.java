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
import java.net.MalformedURLException;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;

/**
 * PUBLIC: A Uniform Resource Identifier locator. Only URI locators
 * should be used with this locator class. The notation is 'URI'.<p>
 *
 * The address is always normalized by the constructor. The address
 * given to the constructor <b>must</b> be absolute.<p>
 */
public class URILocator extends AbstractLocator implements Externalizable {
  protected String address;
  protected short  schemeEnd;     // the ':' char in the scheme part
  protected short  authorityEnd;  // last char in authority part
  protected short  lastSlash;     // last slash in directory path
  protected short  fragmentStart; // index of fragment '#'
  
  static {
    try {
      net.ontopia.net.data.Handler.install();
    } catch (SecurityException e) {
      // Fail silently if there are security issues.
    } catch (NoClassDefFoundError e) {
      // This happens on Google AppEngine, but is not really a problem
      // since the data-URL handler is rarely used. See
      // https://github.com/ontopia/ontopia/issues/118
    }
  }

  /**
   * INTERNAL: No-argument constructor used by serialization. Do not
   * use this constructor in application code.
   */
  public URILocator() {    
  }
  
  /**
   * PUBLIC: Creates a URILocator representing the URI given. Note
   * that the URI string should be in external form, and that it
   * must be absolute.
   */
  public URILocator(String address) throws MalformedURLException {
    this.address = normalize(address);
  }

  /**
   * PUBLIC: Creates a URILocator representing the URL given.
   */
  public URILocator(URL url) throws MalformedURLException {
    this.address = normalize(url.toExternalForm());
  }

  /**
   * PUBLIC: Creates a URILocator containing a file URL referring
   * to the file represented by the File object.<p>
   *
   * @since 1.3.4
   */
  public URILocator(File file) {
    try {
      String path = file.getAbsolutePath();
      if (File.separatorChar != '/')
        path = path.replace(File.separatorChar, '/');
      if (!path.startsWith("/"))
        path = "/" + path;
      if (!path.endsWith("/") && file.isDirectory())
        path = path + "/";

      path = "file:" + path;          
      this.address = normalize(escapeFilePath(path));
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException("INTERNAL ERROR: File " + file +
                                        " produced malformed URL", e);
    }
  }

  /**
   * INTERNAL: Special constructor used when resolving a URI relative 
   * to a base URI. Since the base URI is already normalized we can
   * avoid repeating the normalization, and thus save time.
   */
   
  protected URILocator(String normalized, short schemeEnd, short authorityEnd,
                       short lastSlash, short fragmentStart) {
    this.address = normalized;
    this.schemeEnd = schemeEnd;
    this.authorityEnd = authorityEnd;
    this.lastSlash = lastSlash;
    this.fragmentStart = fragmentStart;
  }
  
  protected String normalize(String address) throws MalformedURLException {
    authorityEnd = -1;
    lastSlash = -1;
    fragmentStart = -1;

    char[] uri = new char[address.length() + 100]; // working buffer
    address.getChars(0, address.length(), uri, 0); // copy into buffer
    int length = decodeURI(uri, address.length());
    schemeEnd = (short) getScheme(uri, length);
    if (schemeEnd == -1)
      throw new MalformedURLException("No valid scheme in URI: " + address);

    if (StringUtils.regionEquals("file", uri, 0, 4) ||
	        StringUtils.regionEquals("jar:file", uri, 0, 8) ||
	        StringUtils.regionEquals("classpath", uri, 0, 9))
      length = parseFileUrl(uri, schemeEnd, length);
    else if (StringUtils.regionEquals("//", uri, schemeEnd+1, 2))
      length = parseHierarchicalUrl(uri, schemeEnd, length);

    return new String(uri, 0, length);
  }

  // --------------------------------------------------------------------------
  // LocatorIF implementation
  // --------------------------------------------------------------------------
  
  public String getNotation() {
    return "URI";
  }

  public String getAddress() {
    return address;
  }
  
  public LocatorIF resolveAbsolute(String rel) {
    int length = rel.length();
    if (length == 0) {
      if (fragmentStart == -1)
        return this;
      else
        return new URILocator(address.substring(0, fragmentStart),
                              schemeEnd, authorityEnd, lastSlash, (short) -1);
    }

    switch(rel.charAt(0)) {
    case '#':
      if (fragmentStart == -1)
        return new URIFragmentLocator(address.intern(), rel.substring(1),
                                      schemeEnd, authorityEnd, lastSlash);
      else
        return new URIFragmentLocator(address.substring(0, fragmentStart).intern(),
                                      rel.substring(1),
                                      schemeEnd, authorityEnd, lastSlash);

    case '/':
      if (length != 1 && rel.charAt(1) == '/') { // begins with "//"
        if (authorityEnd == -1)
          throw new OntopiaRuntimeException(new MalformedURLException("Base URI is not hierarchical"));
        return new URILocator(address.substring(0, schemeEnd+1) + rel,
                              schemeEnd, authorityEnd, lastSlash,
                              fragmentStart);
      } else
        // FIXME: should normalize absolute path
        return new URILocator(address.substring(0, authorityEnd) + rel,
                              schemeEnd, authorityEnd, lastSlash,
                              fragmentStart);
    } // no default needed; the rest of the method _is_ the default
      
    try {
      char[] relative = rel.toCharArray();

      // does the URI have a scheme?
      if (getScheme(relative, relative.length) != -1)
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
          // the "/" here is important, as it was normalized away and needs
          // to be added back
          return new URILocator(address.substring(0, authorityEnd + 1) + "/" +
                                rel);
        else
          return new URILocator(address.substring(0, lastSlash + 1) + rel);
      }
      
      // there were no slashes, so this is a pure file name
      if (lastSlash == -1) // base has no directory part
        return new URILocator(address + rel,
                              schemeEnd, authorityEnd, lastSlash,
                              fragmentStart);
      else
        return new URILocator(address.substring(0, lastSlash + 1) + rel,
                              schemeEnd, authorityEnd, lastSlash,
                              fragmentStart);
    }
    catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public String getExternalForm() {
    return toExternalForm(address);
  }
  
  static String toExternalForm(String address) {
    // need to escape characters that are not unreserved or reserved
    char[] tmp = new char[address.length() * 6]; // worst case scenario
    int pos = 0;

    // we don't escape % because if it's present in the URI it's because
    // we didn't unescape it on the way in.
    for (int ix = 0; ix < address.length(); ix++) {
      char ch = address.charAt(ix);
      if ((ch >= 'a' && ch <= 'z') || // a-z
          (ch >= '?' && ch <= 'Z') || // ? @ A-Z
          (ch >= '%' && ch <= ';') || // % & ' ( ) * + , - . / 0-9 : ;
          ch == '#' | ch == '!' || ch == '$' || ch == '=' || ch == '_' || ch == '~' ||
          (ch == '|' && ix == 7))     // file:/X|/; special case...
        tmp[pos++] = ch;
      else { // have to escape
        tmp[pos++] = '%';
        if (ch <= 0x7F) {
          // 0xxxxxxx
          addByte(tmp, pos, ch);
          pos += 2;
        } else if (ch <= 0x07FF) {
          // 110xxxxx 10xxxxxx
          addByte(tmp, pos, (ch >> 6) | 0xC0);
          pos += 2;
          tmp[pos++] = '%';
          addByte(tmp, pos, (ch & 0x3F) | 0x80);
          pos += 2;
        } else {
          // 1110xxxx 10xxxxxx 10xxxxxx
          addByte(tmp, pos, (ch >> 12) | 0xE0);
          pos += 2;
          tmp[pos++] = '%';
          addByte(tmp, pos, ((ch >> 6) & 0x3F) | 0x80);
          pos += 2;
          tmp[pos++] = '%';
          addByte(tmp, pos, (ch & 0x3F) | 0x80);
          pos += 2;
        }
      }
    }
    
    return new String(tmp, 0, pos);
  }

  private static void addByte(char[] tmp, int pos, int ch) {
    tmp[pos] = encodeHexDigit((ch & 0x00F0) >> 4);
    tmp[pos + 1] = encodeHexDigit(ch & 0x000F);
  }

  // --------------------------------------------------------------------------
  // URI parsing
  // --------------------------------------------------------------------------

  /**
   * INTERNAL: Parses and normalizes a file:/ URL.
   * @param ix The index of the last character in the scheme (':')
   * @return Index of last character in URI.
   */
  private int parseFileUrl(char[] uri, int ix, int length)
    throws MalformedURLException {
    if (ix+2 >= length)
      throw new MalformedURLException("File URL has only scheme name.");

    // STEP 1: deal with hostname and initial slashes
    // file:///home/         -> file:/home/
    // file://localhost/home -> file://localhost/home/
    // file:/home/           -> file:/home/
    // file://graph/tmp/     -> file://graph/tmp/
    
    ix++; // skip ':'
    if (uri[ix] == '/') ix++; // skip ':/'
    
    int chars = -1;
    if (uri[ix] == '/') {
      // three cases: '://server/home/', ':///home/' and '://localhost/home/'
      if (ix+1 < length && uri[ix+1] == '/')         
        chars = 2; // it's ':///home/'; strip '//'
      else 
        chars = 0; // it's '://server/home/', leave it

      System.arraycopy(uri, ix+chars, uri, ix, length - (ix+chars));
      length -= chars;
    }

    // STEP 2: deal with directory part
    // INVARIANT: ix now index of first char after 'file:/'    

    if (chars == 0) {
      authorityEnd = (short) ix;
      return parseDirectoryPart(uri, ix, length);
    } else {
      authorityEnd = (short) (ix-1);
      return parseDirectoryPart(uri, ix-1, length);
    }
  }
  
  /**
   * INTERNAL: Parses and normalizes a hierarchical URL.
   * @param schemeEnd The index of the last character in the scheme (':')
   * @return Index of last character in URI.
   */
  private int parseHierarchicalUrl(char[] uri, int schemeEnd, int length)
    throws MalformedURLException {
    
    // ---parse authority
    // [ [ userinfo "@" ] host [ : port ] ]
    // the only thing we care about is the port number

    // algorithm:
    //   scan outwards, stop on first '/' or the end
    //   after each ':' keep track of where it was and whether it was
    //   followed by non-digits
    int ix = schemeEnd + 3; // skip over the '//'
    int portStart = -1;
    int hostStart = ix;
    String port = null;
    
    while (ix < length &&
           uri[ix] != '/' && uri[ix] != '?' && uri[ix] != '#') {
      
      if (uri[ix] == ':') { // may be port number, check out
        ix++;
        portStart = ix;
        while (ix < length && uri[ix] >= '0' && uri[ix] <= '9') 
          ix++; // port numbers are pure digits, so scan for those
        if (ix >= length ||
            uri[ix] == '/' || uri[ix] == '?' || uri[ix] == '#') {
          // terminated with correct char, so it's a port number
          port = new String(uri, portStart, ix - portStart);
          break; // this means we're done with the authority part
        }
      } else if (uri[ix] == '@') 
        hostStart = ix + 1;
      
      ix++;
    }

    if (port != null && findPortDefault(uri, schemeEnd).equals(port)) {
      // default port number used; remove
      int offset = (ix - portStart) + 1;
      System.arraycopy(uri, ix, uri, portStart - 1, length - ix);
      ix -= offset;
      length -= offset;
    }

    StringUtils.downCaseAscii(uri, hostStart, ix - hostStart);

    // make sure authority part ends with a slash no matter what
    if (uri[ix] != '/') {
      length++; // we just lengthened the URI...
      if (ix+1 < length)
        // have to shift part after '/' out one notch
        System.arraycopy(uri, ix, uri, ix+1, (length - ix) - 1);

      uri[ix++] = '/';
    }
    
    authorityEnd = (short) ix;
    if (ix+1 >= length)
      return length;

    return parseDirectoryPart(uri, ix, length);
  }
    
  public int parseDirectoryPart(char[] uri, int ix, int length)
    throws MalformedURLException {
    
    if (ix == length) { // we are at the last character, so just stop
      lastSlash = -1;
      return length;
    }

    int[] slashpos = new int[(length - authorityEnd) / 2 + 2];
    slashpos[0] = authorityEnd;
    int slashix = 0;
    
    while (ix < length && uri[ix] != '?' && uri[ix] != '#') {
      if (uri[ix] == '/') {
        if (slashpos[slashix] == ix - 1) {// two successive slashes, remove one
          System.arraycopy(uri, ix, uri, ix - 1, length - ix);
          ix--;
          length--;
        }
        
        // WARNING: This loop is time-critical in the extreme.  Minor
        // rearrangements to the tests here can cause the time needed
        // to create URIs to double. This will then affect import
        // times and other important operations as well. Care, and
        // stopwatches, must be exercised if changes are made.
        
        if (ix+2 < length && uri[ix+1] == '.') {
          // handling ./ in URI
          if (uri[ix+2] == '/') {
            System.arraycopy(uri, ix+3, uri, ix+1, length - (ix+3));
            length -= 2;
            continue;
          }
          
          // handling ../ in URI and .. at end of URI
          if (uri[ix+2] == '.' &&
              ((ix+3 < length && uri[ix+3] == '/') ||
               ix+3 == length)) {
            // removing 3 chars if ../, 2 chars if ..
            int chars = 3;
            if (ix+3 == length) chars = 2;

            int offset;
            if (ix == authorityEnd) 
              offset = chars;
            else
              offset = (ix+chars) - slashpos[slashix];

            //debugPrint(uri, length, slashpos, slashix+1);
            System.arraycopy(uri, ix+(chars+1), uri, slashpos[slashix] + 1, 
                             length - (ix+(chars+1)));
            ix = slashpos[slashix];
            length -= offset;
            if (slashix != 0)
              slashix--;
            continue;
          }
        } // end of ../ and ./ checking
        
        if (ix != authorityEnd)
          slashpos[++slashix] = ix;
      }
      ix++;
    }

    // last we check for /. at the end of the directory part, and remove it
    if (slashpos[slashix] + 2 == ix && uri[ix-1] == '.') {
      if (slashix != 0)
        slashix--;
      
      System.arraycopy(uri, ix, uri, ix-1, length - ix);
      length--;
      ix--;
    }

    lastSlash = (short) slashpos[slashix];
    
    // ---parse query, and fragment
    while (ix < length && uri[ix] != '#')
      ix++;

    if (ix < length && uri[ix] == '#') {
      fragmentStart = (short) ix;

      // fragment syntax, RFC 2396, page 27
      //
      // fragment      = *uric
      // uric          = reserved | unreserved | escaped
      // reserved      = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" |
      //                 "$" | ","
      // unreserved    = alphanum | mark
      // mark          = "-" | "_" | "." | "!" | "~" | "*" | "'" |
      //                 "(" | ")"

      ix++; // skip the '#' to begin checking
      while (ix < length &&
             ((uri[ix] >= 'a' && uri[ix] <= 'z') ||
              (uri[ix] >= '?' && uri[ix] <= 'Z') || // ? @ A-Z
              (uri[ix] >= '&' && uri[ix] <= '9') || // & ' ( ) * + , - . / 0-9
              uri[ix] == '!' ||
              uri[ix] == '$' || 
              uri[ix] == ':' || 
              uri[ix] == ';' || 
              uri[ix] == '=' || 
              uri[ix] == '_' || 
              uri[ix] == '~' ||
              uri[ix] == '%')) { // to support percent-escaping
        if (uri[ix] == '%') ix += 2;
        ix++;
      }

      if (ix < length)
        throw new MalformedURLException("Illegal character in fragment: '" + uri[ix] +
                                        "' at position " + ix + " of: '" +
                                        new String(uri) + "'");
    }

    return length;
  }

  /**
   * Parses the scheme part of a URI.
   * @return The index of the last char in the scheme, which will be ':' or
   *         -1 if there is no scheme.
   */  
  private int getScheme(char[] uri, int length) {
    // RFC 2396, section 3.1
    // scheme        = alpha *( alpha | digit | "+" | "-" | "." )
    
    int index = 0;
    while((index < length) &&
          ((uri[index] >= 'a' && uri[index] <= 'z') || // lowalpha
           (uri[index] >= 'A' && uri[index] <= 'Z') || // upalpha
           (uri[index] >= '0' && uri[index] <= '9') || // digit
           uri[index] == '+' ||
           uri[index] == '-' ||
           uri[index] == '.'))
      index++;

    if (index == 0 || index >= length || uri[index] != ':')
      return -1;
      
    return index;
  }

  /**
   * Decodes escape codes in URIs in place in the character array. Returns
   * length of URI in the character array.
   */
  private int decodeURI(char[] uri, int length)
    throws MalformedURLException {
    while (length > 0 && uri[length-1] == ' ')
      length--;
    
    int pos = 0; // pos to write
    int ix;      // index to read

    for (ix = 0; ix < length && uri[ix] == ' '; ix++)
      ;

    for (; ix < length; ix++) {
      switch(uri[ix]) {
      case '%':
        if (ix + 2 >= length)
          throw new MalformedURLException("Incomplete percent-escape at end of URI");
        char ch = (char) (decodeHexDigit(uri[ix+1]) * 16 +
                          decodeHexDigit(uri[ix+2]));
        if (ch != 38 && ch != 37 && ch != 35) {
          // it's not #, & or %, so we can unescape it
          uri[pos++] = ch;
          ix += 2;
        } else
          // it *is* #, & or %. therefore must leave alone
          uri[pos++] = '%';
        break;
      case '+':
        uri[pos++] = ' ';
        break;
      default:
        uri[pos++] = uri[ix];
      }
    }
      
    return pos;
  }

  private int decodeHexDigit(char ch) throws MalformedURLException {
    if (ch >= '0' && ch <= '9')
      return ch - '0';
    else if (ch >= 'A' && ch <= 'F')
      return (ch - 'A') + 10;
    else if (ch >= 'a' && ch <= 'f')
      return (ch - 'a') + 10;
    else
      throw new MalformedURLException("Invalid percent-escape code containing '" + ch + "' as hex digit in");
  }

  private String findPortDefault(char[] uri, int schemeEnd) {
    if (StringUtils.regionEquals("http", uri, 0, schemeEnd))
      return "80";
    else if (StringUtils.regionEquals("https", uri, 0, schemeEnd))
      return "443";
    else if (StringUtils.regionEquals("shttp", uri, 0, schemeEnd))
      return "80";
    else if (StringUtils.regionEquals("ftp", uri, 0, schemeEnd))
      return "21";
    else if (StringUtils.regionEquals("ldap", uri, 0, schemeEnd))
      return "389";
    else if (StringUtils.regionEquals("gopher", uri, 0, schemeEnd))
      return "70";
    else
      return "dummy value";
  }

  /**
   * Escapes the given file path so that illegal characters in the
   * path are correctly escaped.
   */
  private static String escapeFilePath(String path) {
    // only the following does not need to be escaped
    // unreserved  = alphanum | mark
    // mark        = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
    // we don't escape slashes, because those are not allowed in file names

    char[] tmp = new char[path.length() * 6]; // more than enough
    int pos = 0;
    for (int ix = 0; ix < path.length(); ix++) {
      char ch = path.charAt(ix);
      if ((ch >= 'a' && ch <= 'z') ||
          (ch >= 'A' && ch <= 'Z') ||
          (ch >= '0' && ch <= '9') ||
          (ch >= '\'' && ch <= '*') ||
          ch == '!' || ch == '-' || ch == '.' || ch == '_' || ch == '~')
        tmp[pos++] = ch;
      else if (ch > 0x7F) {
        // UTF-8-encode the character

        if (ch < 0x07FF) {
          // 0000 0080-0000 07FF   110xxxxx 10xxxxxx

          int codeval = (ch >> 6) | 0xC0;
          tmp[pos++] = '%';
          tmp[pos++] = encodeHexDigit(codeval >> 4);
          tmp[pos++] = encodeHexDigit(codeval & 0x0F);

          codeval = (ch & 0x003F) | 0x80;
          tmp[pos++] = '%';
          tmp[pos++] = encodeHexDigit(codeval >> 4);
          tmp[pos++] = encodeHexDigit(codeval & 0x0F);
          
        } else if (ch < 0xFFFF) {
          // 0000 0800-0000 FFFF   1110xxxx 10xxxxxx 10xxxxxx
          
          int codeval = (ch >> 12) | 0xE0;
          tmp[pos++] = '%';
          tmp[pos++] = encodeHexDigit(codeval >> 4);
          tmp[pos++] = encodeHexDigit(codeval & 0x0F);

          codeval = ((ch & 0x0FFF) >> 6) | 0x80;
          tmp[pos++] = '%';
          tmp[pos++] = encodeHexDigit(codeval >> 4);
          tmp[pos++] = encodeHexDigit(codeval & 0x0F);

          codeval = ((ch & 0x003F) >> 6) | 0x80;
          tmp[pos++] = '%';
          tmp[pos++] = encodeHexDigit(codeval >> 4);
          tmp[pos++] = encodeHexDigit(codeval & 0x0F);
          
        } else
          throw new OntopiaRuntimeException("INTERNAL ERROR: Only BMP characters supported");
      } else {
        tmp[pos++] = '%';
        tmp[pos++] = encodeHexDigit(ch >> 4);
        tmp[pos++] = encodeHexDigit(ch & 0x0F);
      }
    }

    return new String(tmp, 0, pos);
  }

  private static char encodeHexDigit(int value) {
    if (value <= 9)
      return (char) ('0' + value);
    else
      return (char) ('A' + (value - 10));
  }
  
  // --- Debugging methods

  @SuppressWarnings("unused")
  private void debugPrint(char[] uri, int length, int[] indexes, int count) {
    System.out.println("\n" + new String(uri, 0, length));
    int next = 0;
    for (int ix = 0; ix < length; ix++) {
      if (indexes[next] == ix) {
        System.out.print("^");
        next++;
      } else
        System.out.print(" ");
    }
    System.out.println("");
  }

  // --------------------------------------------------------------------------
  // Misc
  // --------------------------------------------------------------------------

  public int hashCode() {
    return address.hashCode();
  }

  public boolean equals(Object object) {
    try {
      LocatorIF locator = (LocatorIF)object;
      return address.equals(locator.getAddress()) &&
        locator.getNotation().equals("URI");
    } catch (ClassCastException e) {
      return false; // In case the object is not a locator
    } catch (NullPointerException e) {
      return false; // In case the object is null
    }
  }

  // --------------------------------------------------------------------------
  // Externalization
  // --------------------------------------------------------------------------
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(address);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    address = in.readUTF();
  }

  // --------------------------------------------------------------------------
  // Utility method
  // --------------------------------------------------------------------------

  /**
   * INTERNAL: Parses the URI and returns an instance of URILocator if
   * the URI is valid. If the URI is invalid null is returned.
   *
   * @since 3.0
   */
  public static URILocator create(String uriAddress) {
    try {
      return new URILocator(uriAddress);
    } catch (MalformedURLException e) {
      return null;
    }
  }

}
