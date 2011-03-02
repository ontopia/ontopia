
// $Id: URILocator.java,v 1.54 2009/04/27 11:00:23 lars.garshol Exp $

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
      // http://code.google.com/p/ontopia/issues/detail?id=118
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

    if (StringUtils.regionEquals("file", uri, 0, 4))
      length = parseFileUrl(uri, schemeEnd, length);
    else if (StringUtils.regionEquals("//", uri, schemeEnd+1, 2))
      length = parseHierarchicalUrl(uri, schemeEnd, length);

    return new String(uri, 0, length);
  }

  /**
   * INTERNAL: Returns the URI as a URL object.
   * @deprecated Because not all URIs can be represented as URL objects.
   */
  
  public URL getURL() throws MalformedURLException {
    return new URL(address);
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
    char[] tmp = new char[address.length() * 3]; // worst case scenario
    int pos = 0;
    
    for (int ix = 0; ix < address.length(); ix++) {
      char ch = address.charAt(ix);
      if ((ch >= 'a' && ch <= 'z') || // a-z
          (ch >= '?' && ch <= 'Z') || // ? @ A-Z
          (ch >= '&' && ch <= ';') || // & ' ( ) * + , - . / 0-9 : ;
          ch == '#' | ch == '!' || ch == '$' || ch == '=' || ch == '_' || ch == '~' ||
          (ch == '|' && ix == 7))     // file:/X|/; special case...
        tmp[pos++] = ch;
      else { // have to escape
        // FIXME: should do proper UTF-8-encoding here
        ch = (char) (ch & 0x00FF); // cutoff
        tmp[pos++] = '%';
        tmp[pos++] = encodeHexDigit((ch & 0x00F0) >> 4);
        tmp[pos++] = encodeHexDigit(ch & 0x000F);
      }
    }
    
    return new String(tmp, 0, pos);
  }

  // escaping that has no understanding of the URI's internal structure
  static String _new_toExternalForm(String address) {
    int len = address.length();
    StringBuffer sbuf = new StringBuffer(len);
    for (int i = 0; i < len; i++) {
      int ch = address.charAt(i);
      if ('A' <= ch && ch <= 'Z') { // 'A'..'Z'
        sbuf.append((char)ch);
      } else if ('a' <= ch && ch <= 'z') { // 'a'..'z'
        sbuf.append((char)ch);
      } else if ('0' <= ch && ch <= '9') { // '0'..'9'
        sbuf.append((char)ch);
      } else if(ch == '|' && i == 7) { // special case: windows file urls
        sbuf.append((char)ch);
      } else if (ch == '-' || ch == '_' // unreserved
              || ch == '.' || ch == '!'
              || ch == '~' || ch == '*'
              || ch == '\''
              || ch == '(' || ch == ')') {
        sbuf.append((char)ch);
      } else if (ch == ':' || ch == '/' // reserved
              || ch == '#' || ch == '$'
              || ch == '?' || ch == ','
              || ch == ';' || ch == '&'
              || ch == '=' || ch == '@') {
        sbuf.append((char)ch);
      //! } else if (ch == ' ') { // space
      //!   sbuf.append('+');
      } else if (ch <= 0x007f) { // other ASCII
        sbuf.append(hex[ch]);
      } else if (ch <= 0x07FF) { // non-ASCII
        sbuf.append(hex[0xc0 | (ch >> 6)]);
        sbuf.append(hex[0x80 | (ch & 0x3F)]);
      } else { // 0x7FF < ch <= 0xFFFF
        sbuf.append(hex[0xe0 | (ch >> 12)]);
        sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
        sbuf.append(hex[0x80 | (ch & 0x3F)]);
      }
    }
    return sbuf.toString();
  }

  private final static String[] hex = {
    "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
    "%08", "%09", "%0A", "%0B", "%0C", "%0D", "%0E", "%0F",
    "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
    "%18", "%19", "%1A", "%1B", "%1C", "%1D", "%1E", "%1F",
    "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
    "%28", "%29", "%2A", "%2B", "%2C", "%2D", "%2E", "%2F",
    "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
    "%38", "%39", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F",
    "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
    "%48", "%49", "%4A", "%4B", "%4C", "%4D", "%4E", "%4F",
    "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
    "%58", "%59", "%5A", "%5B", "%5C", "%5D", "%5E", "%5F",
    "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
    "%68", "%69", "%6A", "%6B", "%6C", "%6D", "%6E", "%6F",
    "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
    "%78", "%79", "%7A", "%7B", "%7C", "%7D", "%7E", "%7F",
    "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
    "%88", "%89", "%8A", "%8B", "%8C", "%8D", "%8E", "%8F",
    "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
    "%98", "%99", "%9A", "%9B", "%9C", "%9D", "%9E", "%9F",
    "%A0", "%A1", "%A2", "%A3", "%A4", "%A5", "%A6", "%A7",
    "%A8", "%A9", "%AA", "%AB", "%AC", "%AD", "%AE", "%AF",
    "%B0", "%B1", "%B2", "%B3", "%B4", "%B5", "%B6", "%B7",
    "%B8", "%B9", "%BA", "%BB", "%BC", "%BD", "%BE", "%BF",
    "%C0", "%C1", "%C2", "%C3", "%C4", "%C5", "%C6", "%C7",
    "%C8", "%C9", "%CA", "%CB", "%CC", "%CD", "%CE", "%CF",
    "%D0", "%D1", "%D2", "%D3", "%D4", "%D5", "%D6", "%D7",
    "%D8", "%D9", "%DA", "%DB", "%DC", "%DD", "%DE", "%DF",
    "%E0", "%E1", "%E2", "%E3", "%E4", "%E5", "%E6", "%E7",
    "%E8", "%E9", "%EA", "%EB", "%EC", "%ED", "%EE", "%EF",
    "%F0", "%F1", "%F2", "%F3", "%F4", "%F5", "%F6", "%F7",
    "%F8", "%F9", "%FA", "%FB", "%FC", "%FD", "%FE", "%FF"
  };

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
        uri[pos++] = (char) (decodeHexDigit(uri[ix+1]) * 16 +
                             decodeHexDigit(uri[ix+2]));
        ix += 2;
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
