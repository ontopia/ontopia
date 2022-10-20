/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A collection of DB2TM functions intended to be used in
 * the function-column element.
 */
public class Functions {
  private static Logger log = LoggerFactory.getLogger(Functions.class);

  /**
   * INTERNAL: Returns the string argument as-is. This is useful
   * because parameters can be constructed as patterns.
   */
  public static String asIs(String str) {
    return str;
  }

  /**
   * INTERNAL: Uppercases the given String. If the parameter is null
   * it returns null.
   */
  public static String toUpperCase(String str) {
    return (str == null) ? null : str.toUpperCase();
  }

  /**
   * INTERNAL: Lowercases the given String. If the parameter is null
   * it returns null.
   */
  public static String toLowerCase(String str) {
    return (str == null) ? null : str.toLowerCase();
  }

  /**
   * INTERNAL: Trims the given String. If the parameter is null it
   * returns null.
   */
  public static String trim(String str) {
    return (str == null) ? null : str.trim();
  }

  /**
   * INTERNAL: Returns a substring of the given String.
   */
  public static String substring(String str, String beginIndex, String endIndex) throws NumberFormatException {
    int bi = Integer.parseInt(beginIndex);
    int ei = Integer.parseInt(endIndex);
    return str.substring(bi, ei);
  }

  /**
   * INTERNAL: Compares the first two arguments. If they are equal the
   * third argument is returned, if not the fourth one is.
   */
  public static String ifEqualThenElse(String str1, String str2, String thenval, String elseval) {
    return (Objects.equals(str1, str2) ? thenval : elseval);
  }

  /**
   * INTERNAL: Checks to see that the string is null or empty. If it
   * is then the then value is returned, otherwise the else value.
   */
  public static String ifEmptyThenElse(String str, String thenval, String elseval) {
    return (Utils.isValueEmpty(str) ? thenval : elseval);
  }

  /**
   * INTERNAL: Returns the first argument if the two arguments are
   * different. If they are equal null is returned.
   */
  public static String useFirstIfDifferent(String str1, String str2) {
    return (Objects.equals(str1, str2) ? null : str1);
  }
  
  /**
   * INTERNAL: Returns the first argument if the two arguments are
   * equal. If they are different null is returned.
   */
  public static String useFirstIfEqual(String str1, String str2) {
    return (Objects.equals(str1, str2) ? str1 : null);
  }

  /**
   * INTERNAL: Returns the first non-null argument.
   */
  public static String coalesce(String str1, String str2) {
    return (!Utils.isValueEmpty(str1) ? str1 : str2);
  }

  /**
   * INTERNAL: Returns the first non-null argument.
   */
  public static String coalesce(String str1, String str2, String str3) {
    return (!Utils.isValueEmpty(str1) ? str1 : (!Utils.isValueEmpty(str2) ? str2 : str3));
  }

  /**
   * INTERNAL: Same as coalesce, but will return then-argument instead
   * of the checked value. Will return null if none of the checked
   * values are non-empty.
   */
  public static String coalesceThen(String str1, String then1) {
    return (!Utils.isValueEmpty(str1) ? then1 : null);
  }

  /**
   * INTERNAL: Same as coalesce, but will return then-argument instead
   * of the checked value. Will return null if none of the checked
   * values are non-empty.
   */
  public static String coalesceThen(String str1, String then1,
                                    String str2, String then2) {
    return (!Utils.isValueEmpty(str1) ? then1 : (!Utils.isValueEmpty(str2) ? then2 : null));
  }
  
  /**
   * INTERNAL: Function will throw an exception with the given message
   * if the string value is null or empty.
   */
  public static String failIfEmpty(String str, String message) {
    if (Utils.isValueEmpty(str)) {
      throw new DB2TMInputException(message);
    } else {
      return str;
    }
  }

  /**
   * INTERNAL: Conversion between two date formats.
   * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</html>
   */
  public static String convertDate(String date,
                                   String informat,
                                   String outformat) {
    if (Utils.isValueEmpty(date)) {
      return null;
    }
    
    try {
      SimpleDateFormat inf = new SimpleDateFormat(informat);
      SimpleDateFormat outf = new SimpleDateFormat(outformat);
      Date dateobj = inf.parse(date);
      return outf.format(dateobj);
    } catch (ParseException e) {
      throw new DB2TMInputException("Couldn't parse date: " + date, e);
    }
  }

  /**
   * INTERNAL: Replace all occurrences of a regex in a string with a new value.
   */
  public static String stringReplaceAll(String str, String regex, String toValue) {
    return (str == null) ? null : str.replaceAll(regex, toValue);
  }

  /**
   * INTERNAL: Turn a string into a suffix suitable for a PSI.
   */
  public static String makePSI(String str) {
    if (Utils.isValueEmpty(str)) {
      log.debug("No PSI suffix; empty string '{}'", str);
      return null;
    }

    char[] tmp = new char[str.length()];
    int pos = 0;
    for (int ix = 0; ix < str.length(); ix++) {
      char ch = str.charAt(ix);
      if (ch >= 'A' && ch <= 'Z') {
        tmp[pos++] = (char) ((int) ch + 32); // downcase
      } else if ((ch >= 'a' && ch <= 'z') ||
               (ch >= '0' && ch <= '9') ||
               ch == '-' && ch == '_') {
        tmp[pos++] = ch;
      } else if (ch == ' ') {
        tmp[pos++] = '-';
      }
    }

    str = new String(tmp, 0, pos);
    log.debug("Produced PSI suffix: '{}'", str);
    return str;
  }

  /**
   * INTERNAL: Returns the string minus all spaces. Differs from
   * trim() in that internal spaces are also removed.
   */
  public static String stripSpaces(String str) {
    if (str == null) { return null; }
    char[] buf = new char[str.length()];
    int pos = 0;
    for (int ix = 0; ix < buf.length; ix++) {
      char ch = str.charAt(ix);
      if (ch != ' ') {
        buf[pos++] = ch;
      }
    }
    return new String(buf, 0, pos);
  }
}
