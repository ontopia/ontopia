
// $Id: TMXMLReaderTest.java,v 1.2 2008/05/21 13:40:15 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.Assert;
import org.junit.Test;

public class TMXMLReaderTest extends AbstractXMLTestCase {

  private final static String testdataDirectory = "canonical";

  public void setUp() {
  }

  // --- Utilities

  protected TopicMapIF readTopicMap(String filename) throws IOException {
    filename = FileUtils.getTestInputFile(testdataDirectory, "extra", filename);
    TMXMLReader reader = new TMXMLReader(URIUtils.getURI(filename));
    return reader.read();
  }
    
  // --- Test cases

  @Test
  public void testReifiedTopicMap() throws IOException {
    TopicMapIF tm = readTopicMap("reified-tm.xml");
    TopicIF reifier = tm.getReifier();
    Assert.assertTrue("Reification was not preserved", reifier != null);
  }    
}
