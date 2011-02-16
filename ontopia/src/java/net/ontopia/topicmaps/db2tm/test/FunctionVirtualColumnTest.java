
package net.ontopia.topicmaps.db2tm.test;

import net.ontopia.topicmaps.db2tm.*;
import net.ontopia.test.AbstractOntopiaTestCase;

public class FunctionVirtualColumnTest extends AbstractOntopiaTestCase {

  public FunctionVirtualColumnTest(String name) {
    super(name);
  }

  /**
   * Tests normal, successful execution.
   */
  public void testNormal() {
    FunctionVirtualColumn func = new FunctionVirtualColumn(null, "column", "net.ontopia.topicmaps.db2tm.Functions.trim");
    func.addParameter(" a ");
    func.compile();
    assertEquals("function returned incorrect value", "a", func.getValue(null));
  }

  /**
   * Tests error reporting.
   */
  public void testNPE() {
    FunctionVirtualColumn func = new FunctionVirtualColumn(null, "gurble", "net.ontopia.topicmaps.db2tm.Functions.convertDate");
    func.addParameter("12323");
    func.addParameter("");
    func.addParameter("");
    func.compile();

    try {
      func.getValue(null);
      fail("ooops! function should fail.");
    } catch (DB2TMInputException e) {
      String msg = e.getMessage();
      assertTrue("error message did not contain name of column",
                 msg.indexOf("gurble") != -1);
      assertTrue("error message did not contain input value",
                 msg.indexOf("12323") != -1);
    }
  }
}
