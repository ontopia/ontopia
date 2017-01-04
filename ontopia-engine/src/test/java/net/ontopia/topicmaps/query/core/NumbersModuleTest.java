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

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class NumbersModuleTest extends AbstractPredicateTest {

  private final static String PREFIX = 
    "import \"http://psi.ontopia.net/tolog/numbers/\" as numbers ";

  // --- value(string, result, pattern?, locale?)
  @Test
  public void testNumbersValueClosedInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:value(\"1234\", 1234)?");
  }
  @Test
  public void testNumbersValueClosedNegativeInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:value(\"-1234\", -1234)?");
  }
  @Test
  public void testNumbersValueClosedIntegerNotMatch() throws InvalidQueryException, IOException {
    load("numbers.ltm");
      List matches = new ArrayList(); // false
    assertQueryMatches(matches, PREFIX + "numbers:value(\"1234\", 1235)?");
  }
  @Test
  public void testNumbersValueClosedIntegerAsFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:value(\"1234.0\", 1234.0)?");
  }
  @Test
  public void testNumbersValueClosedIntegerAsFloatNotMatch() throws InvalidQueryException, IOException {
    load("numbers.ltm");
      List matches = new ArrayList(); // false
    assertQueryMatches(matches, PREFIX + "numbers:value(\"1234.0\", 1234)?");
  }
  @Test
  public void testNumbersValueClosedFloatAsIntegerNotMatch() throws InvalidQueryException, IOException {
    load("numbers.ltm");
      List matches = new ArrayList(); // false
    assertQueryMatches(matches, PREFIX + "numbers:value(\"1234\", 1234.0)?");
  }
  @Test
  public void testNumbersValueClosedFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:value(\"1234.5678\", 1234.5678)?");
  }
  @Test
  public void testNumbersValueClosedFloatNotMatch() throws InvalidQueryException, IOException {
    load("numbers.ltm");
      List matches = new ArrayList(); // false
    assertQueryMatches(matches, PREFIX + "numbers:value(\"1234.5678\", 1235.8632)?");
  }
  @Test
  public void testNumbersValueOpenInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
      addMatch(matches, "result", 1234);
    assertQueryMatches(matches, PREFIX + "numbers:value(\"1234\", $result)?");
  }
  @Test
  public void testNumbersValueOpenNegativeInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
      addMatch(matches, "result", -1234);
    assertQueryMatches(matches, PREFIX + "numbers:value(\"-1234\", $result)?");
  }
  @Test
  public void testNumbersValueOpenIntegerAsFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 1234.0f);
    assertQueryMatches(matches, PREFIX + "numbers:value(\"1234.0\", $result)?");
  }
  @Test
  public void testNumbersValueOpenFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
      addMatch(matches, "result", 1234.5678f);
    assertQueryMatches(matches, PREFIX + "numbers:value(\"1234.5678\", $result)?");
  }
  @Test
  public void testNumbersValueOpenNegativeFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
      addMatch(matches, "result", -1234.5678f);
    assertQueryMatches(matches, PREFIX + "numbers:value(\"-1234.5678\", $result)?");
  }
  @Test
  public void testNumbersValueClosedPatternLocale() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:value(\"1.234.567,89\", 1234567.8901234, \"#,##0.00\", \"NL\")?");
  }
  @Test
  public void testNumbersValueOpenPatternLocale() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 1234567.8901234f);
    assertQueryMatches(matches, PREFIX + "numbers:value(\"1.234.567,89\", $result, \"#,##0.00\", \"NL\")?");
  }
  @Test
  public void testNumbersValueInvalidPattern() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    try {
      assertQuery(PREFIX + "numbers:value(\"1234\", 1234, \"#.##0,##\")?");
      Assert.fail("Failed to detect invalid pattern");
    } catch (InvalidQueryException e) {
      // Invalid pattern detected correctly
    }
  }
  @Test
  public void testNumbersValueInvalidPatternLocale() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    try {
      assertQuery(PREFIX + "numbers:value(\"1234\", 1234, \"#.##0,##\", \"NL\")?");
      Assert.fail("Failed to detect invalid pattern");
    } catch (InvalidQueryException e) {
      // Invalid pattern detected correctly
    }
  }

  // --- format(number, result, pattern?, locale?)
  @Test
  public void testNumbersFormatClosedInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:format(1234, \"1234\")?");
  }
  @Test
  public void testNumbersFormatOpenInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", "1234");
    assertQueryMatches(matches, PREFIX + "numbers:format(1234, $result)?");
  }
  @Test
  public void testNumbersFormatClosedFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:format(1234.5678, \"1234.5677\")?"); // Float works in mysterious ways
  }
  @Test
  public void testNumbersFormatOpenFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", "1234.5677"); // Float works in mysterious ways
    assertQueryMatches(matches, PREFIX + "numbers:format(1234.5678, $result)?");
  }
  @Test
  public void testNumbersFormatClosedPatternLocale() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:format(1234, \"1.234,00\", \"#,##0.00\", \"NL\")?");
  }
  @Test
  public void testNumbersFormatOpenPatternLocale() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", "1.234,00");
    assertQueryMatches(matches, PREFIX + "numbers:format(1234, $result, \"#,##0.00\", \"NL\")?");
  }
  @Test
  public void testNumbersFormatInvalidPattern() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    try {
      assertQuery(PREFIX + "numbers:format(1234, \"1234\", \"#.##0,##\")?");
      Assert.fail("Failed to detect invalid pattern");
    } catch (InvalidQueryException e) {
      // Invalid pattern detected correctly
    }
  }
  @Test
  public void testNumbersFormatInvalidPatternLocale() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    try {
      assertQuery(PREFIX + "numbers:format(1234, \"1234\", \"#.##0,##\", \"NL\")?");
      Assert.fail("Failed to detect invalid pattern");
    } catch (InvalidQueryException e) {
      // Invalid pattern detected correctly
    }
  }

  // --- absolute(number, result)
  @Test
  public void testNumbersAbsoluteClosedPositiveInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:absolute(1234, 1234)?");
  }
  @Test
  public void testNumbersAbsoluteClosedNegativeInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:absolute(-1234, 1234)?");
  }
  @Test
  public void testNumbersAbsoluteClosedPositiveFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:absolute(1234.5678, 1234.5678)?");
  }
  @Test
  public void testNumbersAbsoluteClosedNegativeFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:absolute(-1234.5678, 1234.5678)?");
  }
  @Test
  public void testNumbersAbsoluteOpenPositiveInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 1234);
    assertQueryMatches(matches, PREFIX + "numbers:absolute(1234, $result)?");
  }
  @Test
  public void testNumbersAbsoluteOpenNegativeInteger() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 1234);
    assertQueryMatches(matches, PREFIX + "numbers:absolute(-1234, $result)?");
  }
  @Test
  public void testNumbersAbsoluteOpenPositiveFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 1234.5678f);
    assertQueryMatches(matches, PREFIX + "numbers:absolute(1234.5678, $result)?");
  }
  @Test
  public void testNumbersAbsoluteOpenNegativeFloat() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 1234.5678f);
    assertQueryMatches(matches, PREFIX + "numbers:absolute(-1234.5678, $result)?");
  }

  // --- add(result, number, number+)
  @Test
  public void testNumbersAddClosedIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:add(6912, 1234, 5678)?");
  }
  @Test
  public void testNumbersAddClosedFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:add(" + (1234.5678f + 8765.4321f) + ", 1234.5678, 8765.4321)?");
  }
  @Test
  public void testNumbersAddClosedIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:add(-88.0, 12, 34.0, -56, -78)?");
  }
  @Test
  public void testNumbersAddOpenIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 6912);
    assertQueryMatches(matches, PREFIX + "numbers:add($result, 1234, 5678)?");
  }
  @Test
  public void testNumbersAddOpenFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 1234.5678f + 8765.4321f);
    assertQueryMatches(matches, PREFIX + "numbers:add($result, 1234.5678, 8765.4321)?");
  }
  @Test
  public void testNumbersAddOpenIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", -88.0f);
    assertQueryMatches(matches, PREFIX + "numbers:add($result, 12, 34.0, -56, -78)?");
  }

  // --- subtract(result, number, number+)
  @Test
  public void testNumbersSubtractClosedIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:subtract(-4444, 1234, 5678)?");
  }
  @Test
  public void testNumbersSubtractClosedFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:subtract(" + (1234.5678f - 8765.4321f) + ", 1234.5678, 8765.4321)?");
  }
  @Test
  public void testNumbersSubtractClosedIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:subtract(" + (-1234f + 5678f - 4039.596f) + ", -1234, -5678, 4039.596)?");
  }
  @Test
  public void testNumbersSubtractOpenIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", -4444);
    assertQueryMatches(matches, PREFIX + "numbers:subtract($result, 1234, 5678)?");
  }
  @Test
  public void testNumbersSubtractOpenFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 1234.5678f - 8765.4321f);
    assertQueryMatches(matches, PREFIX + "numbers:subtract($result, 1234.5678, 8765.4321)?");
  }
  @Test
  public void testNumbersSubtractOpenIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", -1234f + 5678f - 4039.596f);
    assertQueryMatches(matches, PREFIX + "numbers:subtract($result, -1234, -5678, 4039.596)?");
  }

  // --- multiply(result, number, number+)
  @Test
  public void testNumbersMultiplyClosedIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:multiply(408, 12, 34)?");
  }
  @Test
  public void testNumbersMultiplyClosedFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:multiply(700.6652, 12.34, 56.78)?");
  }
  @Test
  public void testNumbersMultiplyClosedIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:multiply(-51.0, -12, 34, -0.25, -0.5)?");
  }
  @Test
  public void testNumbersMultiplyOpenIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 408);
    assertQueryMatches(matches, PREFIX + "numbers:multiply($result, 12, 34)?");
  }
  @Test
  public void testNumbersMultiplyOpenFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 700.6652f);
    assertQueryMatches(matches, PREFIX + "numbers:multiply($result, 12.34, 56.78)?");
  }
  @Test
  public void testNumbersMultiplyOpenIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", -51.0f);
    assertQueryMatches(matches, PREFIX + "numbers:multiply($result, -12, 34, -0.25, -0.5)?");
  }

  // --- divide(result, number, number+)
  @Test
  public void testNumbersDivideClosedIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:divide(3, 56, 16)?");
  }
  @Test
  public void testNumbersDivideClosedFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:divide(-3.5, 56.0, -16.0)?");
  }
  @Test
  public void testNumbersDivideClosedIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:divide(-45.2, -678, 6, 2.5)?");
  }
  @Test
  public void testNumbersDivideOpenIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 3);
    assertQueryMatches(matches, PREFIX + "numbers:divide($result, 56, 16)?");
  }
  @Test
  public void testNumbersDivideOpenFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 3.5f);
    assertQueryMatches(matches, PREFIX + "numbers:divide($result, 56.0, 16.0)?");
  }
  @Test
  public void testNumbersDivideOpenIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", -45.2f);
    assertQueryMatches(matches, PREFIX + "numbers:divide($result, -678, 6, 2.5)?");
  }
  @Test
  public void testNumbersDivideByZero() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    try {
      assertQuery(PREFIX + "numbers:divide(2, 1, 0)?");
      Assert.fail("Failed to detect divide by zero exception");
    } catch (InvalidQueryException e) {
      // OK, exception should occur
    }
  }

  // --- min(result, number, number+)
  @Test
  public void testNumbersMinimumClosedIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:min(101, 105, 103, 108, 101, 105)?");
  }
  @Test
  public void testNumbersMinimumClosedFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:min(-1.234, 6.7, -0.1, 8.9, -1.234)?");
  }
  @Test
  public void testNumbersMinimumClosedIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:min(-4, -4, 1.2345)?");
  }
  @Test
  public void testNumbersMinimumOpenIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 101);
    assertQueryMatches(matches, PREFIX + "numbers:min($result, 105, 103, 108, 101, 105)?");
  }
  @Test
  public void testNumbersMinimumOpenFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", -1.234f);
    assertQueryMatches(matches, PREFIX + "numbers:min($result, 6.7, -0.1, 8.9, -1.234)?");
  }
  @Test
  public void testNumbersMinimumOpenIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", -4);
    assertQueryMatches(matches, PREFIX + "numbers:min($result, -4, 1.2345)?");
  }

  // --- max(result, number, number+)
  @Test
  public void testNumbersMaximumClosedIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:max(108, 105, 103, 108, 101, 105)?");
  }
  @Test
  public void testNumbersMaximumClosedFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:max(8.9, 6.7, -0.1, 8.9, -1.234)?");
  }
  @Test
  public void testNumbersMaximumClosedIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    assertQuery(PREFIX + "numbers:max(4, 4, -1.2345)?");
  }
  @Test
  public void testNumbersMaximumOpenIntegers() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 108);
    assertQueryMatches(matches, PREFIX + "numbers:max($result, 105, 103, 108, 101, 105)?");
  }
  @Test
  public void testNumbersMaximumOpenFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 8.9f);
    assertQueryMatches(matches, PREFIX + "numbers:max($result, 6.7, -0.1, 8.9, -1.234)?");
  }
  @Test
  public void testNumbersMaximumOpenIntegersAndFloats() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "result", 4);
    assertQueryMatches(matches, PREFIX + "numbers:max($result, 4, -1.2345)?");
  }

  // putting it all together
  @Test
  public void testNumbersPIAT() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "name", "Object 3", "percentage", "196,88 %", "quotient", 1.96875f);
    addMatch(matches, "name", "Object 5", "percentage",  "25,17 %", "quotient", 0.2516892f);
    addMatch(matches, "name", "Object 6", "percentage",  "10,00 %", "quotient", 0.1f);
    addMatch(matches, "name", "Object 1", "percentage",   "8,33 %", "quotient", 0.083333336f);
    assertQueryOrder(matches, PREFIX + 
      "select $name, $percentage, $quotient from " +
      "topic-name($object, $objectname)," +
      "value($objectname, $name)," +
      "value-1($object, $o1)," +
      "value-2($object, $o2)," +
      "numbers:value($o1, $value1, \"#,##0.00\", \"NL\")," +
      "numbers:value($o2, $value2, \"#,##0.00\", \"NL\")," +
      "numbers:min($min, $value1, $value2)," +
      "numbers:max($max, $value1, $value2)," +
      "numbers:subtract($diff, $max, $min)," +
      "numbers:divide($quotient, $diff, $min)," +
      "$quotient > 0.05," +
      "numbers:format($quotient, $percentage, \"#0.00 %\", \"NL\")" +
      "order by $quotient DESC?"
    );
  }

  // sorting
  @Test
  public void testIntegerSorting() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "value", 6);
    addMatch(matches, "value", 45);
    addMatch(matches, "value", 78);
    addMatch(matches, "value", 123);
    assertQueryOrder(matches, PREFIX +
      "select $value from " +
      "{ numbers:value(\"6\", $value)" +
      "| numbers:value(\"78\", $value)" +
      "| numbers:value(\"45\", $value)" +
      "| numbers:value(\"123\", $value)" +
      "} order by $value?"
    );
  }
  @Test
  public void testFloatSorting() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "value", 6.0f);
    addMatch(matches, "value", 45.0f);
    addMatch(matches, "value", 78.0f);
    addMatch(matches, "value", 123.0f);
    assertQueryOrder(matches, PREFIX +
      "select $value from " +
      "{ numbers:value(\"6.0\", $value)" +
      "| numbers:value(\"78.0\", $value)" +
      "| numbers:value(\"45.0\", $value)" +
      "| numbers:value(\"123.0\", $value)" +
      "} order by $value?"
    );
  }
  @Test
  public void testIntegerAndFloatSorting() throws InvalidQueryException, IOException {
    load("numbers.ltm");
    List matches = new ArrayList();
    addMatch(matches, "value", 6.0f);
    addMatch(matches, "value", 45);
    addMatch(matches, "value", 78.0f);
    addMatch(matches, "value", 123);
    assertQueryOrder(matches, PREFIX +
      "select $value from " +
      "{ numbers:value(\"6.0\", $value)" +
      "| numbers:value(\"78.0\", $value)" +
      "| numbers:value(\"45\", $value)" +
      "| numbers:value(\"123\", $value)" +
      "} order by $value?"
    );
  }

}
