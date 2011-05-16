
// $Id: ImportExportUtilsTest.java,v 1.1 2007/01/05 10:20:57 grove Exp $

package net.ontopia.topicmaps.utils;

import junit.framework.TestCase;

public class ImportExportUtilsTest extends TestCase {

  public ImportExportUtilsTest(String name) {
    super(name);
  }

  // --- Tests

  public void testGetTopicMapId() {
    testId("1", 1L);
    testId("123", 123L);
    testId("M1", 1L);
    testId("M123", 123L);
    testId("x-ontopia:tm-rdbms:1", 1L);
    testId("x-ontopia:tm-rdbms:123", 123L);
    testId("x-ontopia:tm-rdbms:M1", 1L);
    testId("x-ontopia:tm-rdbms:M123", 123L);
  }
  
  private void testId(String id, long y) {
    long x = ImportExportUtils.getTopicMapId(id);
    assertTrue("Invalid id: " + x + " (should have been: " + y + ")", x == y);
  }
  
}
