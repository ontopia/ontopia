
package net.ontopia.topicmaps.db2tm.test;

import net.ontopia.topicmaps.db2tm.*;
import net.ontopia.test.AbstractOntopiaTestCase;


public class FunctionsTestCase extends AbstractOntopiaTestCase {

  public FunctionsTestCase(String name) {
    super(name);
  }

  public void testToUpperCase() {
    assertEquals("toUpperCase", Functions.toUpperCase("Geir Ove"), "GEIR OVE");
  }

  public void testToLowerCase() {
    assertEquals("toLowerCase", Functions.toLowerCase("Geir Ove"), "geir ove");
  }

  public void testTrim() {
    assertEquals("trimBoth", Functions.trim("  Geir Ove "), "Geir Ove");
    assertEquals("trimRight", Functions.trim("Geir Ove  "), "Geir Ove");
    assertEquals("trimLeft", Functions.trim("  Geir Ove"), "Geir Ove");
  }

  public void testSubstring() {
    assertEquals("substring", Functions.substring("Geir Ove", "3", "6"), "r O");
  }

  public void testIfEqualThenElse() {
    assertEquals("ifEqualThenElse", Functions.ifEqualThenElse("Foo", "Foo", "true", "false"), "true");
    assertEquals("ifEqualThenElse", Functions.ifEqualThenElse("Foo", "Bar", "true", "false"), "false");
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

  public void testCoalesce() {
    String a = "a";
    String b = "b";
    String e = "";
    String w = " ";
    String n = null;
    assertEquals("coalesce(a, b)", Functions.coalesce(a, b), a);
    assertEquals("coalesce(a, n)", Functions.coalesce(a, n), a);
    assertEquals("coalesce(a, e)", Functions.coalesce(a, e), a);
    assertEquals("coalesce(w, e)", Functions.coalesce(w, e), w);
    assertEquals("coalesce(n, b)", Functions.coalesce(n, b), b);
    assertEquals("coalesce(e, b)", Functions.coalesce(e, b), b);
    assertEquals("coalesce(n, n)", Functions.coalesce(n, n), n);
    assertEquals("coalesce(e, e)", Functions.coalesce(e, e), e);
    assertEquals("coalesce(a, n, b)", Functions.coalesce(a, n, b), a);
    assertEquals("coalesce(a, e, b)", Functions.coalesce(a, e, b), a);
    assertEquals("coalesce(w, e, b)", Functions.coalesce(w, e, b), w);
    assertEquals("coalesce(n, a, b)", Functions.coalesce(n, a, b), a);
    assertEquals("coalesce(e, a, b)", Functions.coalesce(e, a, b), a);
    assertEquals("coalesce(n, n, b)", Functions.coalesce(n, n, b), b);
    assertEquals("coalesce(e, e, b)", Functions.coalesce(e, e, b), b);
    assertEquals("coalesce(n, n, n)", Functions.coalesce(n, n, n), n);
    assertEquals("coalesce(e, e, e)", Functions.coalesce(e, e, e), e);
  }

  public void testCoalesceThen() {
    String a = "a";
    String b = "b";
    String c = "c";
    String d = "d";
    String w = " ";
    String e = "";
    String n = null;
    assertEquals("coalesceThen(a, b)", Functions.coalesceThen(a, b), b);
    assertEquals("coalesceThen(a, n)", Functions.coalesceThen(a, n), n);
    assertEquals("coalesceThen(a, e)", Functions.coalesceThen(a, e), e);
    assertEquals("coalesceThen(w, b)", Functions.coalesceThen(w, b), b);
    assertEquals("coalesceThen(n, b)", Functions.coalesceThen(n, b), n);
    assertEquals("coalesceThen(e, b)", Functions.coalesceThen(e, b), n);
    assertEquals("coalesceThen(n, n)", Functions.coalesceThen(n, n), n);
    assertEquals("coalesceThen(a, b, c, d)", Functions.coalesceThen(a, b, c, d), b);
    assertEquals("coalesceThen(n, a, c, d)", Functions.coalesceThen(n, a, c, d), d);
    assertEquals("coalesceThen(e, a, c, d)", Functions.coalesceThen(e, a, c, d), d);
    assertEquals("coalesceThen(n, a, w, d)", Functions.coalesceThen(n, a, c, d), d);
    assertEquals("coalesceThen(n, a, n, d)", Functions.coalesceThen(n, a, n, d), n);
    assertEquals("coalesceThen(e, a, e, d)", Functions.coalesceThen(e, a, e, d), n);
    assertEquals("coalesceThen(e, a, n, d)", Functions.coalesceThen(e, a, n, d), n);
    assertEquals("coalesceThen(n, a, e, d)", Functions.coalesceThen(n, a, e, d), n);
  }

  public void testFailIfEmpty() {
    try {
      Functions.failIfEmpty("", "some message");
      fail("failIfEmpty should have failed.");
    } catch (DB2TMInputException e) {
    }
    try {
      Functions.failIfEmpty(null, "some message");
      fail("failIfEmpty should have failed.");
    } catch (DB2TMInputException e) {
    }
    try {
      assertEquals("wrong return value", Functions.failIfEmpty("abc", "some message"), "abc");
    } catch (DB2TMInputException e) {
      fail("failIfEmpty shouldn't have failed.");
    }
    try {
      assertEquals("wrong return value", Functions.failIfEmpty(" ", "some message"), " ");;
    } catch (DB2TMInputException e) {
      fail("failIfEmpty shouldn't have failed.");
    }
  }

}
