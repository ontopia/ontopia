
package net.ontopia.utils;

import java.util.*;
import java.io.Writer;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * INTERNAL: Class that contains useful string operation methods.
 */
public class StringUtils {
  
  private static Random rand = new Random(); // essential to only create it once

  /**
   * INTERNAL: A string used internally for various control flow
   * purposes. It is a string that is extremely unlikely to occur in
   * real-world data.
   */
  public static final String VERY_UNLIKELY_STRING = "_________________VERY UNLIKELY STRING_____________";
  
  /**
   * INTERNAL: Replaces occurrences of a string within a string,
   * returning a new string where the substitutions have been
   * performed.
   */
  public static String replace(String str, String oldvalue, String newvalue) {
    int match = str.indexOf(oldvalue);
    while (match != -1) {
      // TODO: This is not very fast is it?
      str = str.substring(0, match) + newvalue +
        str.substring(match + oldvalue.length());
      match = str.indexOf(oldvalue,match+newvalue.length());
    }
    return str;
  }
  
  /**
   * INTERNAL: Replaces occurrences of a char within a string,
   * returning a new string where the substitutions have been
   * performed.
   *
   * @since 2.0
   */
  public static String replace(String str, char oldvalue, String newvalue) {
    int match = str.indexOf(oldvalue);
    while (match != -1) {
      str = str.substring(0, match) + newvalue +
        str.substring(match + 1);
      match = str.indexOf(oldvalue,match+newvalue.length());
    }
    return str;
  }

  /**
   * INTERNAL: Splits a string on occurrences of a given
   * substring. The separator is " ".
   */
  public static String[] split(String str) {
    return split(str, " ");
  }
  
  /**
   * INTERNAL: Splits a string on occurrences of a given substring.
   */
  public static String[] split(String str, String separator) {
    int nPos = 0;
    int nStartPos = 0;
    int nIndex = 0;
    String[] splitArray = null;
    int nSplitResults = 1;

    // Count number of pieces
    while ((nPos = str.indexOf (separator, nStartPos)) != -1) {
      nStartPos = nPos + 1;
      nSplitResults++;
    }

    // Create array and set us up
    splitArray = new String[nSplitResults];
    nPos = 0;
    nStartPos = 0;

    // Do the split
    for (nIndex = 0; nIndex < nSplitResults-1; nIndex++) {
      nPos = str.indexOf (separator, nStartPos);
      splitArray[nIndex] = str.substring (nStartPos, nPos);
      nStartPos = nPos + 1;
    }
    splitArray[nIndex] = str.substring (nStartPos);
        
    return splitArray;
  }

  /**
   * INTERNAL: Tokenizes a string on occurrences of a delimiters. This
   * method is effectively using a java.util.StringTokenizer
   * underneath.
   *
   * @since 3.1
   */
  public static String[] tokenize(String str, String delimiters) {
    String[] values = new String[1];
    int c = 0;
    StringTokenizer st = new StringTokenizer(str, delimiters);
    while (st.hasMoreTokens()) {
      if (c > values.length-1) {
        // increase size of array
        String[] newValues = new String[(values.length * 3)/2 + 1];
        System.arraycopy(values, 0, newValues, 0, values.length);
        values = newValues;
      }          
      values[c] = st.nextToken();
      c++;
    }
    if (c < values.length) {
      // reduce size of array
      String[] newValues = new String[c];
      System.arraycopy(values, 0, newValues, 0, c);
      values = newValues;
    }
    return values;
  }
  
  /**
   * INTERNAL: Trims each individual string in the array. Note that
   * this method will effectively replace the strings in the
   * array. The string array is returned just for convenience.
   *
   * @since 3.1
   */
  public static String[] trim(String[] str) {
    for (int i=0; i < str.length; i++) {
      if (str[i] == null)
        continue;
      else
        str[i] = str[i].trim();
    }
    return str;
  }

  /**
   * INTERNAL: Joins the objects in a collection (turned into strings
   * by toString) with a separator string.
   */
  public static String join(Collection objects, String separator) {
    if (objects.isEmpty())
      return "";

    Iterator iter = objects.iterator();
    StringBuffer list = new StringBuffer();
    list.append(iter.next());
    while (iter.hasNext()) {
      list.append(separator);
      list.append(iter.next());
    }
    return list.toString();
  }

  /**
   * INTERNAL: Joins the objects in a collection (turned into strings
   * by the stringifier) with a separator string.
   *
   * @since 2.0
   */
  public static String join(Collection objects, String separator,
                            StringifierIF stringifier) {
    if (objects.isEmpty())
      return "";

    Iterator iter = objects.iterator();
    StringBuffer list=new StringBuffer();
    list.append(stringifier.toString(iter.next()));
    while (iter.hasNext()) {
      list.append(separator);
      list.append(stringifier.toString(iter.next()));
    }
    return list.toString();
  }

  /**
   * INTERNAL: Joins the objects in an array (turned into strings by
   * toString) with a separator string.
   */
  public static String join(Object[] objects, String separator) {
    if (objects.length == 0)
      return "";

    StringBuffer list = new StringBuffer();
    list.append(objects[0]);
    for (int ix = 1; ix < objects.length; ix++) {
      list.append(separator);
      list.append(objects[ix]);
    }
    return list.toString();
  }

  /**
   * INTERNAL: Joins the objects in an array (turned into strings by
   * toString) with a separator string.
   *
   * @since 3.1
   */
  public static String join(Object[] objects, char separator) {
    if (objects.length == 0)
      return "";

    StringBuffer list = new StringBuffer();
    list.append(objects[0]);
    for (int ix = 1; ix < objects.length; ix++) {
      list.append(separator);
      list.append(objects[ix]);
    }
    return list.toString();
  }
  
  /**
   * INTERNAL: Joins the ints in an array (turned into strings by
   * toString) with a separator string.<p>
   *
   * @since 1.3.4
   */
  public static String join(int[] vals, String separator) {
    if (vals.length == 0)
      return "";

    StringBuffer list = new StringBuffer();
    list.append(vals[0]);
    for (int ix = 1; ix < vals.length; ix++) {
      list.append(separator);
      list.append(vals[ix]);
    }
    return list.toString();
  }
  
  /**
   * INTERNAL: Joins the longs in an array (turned into strings by
   * toString) with a separator string.<p>
   *
   * @since 2.0
   */
  public static String join(long[] vals, String separator) {
    if (vals.length == 0)
      return "";

    StringBuffer list = new StringBuffer();
    list.append(vals[0]);
    for (int ix = 1; ix < vals.length; ix++) {
      list.append(separator);
      list.append(vals[ix]);
    }
    return list.toString();
  }
  
  /**
   * INTERNAL: Joins the booleans in an array (turned into strings by
   * toString) with a separator string.<p>
   *
   * @since 2.0
   */
  public static String join(boolean[] vals, String separator) {
    if (vals.length == 0)
      return "";

    StringBuffer list = new StringBuffer();
    list.append(vals[0]);
    for (int ix = 1; ix < vals.length; ix++) {
      list.append(separator);
      list.append(vals[ix]);
    }
    return list.toString();
  }
  
  /**
   * INTERNAL: Joins the bytes in an array (turned into strings by
   * toString) with a separator string.<p>
   *
   * @since 2.0
   */
  public static String join(byte[] vals, String separator) {
    if (vals.length == 0)
      return "";

    StringBuffer list = new StringBuffer();
    list.append(vals[0]);
    for (int ix = 1; ix < vals.length; ix++) {
      list.append(separator);
      list.append(vals[ix]);
    }
    return list.toString();
  }
  
  /**
   * INTERNAL: Joins the characters in an array (turned into strings
   * by toString) with a separator string.<p>
   *
   * @since 2.0
   */
  public static String join(char[] vals, String separator) {
    if (vals.length == 0)
      return "";

    StringBuffer list = new StringBuffer();
    list.append(vals[0]);
    for (int ix = 1; ix < vals.length; ix++) {
      list.append(separator);
      list.append(vals[ix]);
    }
    return list.toString();
  }

  /**
   * INTERNAL: Joins the objects in a collection (turned into strings
   * by toString) with a separator string. The result is appended to
   * the specified StringBuffer.
   *
   * @since 1.3.2
   */
  public static void join(Collection objects, String separator, StringBuffer sb) {
    if (objects.isEmpty()) return;

    Iterator iter = objects.iterator();
    sb.append(iter.next());
    while (iter.hasNext()) {
      sb.append(separator);
      sb.append(iter.next());
    }
  }

  /**
   * INTERNAL: Joins the objects in an array (turned into strings by
   * toString) with a separator string. The result is appended to the
   * specified StringBuffer.
   *
   * @since 1.3.2
   */
  public static void join(Object[] objects, String separator, StringBuffer sb) {
    if (objects.length == 0) return;

    sb.append(objects[0]);
    for (int ix = 1; ix < objects.length; ix++) {
      sb.append(separator);
      sb.append(objects[ix]);
    }
  }
  
  /**
   * INTERNAL: Joins the non-null objects in an array (turned into
   * strings by toString) with a separator string. Note that nulls in
   * the array are ignored.
   *
   * @since 1.2.5
   */
  public static String join(Object[] objects, String separator, boolean remove_nulls) {
    if (objects == null || objects.length == 0)
      return "";

    if (remove_nulls) {
      StringBuffer list = new StringBuffer();
      boolean subseq = false;
      for (int ix = 0; ix < objects.length; ix++) {
        if (objects[ix] != null) {
          if (subseq)
            list.append(separator);
          else
            subseq = true;
          list.append(objects[ix]);
        }
      }
      return list.toString();
    } else {
      // Delegate to other join method if not set to remove nulls.
      return StringUtils.join(objects, separator);
    }
  }
  
  /**
   * INTERNAL: Compare two string values lexically. This method can
   * handle nulls values. Nulls sort lower than non-nulls.<p>
   *
   * @since 3.1
   */
  public static int compare(String s1, String s2) {
    if (s1 == null && s2 == null)
      return 0;
    else if (s1 == null)
      return -1;
    else if (s2 == null)
      return 1;
    else
      return s1.compareTo(s2);
  }
  
  /**
   * INTERNAL: Compare two string values lexically ignoring case. This
   * method can handle nulls values. Nulls sort lower than non-nulls.<p>
   *
   * @since 3.1
   */
  public static int compareToIgnoreCase(String s1, String s2) {
    if (s1 == null && s2 == null)
      return 0;
    else if (s1 == null)
      return -1;
    else if (s2 == null)
      return 1;
    else
      return s1.compareToIgnoreCase(s2);
  }

  /**
   * INTERNAL: Replaces sequences of one or more ' ', \t, \n, \r by a
   * single space, returning the new string.
   */
  public static String normalizeWhitespace(String source) {
    char[] string = source.toCharArray();
    int pos = 0;
    boolean previousWasWS = false;

    for (int ix = 0; ix < string.length; ix++) {
      switch (string[ix]) {
      case ' ': 
      case '\t':
      case '\n':
      case '\r':
        previousWasWS = true;
        break;
      default:
        if (previousWasWS) {
          string[pos++] = ' ';
          previousWasWS = false;
        }
        string[pos++] = string[ix];
      }
    }

    if (previousWasWS)
      string[pos++] = ' ';

    return new String(string, 0, pos);
  }

  /**
   * INTERNAL: Replaces sequences of one or more characters that
   * report as whitespace through Character.isWhitespace(char) by a
   * single space, returning the new string.
   * @since 3.3.0
   */
  public static String normalizeIsWhitespace(String source) {
    char[] string = source.toCharArray();
    int pos = 0;
    boolean previousWasWS = false;

    for (int ix = 0; ix < string.length; ix++) {
      if (Character.isWhitespace(string[ix])) {
        previousWasWS = true;
      } else {
        if (previousWasWS) {
          string[pos++] = ' ';
          previousWasWS = false;
        }
        string[pos++] = string[ix];
      }
    }

    if (previousWasWS)
      string[pos++] = ' ';

    return new String(string, 0, pos);
  }

  /**
   * INTERNAL: Test whether the string is equal to the given region of
   * the character array.
   */
  public static boolean regionEquals(String str, char[] ch, int start,
                                     int length) {
    if (str.length() != length || start+length > ch.length)
      return false;

    char[] strarr = str.toCharArray();
    for (int i=0; i<length; i++)
      if (ch[start+i] != strarr[i])
        return false;
    return true;
  }

  /**
   * INTERNAL: Convert the A-Z characters in the array region to a-z.
   */
  public static void downCaseAscii(char[] ch, int start, int length) {
    int end = start + length;
    for (int ix = start; ix < end; ix++)
      if (ch[ix] >= 'A' && ch[ix] <= 'Z')
        ch[ix] = (char) (ch[ix] | 0x20); // efficient downcase of char :-)
  }

  /**
   * INTERNAL: Transcode a string mistakenly interpreted as ISO 8859-1
   * to one interpreted as UTF-8.
   */
  public static String transcodeUTF8(String original) {
    try {
      byte raw[] = original.getBytes("8859_1");
      return new String(raw, 0, raw.length, "UTF-8");
    } catch (java.io.UnsupportedEncodingException e) {
      // This should never happen, but it doesn't hurt to be sure
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: Transcode a string mistakenly interpreted as ISO 8859-1
   * to one interpreted as something else.
   */
  public static String transcode(String original, String encoding)
    throws java.io.UnsupportedEncodingException {
    
    byte raw[] = original.getBytes("8859_1");
    return new String(raw, 0, raw.length, encoding);
  }

  /**
   * INTERNAL: Filters the specified string for characters that are
   * senstive to HTML interpreters, returning the string with these
   * characters replaced by the corresponding character entities.
   *
   * @param value The string to be filtered and returned
   *
   * @since 1.3.1
   */
  public static String escapeHTMLEntities(String value) {
    if (value == null)
      return null;

    char content[] = new char[value.length()];
    value.getChars(0, content.length, content, 0);
    StringBuffer result = new StringBuffer(content.length + 50);
    for (int i = 0; i < content.length; i++) {
      switch (content[i]) {
      case '<':
        result.append("&lt;");
        break;
      case '>':
        result.append("&gt;");
        break;
      case '&':
        result.append("&amp;");
        break;
      case '"':
        result.append("&quot;");
        break;
      case '\'':
        result.append("&#39;");
        break;
      default:
        result.append(content[i]);
      }
    }
    return result.toString();
  }

  /**
   * INTERNAL: Filters the specified string for characters that are
   * senstive to HTML interpreters, writing the string with these
   * characters replaced by the corresponding character entities to
   * the given writer.
   *
   * @param value The string to be filtered and written.
   *
   * @since 3.0
   */
  public static void escapeHTMLEntities(String value, Writer out)
    throws IOException {
    if (value == null)
      return;

    char content[] = new char[value.length()];
    value.getChars(0, content.length, content, 0);
    for (int i = 0; i < content.length; i++) {
      switch (content[i]) {
      case '<':
        out.write("&lt;");
        break;
      case '>':
        out.write("&gt;");
        break;
      case '&':
        out.write("&amp;");
        break;
      case '"':
        out.write("&quot;");
        break;
      case '\'':
        out.write("&#39;");
        break;
      default:
        out.write(content[i]);
      }
    }
  }

  /**
   * INTERNAL: Make a random ID-like string of the given number of
   * characters.   
   */
  public static String makeRandomId(int length) {
    char[] chars = new char[length];
    for (int ix = 0; ix < length; ix++)
      chars[ix] = (char) (65 + rand.nextInt(26));
    return new String(chars);
  }

  /**
   * INTERNAL: Pad characters to the given length.
   */
  public static String pad(int number, char ch, int length) {
    return pad(Integer.toString(number), ch, length);
  }

  /**
   * INTERNAL: Pad with filler characters in front of a base string to
   * get the given length.
   */
  public static String pad(String str, char ch, int length) {
    char[] result = new char[length];
    for (int i=0, s=str.length(), p=length-s; i < length ; i++) {
      if (i < p)
        result[i] = ch;
      else
        result[i] = str.charAt(i-p);
    }
    return new String(result);
  }

  /**
   * INTERNAL: Returns a 32 character long hex encoded MD5 digest of
   * the given string. Hex letters are returned in lower case.
   */
  public static String md5_32(String value) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digest = md.digest(value.getBytes("UTF-8"));
      StringBuffer md5hash = new StringBuffer(digest.length * 2);
      for (int i=0; i < digest.length; i++) {
        String hex = Integer.toHexString(digest[i] & 0xFF);
        if (hex.length() == 1)
          md5hash.append('0');
        md5hash.append(hex);
      }
      return md5hash.toString();
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: Returns true if the string is a valid integer.
   */
  public static boolean isInteger(String candidate) {
    try {
      Integer.parseInt(candidate);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }    
  }

  /**
   * INTERNAL: Creates a candidate ID from an input string. The
   * algorithm discards characters above U+00FF, strips accents off
   * remaining characters, then discards everything that doesn't match
   * the LTM NAME production (except leading characters, which turn
   * into underscores). Whitespace is normalized, and turns into
   * a hyphen when internal to the string. Letters are lowercased.
   */
  public static String normalizeId(String name) {
    char[] buffer = name.toCharArray();

    int outix = 0;
    boolean whitespacerun = false;
    boolean firstchar = false;
    for (int inix = 0; inix < buffer.length; inix++) {
      char ch = buffer[inix];
      
      // discard high characters
      if (ch > 0x00FF)
        continue;

      // handle whitespace
      if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
        if (!firstchar || whitespacerun)
          continue;
        ch = '-';
        whitespacerun = true;
      } else
        whitespacerun = false;
      
      // check mapping table
      char mapsto = charmap[ch];
      if (mapsto == 0 && firstchar)
        continue;        

      // update buffer
      if (mapsto == 0)
        // discards before first NAME char turn into underscores
        buffer[outix++] = '_';
      else {
        buffer[outix++] = mapsto;
        firstchar = true;
      }
    }

    // whitespace at end will leave a trailing '-', which needs to go
    if (whitespacerun)
      outix--; // leave out last '-'

    // check if we have a valid name start character first
    if (outix < 1)
      return null;
    else if (isNameStart(buffer[0]))
      return new String(buffer, 0, outix);
    else
      return "_" + new String(buffer, 0, outix);
  }

  private static boolean isNameStart(char ch) {
    return (ch >= 'A' && ch <= 'Z') ||
           (ch >= 'a' && ch <= 'z') ||
           ch == '_';      
  }

  // Accent-stripping and lowercasing map used for name->ID normalization
  // Auto-generated from unicodedata.txt
  static final char[] charmap = new char[]{
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: SPACE
    0, // discarded: EXCLAMATION MARK
    0, // discarded: QUOTATION MARK
    0, // discarded: NUMBER SIGN
    0, // discarded: DOLLAR SIGN
    0, // discarded: PERCENT SIGN
    0, // discarded: AMPERSAND
    0, // discarded: APOSTROPHE
    0, // discarded: LEFT PARENTHESIS
    0, // discarded: RIGHT PARENTHESIS
    0, // discarded: ASTERISK
    0, // discarded: PLUS SIGN
    0, // discarded: COMMA
    45, // untouched: HYPHEN-MINUS
    46, // untouched: FULL STOP
    0, // discarded: SOLIDUS
    48, // untouched: DIGIT ZERO
    49, // untouched: DIGIT ONE
    50, // untouched: DIGIT TWO
    51, // untouched: DIGIT THREE
    52, // untouched: DIGIT FOUR
    53, // untouched: DIGIT FIVE
    54, // untouched: DIGIT SIX
    55, // untouched: DIGIT SEVEN
    56, // untouched: DIGIT EIGHT
    57, // untouched: DIGIT NINE
    0, // discarded: COLON
    0, // discarded: SEMICOLON
    0, // discarded: LESS-THAN SIGN
    0, // discarded: EQUALS SIGN
    0, // discarded: GREATER-THAN SIGN
    0, // discarded: QUESTION MARK
    0, // discarded: COMMERCIAL AT
    97, // mapped: LATIN CAPITAL LETTER A -> LATIN SMALL LETTER A
    98, // mapped: LATIN CAPITAL LETTER B -> LATIN SMALL LETTER B
    99, // mapped: LATIN CAPITAL LETTER C -> LATIN SMALL LETTER C
    100, // mapped: LATIN CAPITAL LETTER D -> LATIN SMALL LETTER D
    101, // mapped: LATIN CAPITAL LETTER E -> LATIN SMALL LETTER E
    102, // mapped: LATIN CAPITAL LETTER F -> LATIN SMALL LETTER F
    103, // mapped: LATIN CAPITAL LETTER G -> LATIN SMALL LETTER G
    104, // mapped: LATIN CAPITAL LETTER H -> LATIN SMALL LETTER H
    105, // mapped: LATIN CAPITAL LETTER I -> LATIN SMALL LETTER I
    106, // mapped: LATIN CAPITAL LETTER J -> LATIN SMALL LETTER J
    107, // mapped: LATIN CAPITAL LETTER K -> LATIN SMALL LETTER K
    108, // mapped: LATIN CAPITAL LETTER L -> LATIN SMALL LETTER L
    109, // mapped: LATIN CAPITAL LETTER M -> LATIN SMALL LETTER M
    110, // mapped: LATIN CAPITAL LETTER N -> LATIN SMALL LETTER N
    111, // mapped: LATIN CAPITAL LETTER O -> LATIN SMALL LETTER O
    112, // mapped: LATIN CAPITAL LETTER P -> LATIN SMALL LETTER P
    113, // mapped: LATIN CAPITAL LETTER Q -> LATIN SMALL LETTER Q
    114, // mapped: LATIN CAPITAL LETTER R -> LATIN SMALL LETTER R
    115, // mapped: LATIN CAPITAL LETTER S -> LATIN SMALL LETTER S
    116, // mapped: LATIN CAPITAL LETTER T -> LATIN SMALL LETTER T
    117, // mapped: LATIN CAPITAL LETTER U -> LATIN SMALL LETTER U
    118, // mapped: LATIN CAPITAL LETTER V -> LATIN SMALL LETTER V
    119, // mapped: LATIN CAPITAL LETTER W -> LATIN SMALL LETTER W
    120, // mapped: LATIN CAPITAL LETTER X -> LATIN SMALL LETTER X
    121, // mapped: LATIN CAPITAL LETTER Y -> LATIN SMALL LETTER Y
    122, // mapped: LATIN CAPITAL LETTER Z -> LATIN SMALL LETTER Z
    0, // discarded: LEFT SQUARE BRACKET
    0, // discarded: REVERSE SOLIDUS
    0, // discarded: RIGHT SQUARE BRACKET
    0, // discarded: CIRCUMFLEX ACCENT
    95, // untouched: LOW LINE
    0, // discarded: GRAVE ACCENT
    97, // untouched: LATIN SMALL LETTER A
    98, // untouched: LATIN SMALL LETTER B
    99, // untouched: LATIN SMALL LETTER C
    100, // untouched: LATIN SMALL LETTER D
    101, // untouched: LATIN SMALL LETTER E
    102, // untouched: LATIN SMALL LETTER F
    103, // untouched: LATIN SMALL LETTER G
    104, // untouched: LATIN SMALL LETTER H
    105, // untouched: LATIN SMALL LETTER I
    106, // untouched: LATIN SMALL LETTER J
    107, // untouched: LATIN SMALL LETTER K
    108, // untouched: LATIN SMALL LETTER L
    109, // untouched: LATIN SMALL LETTER M
    110, // untouched: LATIN SMALL LETTER N
    111, // untouched: LATIN SMALL LETTER O
    112, // untouched: LATIN SMALL LETTER P
    113, // untouched: LATIN SMALL LETTER Q
    114, // untouched: LATIN SMALL LETTER R
    115, // untouched: LATIN SMALL LETTER S
    116, // untouched: LATIN SMALL LETTER T
    117, // untouched: LATIN SMALL LETTER U
    118, // untouched: LATIN SMALL LETTER V
    119, // untouched: LATIN SMALL LETTER W
    120, // untouched: LATIN SMALL LETTER X
    121, // untouched: LATIN SMALL LETTER Y
    122, // untouched: LATIN SMALL LETTER Z
    0, // discarded: LEFT CURLY BRACKET
    0, // discarded: VERTICAL LINE
    0, // discarded: RIGHT CURLY BRACKET
    0, // discarded: TILDE
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: <control>
    0, // discarded: NO-BREAK SPACE
    0, // discarded: INVERTED EXCLAMATION MARK
    0, // discarded: CENT SIGN
    0, // discarded: POUND SIGN
    0, // discarded: CURRENCY SIGN
    0, // discarded: YEN SIGN
    0, // discarded: BROKEN BAR
    0, // discarded: SECTION SIGN
    0, // discarded: DIAERESIS
    0, // discarded: COPYRIGHT SIGN
    97, // mapped: FEMININE ORDINAL INDICATOR -> LATIN SMALL LETTER A
    0, // discarded: LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
    0, // discarded: NOT SIGN
    0, // discarded: SOFT HYPHEN
    0, // discarded: REGISTERED SIGN
    0, // discarded: MACRON
    0, // discarded: DEGREE SIGN
    0, // discarded: PLUS-MINUS SIGN
    0, // discarded: SUPERSCRIPT TWO
    0, // discarded: SUPERSCRIPT THREE
    0, // discarded: ACUTE ACCENT
    0, // discarded: MICRO SIGN
    0, // discarded: PILCROW SIGN
    0, // discarded: MIDDLE DOT
    0, // discarded: CEDILLA
    0, // discarded: SUPERSCRIPT ONE
    111, // mapped: MASCULINE ORDINAL INDICATOR -> LATIN SMALL LETTER O
    0, // discarded: RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
    0, // discarded: VULGAR FRACTION ONE QUARTER
    0, // discarded: VULGAR FRACTION ONE HALF
    0, // discarded: VULGAR FRACTION THREE QUARTERS
    0, // discarded: INVERTED QUESTION MARK
    97, // mapped: LATIN CAPITAL LETTER A WITH GRAVE -> LATIN SMALL LETTER A
    97, // mapped: LATIN CAPITAL LETTER A WITH ACUTE -> LATIN SMALL LETTER A
    97, // mapped: LATIN CAPITAL LETTER A WITH CIRCUMFLEX -> LATIN SMALL LETTER A
    97, // mapped: LATIN CAPITAL LETTER A WITH TILDE -> LATIN SMALL LETTER A
    97, // mapped: LATIN CAPITAL LETTER A WITH DIAERESIS -> LATIN SMALL LETTER A
    97, // mapped: LATIN CAPITAL LETTER A WITH RING ABOVE -> LATIN SMALL LETTER A
    101, // mapped: LATIN CAPITAL LETTER AE -> LATIN SMALL LETTER E
    99, // mapped: LATIN CAPITAL LETTER C WITH CEDILLA -> LATIN SMALL LETTER C
    101, // mapped: LATIN CAPITAL LETTER E WITH GRAVE -> LATIN SMALL LETTER E
    101, // mapped: LATIN CAPITAL LETTER E WITH ACUTE -> LATIN SMALL LETTER E
    101, // mapped: LATIN CAPITAL LETTER E WITH CIRCUMFLEX -> LATIN SMALL LETTER E
    101, // mapped: LATIN CAPITAL LETTER E WITH DIAERESIS -> LATIN SMALL LETTER E
    105, // mapped: LATIN CAPITAL LETTER I WITH GRAVE -> LATIN SMALL LETTER I
    105, // mapped: LATIN CAPITAL LETTER I WITH ACUTE -> LATIN SMALL LETTER I
    105, // mapped: LATIN CAPITAL LETTER I WITH CIRCUMFLEX -> LATIN SMALL LETTER I
    105, // mapped: LATIN CAPITAL LETTER I WITH DIAERESIS -> LATIN SMALL LETTER I
    0, // discarded: LATIN CAPITAL LETTER ETH
    110, // mapped: LATIN CAPITAL LETTER N WITH TILDE -> LATIN SMALL LETTER N
    111, // mapped: LATIN CAPITAL LETTER O WITH GRAVE -> LATIN SMALL LETTER O
    111, // mapped: LATIN CAPITAL LETTER O WITH ACUTE -> LATIN SMALL LETTER O
    111, // mapped: LATIN CAPITAL LETTER O WITH CIRCUMFLEX -> LATIN SMALL LETTER O
    111, // mapped: LATIN CAPITAL LETTER O WITH TILDE -> LATIN SMALL LETTER O
    111, // mapped: LATIN CAPITAL LETTER O WITH DIAERESIS -> LATIN SMALL LETTER O
    0, // discarded: MULTIPLICATION SIGN
    111, // mapped: LATIN CAPITAL LETTER O WITH STROKE -> LATIN SMALL LETTER O
    117, // mapped: LATIN CAPITAL LETTER U WITH GRAVE -> LATIN SMALL LETTER U
    117, // mapped: LATIN CAPITAL LETTER U WITH ACUTE -> LATIN SMALL LETTER U
    117, // mapped: LATIN CAPITAL LETTER U WITH CIRCUMFLEX -> LATIN SMALL LETTER U
    117, // mapped: LATIN CAPITAL LETTER U WITH DIAERESIS -> LATIN SMALL LETTER U
    121, // mapped: LATIN CAPITAL LETTER Y WITH ACUTE -> LATIN SMALL LETTER Y
    0, // discarded: LATIN CAPITAL LETTER THORN
    0, // discarded: LATIN SMALL LETTER SHARP S
    97, // mapped: LATIN SMALL LETTER A WITH GRAVE -> LATIN SMALL LETTER A
    97, // mapped: LATIN SMALL LETTER A WITH ACUTE -> LATIN SMALL LETTER A
    97, // mapped: LATIN SMALL LETTER A WITH CIRCUMFLEX -> LATIN SMALL LETTER A
    97, // mapped: LATIN SMALL LETTER A WITH TILDE -> LATIN SMALL LETTER A
    97, // mapped: LATIN SMALL LETTER A WITH DIAERESIS -> LATIN SMALL LETTER A
    97, // mapped: LATIN SMALL LETTER A WITH RING ABOVE -> LATIN SMALL LETTER A
    101, // mapped: LATIN SMALL LETTER AE -> LATIN SMALL LETTER E
    99, // mapped: LATIN SMALL LETTER C WITH CEDILLA -> LATIN SMALL LETTER C
    101, // mapped: LATIN SMALL LETTER E WITH GRAVE -> LATIN SMALL LETTER E
    101, // mapped: LATIN SMALL LETTER E WITH ACUTE -> LATIN SMALL LETTER E
    101, // mapped: LATIN SMALL LETTER E WITH CIRCUMFLEX -> LATIN SMALL LETTER E
    101, // mapped: LATIN SMALL LETTER E WITH DIAERESIS -> LATIN SMALL LETTER E
    105, // mapped: LATIN SMALL LETTER I WITH GRAVE -> LATIN SMALL LETTER I
    105, // mapped: LATIN SMALL LETTER I WITH ACUTE -> LATIN SMALL LETTER I
    105, // mapped: LATIN SMALL LETTER I WITH CIRCUMFLEX -> LATIN SMALL LETTER I
    105, // mapped: LATIN SMALL LETTER I WITH DIAERESIS -> LATIN SMALL LETTER I
    0, // discarded: LATIN SMALL LETTER ETH
    110, // mapped: LATIN SMALL LETTER N WITH TILDE -> LATIN SMALL LETTER N
    111, // mapped: LATIN SMALL LETTER O WITH GRAVE -> LATIN SMALL LETTER O
    111, // mapped: LATIN SMALL LETTER O WITH ACUTE -> LATIN SMALL LETTER O
    111, // mapped: LATIN SMALL LETTER O WITH CIRCUMFLEX -> LATIN SMALL LETTER O
    111, // mapped: LATIN SMALL LETTER O WITH TILDE -> LATIN SMALL LETTER O
    111, // mapped: LATIN SMALL LETTER O WITH DIAERESIS -> LATIN SMALL LETTER O
    0, // discarded: DIVISION SIGN
    111, // mapped: LATIN SMALL LETTER O WITH STROKE -> LATIN SMALL LETTER O
    117, // mapped: LATIN SMALL LETTER U WITH GRAVE -> LATIN SMALL LETTER U
    117, // mapped: LATIN SMALL LETTER U WITH ACUTE -> LATIN SMALL LETTER U
    117, // mapped: LATIN SMALL LETTER U WITH CIRCUMFLEX -> LATIN SMALL LETTER U
    117, // mapped: LATIN SMALL LETTER U WITH DIAERESIS -> LATIN SMALL LETTER U
    121, // mapped: LATIN SMALL LETTER Y WITH ACUTE -> LATIN SMALL LETTER Y
    0, // discarded: LATIN SMALL LETTER THORN
    121, // mapped: LATIN SMALL LETTER Y WITH DIAERESIS -> LATIN SMALL LETTER Y
  };
  
}
