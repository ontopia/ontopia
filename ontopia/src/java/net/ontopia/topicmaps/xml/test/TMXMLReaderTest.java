
// $Id: TMXMLReaderTest.java,v 1.2 2008/05/21 13:40:15 geir.gronmo Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import java.util.*;

import org.xml.sax.SAXParseException;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;

public class TMXMLReaderTest extends AbstractXMLTestCase {

  public TMXMLReaderTest(String name) {
    super(name);
  }
    
  public void setUp() {
  }

  // --- Utilities

  protected TopicMapIF readTopicMap(String filename) throws IOException {
    filename = resolveFileName("canonical" + File.separator + "extra", filename);
    TMXMLReader reader = new TMXMLReader(filename);
    return reader.read();
  }
    
  // --- Test cases

  public void testReifiedTopicMap() throws IOException {
    TopicMapIF tm = readTopicMap("reified-tm.xml");
    TopicIF reifier = tm.getReifier();
    assertTrue("Reification was not preserved", reifier != null);
  }    
}
