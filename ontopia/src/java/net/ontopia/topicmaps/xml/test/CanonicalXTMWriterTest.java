
// $Id: CanonicalXTMWriterTest.java,v 1.1 2008/05/23 11:46:12 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;

/**
 * INTERNAL. The purpose of this test class is basically to verify
 * that all entry points in the API work as advertised.
 */
public class CanonicalXTMWriterTest extends AbstractXMLTestCase {
  private TopicMapIF topicmap;

  public CanonicalXTMWriterTest(String name) {
    super(name);
  }
  
  public void setUp() throws IOException {
    topicmap = makeEmptyTopicMap();
    String root = getTestDirectory();
    verifyDirectory(root, "cxtm", "out");
  }
  
  // --- Test cases

  public void testOutputStream() throws IOException {
    String baseline = resolveFileName("cxtm", "baseline", "outputstream.cxtm");
    String out = resolveFileName("cxtm", "out", "outputstream.cxtm");

    FileOutputStream outs = new FileOutputStream(out);
    new CanonicalXTMWriter(outs).write(topicmap);
    outs.close();

    assertTrue("OutputStream export gives incorrect output",
               FileUtils.compare(out, baseline));
  }

  public void testWriter() throws IOException {
    String baseline = resolveFileName("cxtm", "baseline", "writer.cxtm");
    String out = resolveFileName("cxtm", "out", "writer.cxtm");

    Writer outw = new OutputStreamWriter(new FileOutputStream(out), "utf-8");
    new CanonicalXTMWriter(outw).write(topicmap);
    outw.close();

    assertTrue("OutputStream export gives incorrect output",
               FileUtils.compare(out, baseline));
  }

  // --- Utilities

  private TopicMapIF makeEmptyTopicMap() throws IOException {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    store.setBaseAddress(new URILocator("http://www.ontopia.net"));
    return store.getTopicMap();
  }
}
