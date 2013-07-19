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
