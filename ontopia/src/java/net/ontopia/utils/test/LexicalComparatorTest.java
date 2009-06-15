// $Id: LexicalComparatorTest.java,v 1.7 2006/02/09 18:58:17 grove Exp $

package net.ontopia.utils.test;

import net.ontopia.utils.LexicalComparator;

public class LexicalComparatorTest extends AbstractComparatorTest {

  public LexicalComparatorTest(String name) {
    super(name);
  }

  public void testIdentical() {
    testComparator(LexicalComparator.CASE_SENSITIVE.compare("FOOBAR", "FOOBAR"), 0, 1);
  }

  public void testCaseDifference() {
    testComparator(LexicalComparator.CASE_SENSITIVE.compare("FOOBAR", "FOoBAR"), 1, 0);
  }

  public void testSmallerThan() {
    testComparator(LexicalComparator.CASE_SENSITIVE.compare("FOOBAR", "FOOAR"), 1, 0);
  }

  public void testGreaterThan() {
    testComparator(LexicalComparator.CASE_SENSITIVE.compare("FOOAR", "FOOBAR"), -1, 0);
  }

  public void testIgnoreCase() {
    testComparator(LexicalComparator.CASE_INSENSITIVE.compare("FOOBAR", "FOoBAR"), 0, 1);
  }

  public void testGreaterThanIgnore() {
    testComparator(LexicalComparator.CASE_INSENSITIVE.compare("FOOaR", "FOOBAR"), -1, 0);
  }

  public void testSmallerThanIgnore() {
    testComparator(LexicalComparator.CASE_INSENSITIVE.compare("FOOBAR", "FOOBAR"), 0, 1);
  }

}




