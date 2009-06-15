
// $Id: ImportExportUtilsTest.java,v 1.1 2007/01/05 10:20:57 grove Exp $

package net.ontopia.topicmaps.utils.test;

import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.test.AbstractOntopiaTestCase;

public class ImportExportUtilsTest extends AbstractOntopiaTestCase {

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
