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
import jakarta.servlet.ServletException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestGetTopic extends TestTMRAPOperation {

  /**
   * ERROR: The name of the syntax is misspelled.
   */
  @Test
  public void testErrorWrongSyntax() 
      throws ServletException, IOException {
    // Check that the topic italy exists.
    
    StringWriter stringWriter = new StringWriter();
    
    // ERROR: The name of the syntax is misspelled.
    int code =
      doGet(uriPrefix + "get-topic", "topicmap=i18n.ltm&syntax=GObBlediGoOk" +
        "&identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
        paramsTable, rapServlet, stringWriter);
    Assert.assertTrue("TMRAP servlet accepted bad syntax", code == 400);
  }
  
  /**
   * The subject indicator is misspelled.
   */
  @Test
  public void testWrongSubjectIndicator() 
      throws ServletException, IOException {
    // The subject indicator is misspelled (XXX in topicXXXmaps).
    StringWriter stringWriter = new StringWriter();
    doGet(uriPrefix + "get-topic", "topicmap=i18n.ltm&syntax=application/x-xtm" +
        "&identifier=http://www.topicXXXmaps.org/xtm/1.0/country.xtm%23IT",
          paramsTable, rapServlet, stringWriter, 200);
    String italyFragment = stringWriter.toString();
    
    // No matches found should give output of length 23
    Assert.assertEquals(23, canonicalizeXTM(italyFragment).length());
  }
  
  /**
   * ERROR: The name of the topic map is misspelled.
   */
  @Test
  public void testErrorWrongTopicMap()
      throws ServletException, IOException {
    String uriPrefix = "http://localhost:8080/omnigator/plugins/viz/";
    RAPServlet rapServlet = new RAPServlet();
    setupRAPServlet(rapServlet, uriPrefix);

    // Check that the topic italy exists.
    
    StringWriter stringWriter = new StringWriter();
    
    // ERROR: The name of the topic map is misspelled.
    int code = doGet(uriPrefix + "get-topic", "topicmap=i8n.ltm&syntax=XTM" +
        "&identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
        paramsTable, rapServlet, stringWriter);
    Assert.assertTrue("TMRAP servlet accepted bad topic map ID", code == 400);
  }

  /**
   * Get two topics from the topic map i18n.
   */
  @Test
  public void testTwoTopics() throws ServletException, IOException {
    // Check that the topics italy and finland exist.
    File outfile = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "get-topic-two-topics.xtm");
    Writer out = new FileWriter(outfile);
    doGet(uriPrefix + "get-topic", "topicmap=i18n.ltm" +
        "&identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT" +
        "&identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23FI",
          paramsTable, rapServlet, out, 200);
    out.close();

    // Verify response
    verifyCanonical("get-topic-two-topics.xtm");
  }

  /**
   * Specify XTM explicitly.
   */
  @Test
  public void testXTM() throws ServletException, IOException {
    File outfile = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "get-topic-two-topics.xtm");
    Writer out = new FileWriter(outfile);
    doGet(uriPrefix + "get-topic", "topicmap=i18n.ltm&syntax=application/x-xtm" +
        "&identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT" +
        "&identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23FI",
          paramsTable, rapServlet, out, 200);
    out.close();

    // Verify response
    verifyCanonical("get-topic-two-topics.xtm");
  }
  
  /**
   * Get one topic from two different topic maps.
   */
  @Test
  public void testTopicsFromTwoMaps() throws ServletException, IOException {
    // Check that the topics italy and finland exist.
    File outfile = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "get-topic-two-maps.xtm");
    Writer out = new FileWriter(outfile);
    doGet(uriPrefix + "get-topic", "topicmap=i18n.ltm&topicmap=opera.xtm&" +
          "identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
          paramsTable, rapServlet, out, 200);
    out.close();

    // Verify response
    verifyCanonical("get-topic-two-maps.xtm");
  }
}
