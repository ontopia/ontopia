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

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {
  
  @Test
  public void testEscapeEntitiesAmp() {
    assertEscapedEquals("intro & Co", "intro &amp; Co");
  }

  @Test
  public void testEscapeEntitiesLt() {
    assertEscapedEquals("23 < 42", "23 &lt; 42");
  }
  
  @Test
  public void testEscapeEntitiesTag() {
    assertEscapedEquals("<boring>", "&lt;boring&gt;");
  }
  
  @Test
  public void testEscapeEntitiesQuot() {
    assertEscapedEquals("Do know \"So, what?\"", "Do know &quot;So, what?&quot;");
  }
  
  protected void assertEscapedEquals(String to_esc, String expected) {
    String result = StringUtils.escapeHTMLEntities(to_esc);
    Assert.assertTrue("'" + result + "' did not equal the escaped string '" + expected +"'",
               result.equals(expected));
  }

  // --- makeRandomId test cases

  @Test
  public void testMakeRandomId() {
    String id = StringUtils.makeRandomId(10);
    Assert.assertTrue("random id had wrong length", id.length() == 10);
  }

  @Test
  public void testMakeTwoRandomIds() {
    String id1 = StringUtils.makeRandomId(10);
    String id2 = StringUtils.makeRandomId(10);
    Assert.assertTrue("random id1 had wrong length", id1.length() == 10);
    Assert.assertTrue("random id2 had wrong length", id2.length() == 10);
    Assert.assertTrue("random ids are equal!", !id1.equals(id2));
  }

  // --- normalizeId test cases

  @Test
  public void testNormalizeIdEmpty() {
    Assert.assertTrue("incorrect normalization of empty string",
               StringUtils.normalizeId("") == null);
  }

  @Test
  public void testNormalizeIdOK() {
    Assert.assertEquals("incorrect normalization",
                 StringUtils.normalizeId("abc"), "abc");
  }

  @Test
  public void testNormalizeIdOK1() {
    Assert.assertEquals("incorrect normalization",
                 StringUtils.normalizeId("a"), "a");
  }

  @Test
  public void testNormalizeIdLowerCase() {
    Assert.assertEquals("incorrect normalization",
                 StringUtils.normalizeId("ABCD"), "abcd");
  }

  @Test
  public void testNormalizeIdStripAccents() {
    String input = "ab\u00C6\u00D8\u00E5\u00E9\u00FF\u00FCab\u00CF";
    Assert.assertEquals("incorrect normalization",
                 StringUtils.normalizeId(input), "abeoaeyuabi");
  }

  @Test
  public void testNormalizeIdKeepSpecials() {
    Assert.assertEquals("incorrect normalization",
                 StringUtils.normalizeId("ab._-"), "ab._-");
  }

  @Test
  public void testNormalizeIdGetRidOfSpaces() {
    String id = StringUtils.normalizeId("  ab   ab  ");
    Assert.assertTrue("incorrect normalization, should be 'ab-ab', but was '" + id + "'",
               "ab-ab".equals(id));
  }

  @Test
  public void testNormalizeIdEarlyDiscard() {
    String id = StringUtils.normalizeId("@@ab");
    Assert.assertTrue("incorrect normalization, should be '__ab', but was '" + id + "'",
               "__ab".equals(id));
  }
}