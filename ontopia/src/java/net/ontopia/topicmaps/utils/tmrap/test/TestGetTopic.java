
// $Id: TestGetTopic.java,v 1.5 2007/06/18 09:04:34 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.tmrap.test;

import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;

import net.ontopia.utils.FileUtils;
import net.ontopia.topicmaps.utils.tmrap.RAPServlet;

public class TestGetTopic extends TestTMRAPOperation {

  public TestGetTopic() {
    super("TestGetTopic");
  }
  
  /**
   * ERROR: The name of the syntax is misspelled.
   */
  public void testErrorWrongSyntax() 
      throws ServletException, IOException {
    // Check that the topic italy exists.
    
    StringWriter stringWriter = new StringWriter();
    
    // ERROR: The name of the syntax is misspelled.
    int code =
      doGet(uriPrefix + "get-topic", "topicmap=i18n.ltm&syntax=GObBlediGoOk" +
        "&identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
        paramsTable, rapServlet, stringWriter);
    assertTrue("TMRAP servlet accepted bad syntax", code == 400);
  }
  
  /**
   * The subject indicator is misspelled.
   */
  public void testWrongSubjectIndicator() 
      throws ServletException, IOException {
    // The subject indicator is misspelled (XXX in topicXXXmaps).
    StringWriter stringWriter = new StringWriter();
    doGet(uriPrefix + "get-topic", "topicmap=i18n.ltm&syntax=application/x-xtm" +
        "&identifier=http://www.topicXXXmaps.org/xtm/1.0/country.xtm%23IT",
          paramsTable, rapServlet, stringWriter, 200);
    String italyFragment = stringWriter.toString();
    
    // No matches found should give output of length 23
    assertEquals(23, canonicalizeXTM(italyFragment).length());
  }
  
  /**
   * ERROR: The name of the topic map is misspelled.
   */
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
    assertTrue("TMRAP servlet accepted bad topic map ID", code == 400);
  }

  /**
   * Get two topics from the topic map i18n.
   */
  public void testTwoTopics() throws ServletException, IOException {
    // Check that the topics italy and finland exist.
    verifyDirectory(getBaseDir(), "out");    
    Writer out = new FileWriter(getOutDir() + "get-topic-two-topics.xtm");
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
  public void testXTM() throws ServletException, IOException {
    verifyDirectory(getBaseDir(), "out");    
    Writer out = new FileWriter(getOutDir() + "get-topic-two-topics.xtm");
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
  public void testTopicsFromTwoMaps() throws ServletException, IOException {
    // Check that the topics italy and finland exist.
    verifyDirectory(getBaseDir(), "out");    
    Writer out = new FileWriter(getOutDir() + "get-topic-two-maps.xtm");
    doGet(uriPrefix + "get-topic", "topicmap=i18n.ltm&topicmap=opera.xtm&" +
          "identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
          paramsTable, rapServlet, out, 200);
    out.close();

    // Verify response
    verifyCanonical("get-topic-two-maps.xtm");
  }
}
