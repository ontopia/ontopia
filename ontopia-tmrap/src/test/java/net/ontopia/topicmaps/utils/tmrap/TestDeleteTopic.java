
// $Id: TestDeleteTopic.java,v 1.4 2006/05/05 14:22:17 opland Exp $

package net.ontopia.topicmaps.utils.tmrap;

import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.ServletException;

import org.junit.Test;
import org.junit.Assert;

public class TestDeleteTopic extends TestTMRAPOperation {

  // FIXME: improve tests to access TMs directly
  // FIXME: test what happens if non-existent topic is deleted
  // FIXME: test what happens if no parameters given at all
  // FIXME: test what happens if non-existent TM is given
  
  /**
   * Check that the topic italy exists (by getting it).
   * Delete the topic italy from the i18n topic map.
   * Check that the topic no longer exists
   */
  @Test
  public void testDelete() throws ServletException, IOException {
    // The topic "italy" already exists; we just assume this without
    // doing any further testing.
    StringWriter stringWriter = new StringWriter();
    
    // Delete the topic italy
    doPost(uriPrefix + "delete-topic",
        "topicmap=i18n.ltm&identifier" +
            "=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
        paramsTable, rapServlet, stringWriter);
    
    // Check that the topic italy no longer exists
    stringWriter = new StringWriter();
    doGet(uriPrefix + "get-topic",
        "topicmap=i18n.ltm&identifier" +
            "=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
          paramsTable, rapServlet, stringWriter, 200);
    Assert.assertEquals(23, canonicalizeXTM(stringWriter.toString()).length());
  }
  
  /**
   * Get one topic from two different topic maps.
   */
  @Test
  public void testTopicsFromTwoMaps() throws ServletException, IOException {
    // We assume, without proof, that "italy" exists in both TMs
    StringWriter stringWriter = new StringWriter();
    
    // Delete the topic italy from both topic maps
    doPost(uriPrefix + "delete-topic",
        "topicmap=i18n.ltm&topicmap=opera.xtm&identifier" +
            "=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
        paramsTable, rapServlet, stringWriter);
    
    stringWriter = new StringWriter();
    doGet(uriPrefix + "get-topic", "topicmap=i18n.ltm&topicmap=opera.xtm&" +
        "identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
          paramsTable, rapServlet, stringWriter, 200);
    String fragment = stringWriter.toString();
    Assert.assertEquals(23, canonicalizeXTM(fragment).length());
  }
}
