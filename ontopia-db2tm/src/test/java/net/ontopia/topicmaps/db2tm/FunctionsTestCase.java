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

import org.junit.Test;
import org.junit.Assert;

public class FunctionsTestCase {

  @Test
  public void testToUpperCase() {
    Assert.assertEquals("toUpperCase", Functions.toUpperCase("Geir Ove"), "GEIR OVE");
  }

  @Test
  public void testToLowerCase() {
    Assert.assertEquals("toLowerCase", Functions.toLowerCase("Geir Ove"), "geir ove");
  }

  @Test
  public void testTrim() {
    Assert.assertEquals("trimEmpty", Functions.trim(""), "");
    Assert.assertEquals("trimOnlySpaces", Functions.trim("   "), "");
    Assert.assertEquals("trimBoth", Functions.trim("  Geir Ove "), "Geir Ove");
    Assert.assertEquals("trimRight", Functions.trim("Geir Ove  "), "Geir Ove");
    Assert.assertEquals("trimLeft", Functions.trim("  Geir Ove"), "Geir Ove");
  }

  @Test
  public void testStripSpaces() {
    Assert.assertEquals("stripEmpty", Functions.stripSpaces(""), "");
    Assert.assertEquals("stripOnlySpaces", Functions.stripSpaces("   "), "");
    Assert.assertEquals("stripBoth", Functions.stripSpaces("  Geir Ove "), "GeirOve");
    Assert.assertEquals("stripRight", Functions.stripSpaces("Geir Ove  "), "GeirOve");
    Assert.assertEquals("stripLeft", Functions.stripSpaces("  Geir Ove"), "GeirOve");
  }

  @Test
  public void testSubstring() {
    Assert.assertEquals("substring", Functions.substring("Geir Ove", "3", "6"), "r O");
  }

  @Test
  public void testIfEqualThenElse() {
    Assert.assertEquals("ifEqualThenElse", Functions.ifEqualThenElse("Foo", "Foo", "true", "false"), "true");
    Assert.assertEquals("ifEqualThenElse", Functions.ifEqualThenElse("Foo", "Bar", "true", "false"), "false");
  }

  //! public void testUseFirstIfDifferent() {
  //!   assertEquals("useFirstIfDifferent", Functions.ifEqualThenElse("Foo", "Bar", "true", "false"), "true");
  //!   assertEquals("useFirstIfDifferent", Functions.ifEqualThenElse("Foo", "Foo", "true", "false"), "false");
  //! }
  //! 
  //! public void testUseFirstIfEqual() {
  //!   assertEquals("useFirstIfDifferent", Functions.ifEqualThenElse("Foo", "Bar", "true", "false"), "true");
  //!   assertEquals("useFirstIfDifferent", Functions.ifEqualThenElse("Foo", "Foo", "true", "false"), "false");
  //! }

  @Test
  public void testCoalesce() {
    String a = "a";
    String b = "b";
    String e = "";
    String w = " ";
    String n = null;
    Assert.assertEquals("coalesce(a, b)", Functions.coalesce(a, b), a);
    Assert.assertEquals("coalesce(a, n)", Functions.coalesce(a, n), a);
    Assert.assertEquals("coalesce(a, e)", Functions.coalesce(a, e), a);
    Assert.assertEquals("coalesce(w, e)", Functions.coalesce(w, e), w);
    Assert.assertEquals("coalesce(n, b)", Functions.coalesce(n, b), b);
    Assert.assertEquals("coalesce(e, b)", Functions.coalesce(e, b), b);
    Assert.assertEquals("coalesce(n, n)", Functions.coalesce(n, n), n);
    Assert.assertEquals("coalesce(e, e)", Functions.coalesce(e, e), e);
    Assert.assertEquals("coalesce(a, n, b)", Functions.coalesce(a, n, b), a);
    Assert.assertEquals("coalesce(a, e, b)", Functions.coalesce(a, e, b), a);
    Assert.assertEquals("coalesce(w, e, b)", Functions.coalesce(w, e, b), w);
    Assert.assertEquals("coalesce(n, a, b)", Functions.coalesce(n, a, b), a);
    Assert.assertEquals("coalesce(e, a, b)", Functions.coalesce(e, a, b), a);
    Assert.assertEquals("coalesce(n, n, b)", Functions.coalesce(n, n, b), b);
    Assert.assertEquals("coalesce(e, e, b)", Functions.coalesce(e, e, b), b);
    Assert.assertEquals("coalesce(n, n, n)", Functions.coalesce(n, n, n), n);
    Assert.assertEquals("coalesce(e, e, e)", Functions.coalesce(e, e, e), e);
  }

  @Test
  public void testCoalesceThen() {
    String a = "a";
    String b = "b";
    String c = "c";
    String d = "d";
    String w = " ";
    String e = "";
    String n = null;
    Assert.assertEquals("coalesceThen(a, b)", Functions.coalesceThen(a, b), b);
    Assert.assertEquals("coalesceThen(a, n)", Functions.coalesceThen(a, n), n);
    Assert.assertEquals("coalesceThen(a, e)", Functions.coalesceThen(a, e), e);
    Assert.assertEquals("coalesceThen(w, b)", Functions.coalesceThen(w, b), b);
    Assert.assertEquals("coalesceThen(n, b)", Functions.coalesceThen(n, b), n);
    Assert.assertEquals("coalesceThen(e, b)", Functions.coalesceThen(e, b), n);
    Assert.assertEquals("coalesceThen(n, n)", Functions.coalesceThen(n, n), n);
    Assert.assertEquals("coalesceThen(a, b, c, d)", Functions.coalesceThen(a, b, c, d), b);
    Assert.assertEquals("coalesceThen(n, a, c, d)", Functions.coalesceThen(n, a, c, d), d);
    Assert.assertEquals("coalesceThen(e, a, c, d)", Functions.coalesceThen(e, a, c, d), d);
    Assert.assertEquals("coalesceThen(n, a, w, d)", Functions.coalesceThen(n, a, c, d), d);
    Assert.assertEquals("coalesceThen(n, a, n, d)", Functions.coalesceThen(n, a, n, d), n);
    Assert.assertEquals("coalesceThen(e, a, e, d)", Functions.coalesceThen(e, a, e, d), n);
    Assert.assertEquals("coalesceThen(e, a, n, d)", Functions.coalesceThen(e, a, n, d), n);
    Assert.assertEquals("coalesceThen(n, a, e, d)", Functions.coalesceThen(n, a, e, d), n);
  }

  @Test
  public void testFailIfEmpty() {
    try {
      Functions.failIfEmpty("", "some message");
      Assert.fail("failIfEmpty should have failed.");
    } catch (DB2TMInputException e) {
    }
    try {
      Functions.failIfEmpty(null, "some message");
      Assert.fail("failIfEmpty should have failed.");
    } catch (DB2TMInputException e) {
    }
    try {
      Assert.assertEquals("wrong return value", Functions.failIfEmpty("abc", "some message"), "abc");
    } catch (DB2TMInputException e) {
      Assert.fail("failIfEmpty shouldn't have failed.");
    }
    try {
      Assert.assertEquals("wrong return value", Functions.failIfEmpty(" ", "some message"), " ");
    } catch (DB2TMInputException e) {
      Assert.fail("failIfEmpty shouldn't have failed.");
    }
  }

  @Test
  public void testMakePsi() {
    Assert.assertEquals("abcabc-foo-bar", Functions.makePSI("abcABC foo BAR"));
  }
  
  @Test
  public void testMakePsiDoesNotStripBoundaryChars() {
    Assert.assertEquals("1979za", Functions.makePSI("1979za"));
  }

  @Test
  public void testMakePsiBadChars() {
    Assert.assertEquals("is-a--fail", Functions.makePSI("'is a \u00E6\u00F8\u00E5 #fail'"));
  }
}
