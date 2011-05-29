
package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;

import org.junit.Assert;
import org.junit.Test;

/**
 * INTERNAL. The purpose of this test class is basically to verify
 * that all entry points in the API work as advertised.
 */
public class CanonicalXTMWriterTest extends AbstractXMLTestCase {
  private TopicMapIF topicmap;

  private final static String testdataDirectory = "cxtm";

  public void setUp() throws IOException {
    topicmap = makeEmptyTopicMap();
    String root = FileUtils.getTestdataOutputDirectory();
    FileUtils.verifyDirectory(root, testdataDirectory, "out");
  }
  
  // --- Test cases

  @Test
  public void testOutputStream() throws IOException {
    String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline", "outputstream.cxtm");
    File out = FileUtils.getTestOutputFile(testdataDirectory, "out", "outputstream.cxtm");

    FileOutputStream outs = new FileOutputStream(out);
    new CanonicalXTMWriter(outs).write(topicmap);
    outs.close();

    Assert.assertTrue("OutputStream export gives incorrect output",
               FileUtils.compareFileToResource(out, baseline));
  }

  @Test
  public void testWriter() throws IOException {
    String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline", "writer.cxtm");
    File out = FileUtils.getTestOutputFile(testdataDirectory, "out", "writer.cxtm");

    Writer outw = new OutputStreamWriter(new FileOutputStream(out), "utf-8");
    new CanonicalXTMWriter(outw).write(topicmap);
    outw.close();

    Assert.assertTrue("OutputStream export gives incorrect output",
               FileUtils.compareFileToResource(out, baseline));
  }

  // --- Utilities

  private TopicMapIF makeEmptyTopicMap() throws IOException {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    store.setBaseAddress(new URILocator("http://www.ontopia.net"));
    return store.getTopicMap();
  }
}
