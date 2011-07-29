
package net.ontopia.topicmaps.xml;

import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.*;
import net.ontopia.xml.*;

import org.xml.sax.*;
import java.util.*;
import java.io.*;
import java.net.*;

import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the ExternalReferenceHandlerIF interface by counting
 * calls made to that interface and testing them against an
 * expected count for a given test file.
 * Note that the testing is currently split into two test cases.
 * This is because the testExternalTMRefs fails and if everything is
 * in one function which is renamed to _testFoo(), the framework fails as
 * there are no tests it can access.
 */
public class ExternalRefTest
{

  private final static String testdataDirectory = "various";

  protected CountingRefHandler readTestFile(String fileName) {
    CountingRefHandler extRefHandler = new CountingRefHandler();
    try {

      String testfile = TestFileUtils.getTestInputFile(testdataDirectory, fileName);
      XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(testfile));
      reader.setExternalReferenceHandler(extRefHandler);
      reader.read();
    } catch (MalformedURLException ex) {
      Assert.fail("MalformedURLException initialising base address of test file.");
    } catch (IOException ex) {
      Assert.fail("IOException parsing test file." + ex.toString());
    }

    return extRefHandler;
  }

  @Test
  public void testExternalRefs() {
    int expectTopics = 7;
    CountingRefHandler extRefHandler = readTestFile("external-ref.xtm");
    Assert.assertTrue("Not all external topics reported. Expected " + String.valueOf(expectTopics) + ", got " 
               + extRefHandler.getTopicRefs().size(),
               extRefHandler.getTopicRefs().size() == expectTopics);
  }

  @Test
  public void testExternalTMRefs() {
    int expectMaps   = 1;
    CountingRefHandler extRefHandler = readTestFile("external-tm.xtm");
    Assert.assertTrue("Not all external topic maps reported. Expected: " + String.valueOf(expectMaps) + ", got "
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
