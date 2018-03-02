/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.utils.tmrap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;
import javax.servlet.ServletException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestAddFragment extends TestTMRAPOperation {

  /**
   * Missing fragment should cause error.
   */
  @Test
  public void testNoFragment() throws ServletException, IOException {
    Writer out = new StringWriter();
    Hashtable tempTable = new Hashtable();
    tempTable.put(RAPServlet.TOPICMAP_PARAMETER_NAME, "i18n.ltm");
    tempTable.put(RAPServlet.SYNTAX_PARAMETER_NAME, RAPServlet.SYNTAX_LTM);
    int code = doPost(uriPrefix + "add-fragment", tempTable, rapServlet, out);
    Assert.assertTrue("Request with missing fragment was accepted: " + code,
               code == 400);
  }
  
  /**
   * Incorrect syntax value should be rejected.
   */
  @Test
  public void testBadSyntax() throws ServletException, IOException {
    Writer out = new StringWriter();
    String fragment = " [denmark : country = \"Denmark\" "+
      "@\"http://www.topicmaps.org/xtm/1.0/country.xtm#DK\"] ";
    Hashtable tempTable = new Hashtable(paramsTable);
    tempTable.put(RAPServlet.TOPICMAP_PARAMETER_NAME, "i18n.ltm");
    tempTable.put(RAPServlet.SYNTAX_PARAMETER_NAME, "foo");
    tempTable.put(RAPServlet.FRAGMENT_PARAMETER_NAME, fragment);
    int code = doPost(uriPrefix + "add-fragment", tempTable, rapServlet, out);
    Assert.assertTrue("Bad syntax value was accepted by TMRAP servlet: " + code,
               code == 400);
  }

  /**
   * Incorrect topic map ID should be rejected.
   */
  @Test
  public void testBadTMID() throws ServletException, IOException {
    Writer out = new StringWriter();
    String fragment = " [denmark : country = \"Denmark\" "+
      "@\"http://www.topicmaps.org/xtm/1.0/country.xtm#DK\"] ";
    Hashtable tempTable = new Hashtable(paramsTable);
    tempTable.put(RAPServlet.TOPICMAP_PARAMETER_NAME, "i81n.ltm");
    tempTable.put(RAPServlet.SYNTAX_PARAMETER_NAME, RAPServlet.SYNTAX_LTM);
    tempTable.put(RAPServlet.FRAGMENT_PARAMETER_NAME, fragment);
    int code = doPost(uriPrefix + "add-fragment", tempTable, rapServlet, out);
    Assert.assertTrue("Bad topic map ID was accepted by TMRAP servlet: " + code,
               code == 400);
  }

  /**
   * Syntax errors should be rejected.
   */
  @Test
  public void testBadFragment() throws ServletException, IOException {
    Writer out = new StringWriter();
    String fragment = " [denmark ";
    Hashtable tempTable = new Hashtable(paramsTable);
    tempTable.put(RAPServlet.TOPICMAP_PARAMETER_NAME, "i18n.ltm");
    tempTable.put(RAPServlet.SYNTAX_PARAMETER_NAME, RAPServlet.SYNTAX_LTM);
    tempTable.put(RAPServlet.FRAGMENT_PARAMETER_NAME, fragment);
    int code = doPost(uriPrefix + "add-fragment", tempTable, rapServlet, out);
    Assert.assertTrue("Bad LTM was accepted by TMRAP servlet: " + code,
               code == 400);
  }  
  
  /**
   * A normal add fragment request that should succeed.
   */
  @Test
  public void testAddTopicLTM() throws ServletException, IOException {
    // Add the topic denmark into the topic map.
    Writer out = new StringWriter();
    String fragment = " [denmark : country = \"Denmark\" "+
      "@\"http://www.topicmaps.org/xtm/1.0/country.xtm#DK\"] ";
    Hashtable tempTable = new Hashtable(paramsTable);
    tempTable.put(RAPServlet.TOPICMAP_PARAMETER_NAME, "i18n.ltm");
    tempTable.put(RAPServlet.SYNTAX_PARAMETER_NAME, RAPServlet.SYNTAX_LTM);
    tempTable.put(RAPServlet.FRAGMENT_PARAMETER_NAME, fragment);
    doPost(uriPrefix + "add-fragment", tempTable, rapServlet, out);
    
    // Verify that denmark was added correctly
    File outfile = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "add-fragment-denmark.xtm");    
    out = new FileWriter(outfile);
    doGet(uriPrefix + "get-topic",
          "topicmap=i18n.ltm&syntax=application/x-xtm&identifier" +
          "=http://www.topicmaps.org/xtm/1.0/country.xtm%23DK",
          paramsTable, rapServlet, out, 200);
    out.close();
    verifyCanonical("add-fragment-denmark.xtm");
  }
}
