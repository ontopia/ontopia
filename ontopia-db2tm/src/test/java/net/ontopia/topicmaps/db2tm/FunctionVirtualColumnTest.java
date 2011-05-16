
package net.ontopia.topicmaps.db2tm;

import org.junit.Test;
import org.junit.Assert;

public class FunctionVirtualColumnTest {

  /**
   * Tests normal, successful execution.
   */
  @Test
  public void testNormal() {
    FunctionVirtualColumn func = new FunctionVirtualColumn(null, "column", "net.ontopia.topicmaps.db2tm.Functions.trim");
    func.addParameter(" a ");
    func.compile();
    Assert.assertEquals("function returned incorrect value", "a", func.getValue(null));
  }

  /**
   * Tests error reporting.
   */
  @Test
  public void testNPE() {
    FunctionVirtualColumn func = new FunctionVirtualColumn(null, "gurble", "net.ontopia.topicmaps.db2tm.Functions.convertDate");
    func.addParameter("12323");
    func.addParameter("");
    func.addParameter("");
    func.compile();

    try {
      func.getValue(null);
      Assert.fail("ooops! function should fail.");
    } catch (DB2TMInputException e) {
      String msg = e.getMessage();
      Assert.assertTrue("error message did not contain name of column",
                 msg.indexOf("gurble") != -1);
      Assert.assertTrue("error message did not contain input value",
                 msg.indexOf("12323") != -1);
    }
  }
}
