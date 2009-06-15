
// $Id: ExternalRefTest.java,v 1.11 2003/03/28 15:45:15 larsga Exp $

package net.ontopia.topicmaps.xml.test;

import net.ontopia.test.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.*;
import net.ontopia.xml.*;

import org.xml.sax.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Tests the ExternalReferenceHandlerIF interface by counting
 * calls made to that interface and testing them against an
 * expected count for a given test file.
 * Note that the testing is currently split into two test cases.
 * This is because the testExternalTMRefs fails and if everything is
 * in one function which is renamed to _testFoo(), the framework fails as
 * there are no tests it can access.
 */
public class ExternalRefTest extends AbstractOntopiaTestCase
{

  public ExternalRefTest(String name) {
    super(name);
  }

  protected CountingRefHandler readTestFile(String fileName) {
    CountingRefHandler extRefHandler = new CountingRefHandler();
    try {

      String testfile = "file:" + AbstractOntopiaTestCase.getTestDirectory() + File.separator +
        "various" + File.separator+ fileName;
      XTMTopicMapReader reader = new XTMTopicMapReader(testfile);
      reader.setExternalReferenceHandler(extRefHandler);
      reader.read();
    } catch (MalformedURLException ex) {
      fail("MalformedURLException initialising base address of test file.");
    } catch (IOException ex) {
      fail("IOException parsing test file." + ex.toString());
    }

    return extRefHandler;
  }

  public void testExternalRefs() {
    int expectTopics = 7;
    CountingRefHandler extRefHandler = readTestFile("external-ref.xtm");
    assertTrue("Not all external topics reported. Expected " + String.valueOf(expectTopics) + ", got " 
               + extRefHandler.getTopicRefs().size(),
               extRefHandler.getTopicRefs().size() == expectTopics);
  }

  public void testExternalTMRefs() {
    int expectMaps   = 1;
    CountingRefHandler extRefHandler = readTestFile("external-tm.xtm");
    assertTrue("Not all external topic maps reported. Expected: " + String.valueOf(expectMaps) + ", got "
               + extRefHandler.getTMRefs().size(),
               extRefHandler.getTMRefs().size() == expectMaps);
  }
  
}

class CountingRefHandler implements ExternalReferenceHandlerIF
{
  protected ArrayList tmrefs;
  protected ArrayList topicrefs;

  public CountingRefHandler() {
    tmrefs = new ArrayList();
    topicrefs = new ArrayList();
  }

  public Collection getTMRefs() { return tmrefs; }
  public Collection getTopicRefs() { return topicrefs; }

  public LocatorIF externalTopicMap(LocatorIF address) {
    tmrefs.add(address);
    return null;
  }

  public LocatorIF externalTopic(LocatorIF address) {
    topicrefs.add(address);
    return null;
  }

}
