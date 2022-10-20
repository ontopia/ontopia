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

import java.io.IOException;
import java.io.Writer;
import java.util.Random;

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
   * INTERNAL: Filters the specified string for characters that are
   * senstive to HTML interpreters, returning the string with these
   * characters replaced by the corresponding character entities.
   *
   * @param value The string to be filtered and returned
   *
   * @since 1.3.1
   */
  public static String escapeHTMLEntities(String value) {
    if (value == null) {
      return null;
    }

    return value
            .replace("&", "&amp;") // order matters
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("\'", "&#39;");
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
    if (value == null) {
      return;
    }

    out.write(escapeHTMLEntities(value));
  }

  /**
   * INTERNAL: Make a random ID-like string of the given number of
   * characters.   
   */
  public static String makeRandomId(int length) {
    char[] chars = new char[length];
    for (int ix = 0; ix < length; ix++) {
      chars[ix] = (char) (65 + rand.nextInt(26));
    }
    return new String(chars);
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
      if (ch > 0x00FF) {
        continue;
      }

      // handle whitespace
      if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
        if (!firstchar || whitespacerun) {
          continue;
        }
        ch = '-';
        whitespacerun = true;
      } else {
        whitespacerun = false;
      }
      
      // check mapping table
      char mapsto = charmap[ch];
      if (mapsto == 0 && firstchar) {
        continue;
      }        

      // update buffer
      if (mapsto == 0) {
        // discards before first NAME char turn into underscores
        buffer[outix++] = '_';
      } else {
        buffer[outix++] = mapsto;
        firstchar = true;
      }
    }

    // whitespace at end will leave a trailing '-', which needs to go
    if (whitespacerun) {
      outix--; // leave out last '-'
    }

    // check if we have a valid name start character first
    if (outix < 1) {
      return null;
    } else if (isNameStart(buffer[0])) {
      return new String(buffer, 0, outix);
    } else {
      return "_" + new String(buffer, 0, outix);
    }
  }

  private static boolean isNameStart(char ch) {
    return (ch >= 'A' && ch <= 'Z') ||
           (ch >= 'a' && ch <= 'z') ||
           ch == '_';      
  }

  // Accent-stripping and lowercasing map used for name->ID normalization
  // Auto-generated from unicodedata.txt
  private static final char[] charmap = new char[]{
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
