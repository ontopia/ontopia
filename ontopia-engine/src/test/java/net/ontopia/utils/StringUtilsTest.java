
package net.ontopia.utils;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {
  
  public StringUtilsTest(String name) {
    super(name);
  }

  protected void setUp() {
  }

  protected void tearDown() {
  }

  // --- normalize test cases
  
  public void testNormalizeEmpty() {
    verifyNormalize("", "");
  }
  
  public void testNormalizeWord() {
    verifyNormalize("abc", "abc");
  }

  public void testNormalizeSpaces() {
    verifyNormalize("    ", " ");
  }

  public void testNormalizeWordWithSpace() {
    verifyNormalize("abc def", "abc def");
  }

  public void testNormalizeWordWithSpaces() {
    verifyNormalize("abc       def", "abc def");
  }

  public void testNormalizeWordWithFunnyChar() {
    verifyNormalize2("abc\tdef", "abc def");
  }

  public void testNormalizeWordWithFunnyChars() {
    verifyNormalize("abc  \r\t\n     def", "abc def");
  }

  public void testNormalizeWordWithSpacesAtBothEnds() {
    verifyNormalize("     abc def     ", " abc def ");
  }

  protected void verifyNormalize(String source, String target) {
    String result = StringUtils.normalizeWhitespace(source);
    assertTrue("'" + source + "'should normalize to '" + target +
               "', got '" + result + "'", target.equals(result));
  }

  // --- normalizeIsWhitespace test cases
  
  public void testNormalizeEmpty2() {
    verifyNormalize2("", "");
  }
  
  public void testNormalizeWord2() {
    verifyNormalize2("abc", "abc");
  }

  public void testNormalizeSpaces2() {
    verifyNormalize2("    ", " ");
  }

  public void testNormalizeWordWithSpace2() {
    verifyNormalize2("abc def", "abc def");
  }

  public void testNormalizeWordWithSpaces2() {
    verifyNormalize2("abc       def", "abc def");
  }

  public void testNormalizeWordWithFunnyChar2() {
    verifyNormalize2("abc\u000Bdef", "abc def");
  }

  public void testNormalizeWordWithFunnyChars2() {
    verifyNormalize2("abc  \r\t\n\u000B     def", "abc def");
  }

  public void testNormalizeWordWithSpacesAtBothEnds2() {
    verifyNormalize2("     abc def     ", " abc def ");
  }

  protected void verifyNormalize2(String source, String target) {
    String result = StringUtils.normalizeIsWhitespace(source);
    assertTrue("'" + source + "'should normalize to '" + target +
               "', got '" + result + "'", target.equals(result));
  }
  
  // --- regionEquals test cases

  public void testREEmpty() {
    verifyRegionEquals("", new char[]{}, 0, 0);
  }
  
  public void testREUnequal() {
    verifyRegionNotEquals("abc", new char[]{}, 0, 0);
  }
  
  public void testRERegion() {
    verifyRegionEquals("bcd", new char[]{'a', 'b', 'c', 'd', 'e'}, 1, 3);
  }
  
  public void testRERegionMiss() {
    verifyRegionNotEquals("bcd", new char[]{'a', 'b', 'c', 'd', 'e'}, 1, 4);
  }
  
  public void testRERegionMiss2() {
    verifyRegionNotEquals("bcd", new char[]{'a', 'b', 'c', 'e', 'd'}, 1, 3);
  }
  
  public void testRERegionWhole() {
    verifyRegionEquals("abc", new char[]{'a', 'b', 'c'}, 0, 3);
  }
  
  public void testRERegionEnd() {
    verifyRegionEquals("bcd", new char[]{'a', 'b', 'c', 'd'}, 1, 3);
  }
  
  protected void verifyRegionEquals(String str, char[] ch, int st, int len) {
    assertTrue("'" + str + "' did not equal " + ch + "[" + st + ":" + (st+len) + "]",
               StringUtils.regionEquals(str, ch, st, len));
  }
  
  protected void verifyRegionNotEquals(String str, char[] ch, int st, int len) {
    assertTrue("'" + str + "' equalled " + ch + "[" + st + ":" + (st+len) + "]",
               !StringUtils.regionEquals(str, ch, st, len));
  }


  public void testEscapeEntitiesAmp() {
    verifyEscapedEquals("intro & Co", "intro &amp; Co");
  }

  public void testEscapeEntitiesLt() {
    verifyEscapedEquals("23 < 42", "23 &lt; 42");
  }
  
  public void testEscapeEntitiesTag() {
    verifyEscapedEquals("<boring>", "&lt;boring&gt;");
  }
  
  public void testEscapeEntitiesQuot() {
    verifyEscapedEquals("Do know \"So, what?\"", "Do know &quot;So, what?&quot;");
  }
  
  protected void verifyEscapedEquals(String to_esc, String expected) {
    String result = StringUtils.escapeHTMLEntities(to_esc);
    assertTrue("'" + result + "' did not equal the escaped string '" + expected +"'",
               result.equals(expected));
  }

  // --- pad test cases
  public void testPadStringZeros() {
    // pad(String,char, int)
    padZeros("", "00000000", 8);
    padZeros("1", "00000001", 8);
    padZeros("12", "00000012", 8);
    padZeros("123", "00000123", 8);
    padZeros("1234", "00001234", 8);
    padZeros("12345", "00012345", 8);
    padZeros("123456", "00123456", 8);
    padZeros("1234567", "01234567", 8);
    padZeros("12345678", "12345678", 8);
    padZeros("123456789", "23456789", 8);
  }

  public void testPadIntZeros() {
    // pad(int,char, int)
    padZeros(1, "00000001", 8);
    padZeros(12, "00000012", 8);
    padZeros(123, "00000123", 8);
    padZeros(1234, "00001234", 8);
    padZeros(12345, "00012345", 8);
    padZeros(123456, "00123456", 8);
    padZeros(1234567, "01234567", 8);
    padZeros(12345678, "12345678", 8);
    padZeros(123456789, "23456789", 8);
  }

  protected void padZeros(String num, String correct, int length) {
    String result = StringUtils.pad(num, '0', length);
    assertTrue("String \"" + num + "\" not correctly padded: \"" + result + "\"", correct.equals(result));
  }

  protected void padZeros(int num, String correct, int length) {
    String result = StringUtils.pad(num, '0', length);
    assertTrue("String \"" + num + "\" not correctly padded: \"" + result + "\"", correct.equals(result));
  }
  
  // --- makeRandomId test cases

  public void testMakeRandomId() {
    String id = StringUtils.makeRandomId(10);
    assertTrue("random id had wrong length", id.length() == 10);
  }

  public void testMakeTwoRandomIds() {
    String id1 = StringUtils.makeRandomId(10);
    String id2 = StringUtils.makeRandomId(10);
    assertTrue("random id1 had wrong length", id1.length() == 10);
    assertTrue("random id2 had wrong length", id2.length() == 10);
    assertTrue("random ids are equal!", !id1.equals(id2));
  }

  // --- normalizeId test cases

  public void testNormalizeIdEmpty() {
    assertTrue("incorrect normalization of empty string",
               StringUtils.normalizeId("") == null);
  }

  public void testNormalizeIdOK() {
    assertEquals("incorrect normalization",
                 StringUtils.normalizeId("abc"), "abc");
  }

  public void testNormalizeIdOK1() {
    assertEquals("incorrect normalization",
                 StringUtils.normalizeId("a"), "a");
  }

  public void testNormalizeIdLowerCase() {
    assertEquals("incorrect normalization",
                 StringUtils.normalizeId("ABCD"), "abcd");
  }

  public void testNormalizeIdStripAccents() {
    //String input = "ab∆ÿ≈Èˇ¸abœ"; got to replace this with \u0000 escapes :-(
    String input = "ab\u00C6\u00D8\u00E5\u00E9\u00FF\u00FCab\u00CF";
    assertEquals("incorrect normalization",
                 StringUtils.normalizeId(input), "abeoaeyuabi");
  }

  public void testNormalizeIdKeepSpecials() {
    assertEquals("incorrect normalization",
                 StringUtils.normalizeId("ab._-"), "ab._-");
  }

  public void testNormalizeIdGetRidOfSpaces() {
    String id = StringUtils.normalizeId("  ab   ab  ");
    assertTrue("incorrect normalization, should be 'ab-ab', but was '" + id + "'",
               "ab-ab".equals(id));
  }

  public void testNormalizeIdEarlyDiscard() {
    String id = StringUtils.normalizeId("@@ab");
    assertTrue("incorrect normalization, should be '__ab', but was '" + id + "'",
               "__ab".equals(id));
  }
}
