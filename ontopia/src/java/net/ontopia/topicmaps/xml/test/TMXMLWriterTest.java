
// $Id: TMXMLWriterTest.java,v 1.10 2008/06/13 08:17:58 geir.gronmo Exp $

package net.ontopia.topicmaps.xml.test;

import java.util.Map;
import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.StringWriter;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.TMXMLWriter;
import net.ontopia.topicmaps.test.*;
import org.xml.sax.SAXException;

public class TMXMLWriterTest extends AbstractTopicMapTestCase {
  private TopicMapIF topicmap;
  private TopicMapBuilderIF builder;
  
  public TMXMLWriterTest(String name) {
    super(name);
  }
    
  public void setUp() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    topicmap = store.getTopicMap();
    builder = topicmap.getBuilder();
  }
 
  // --- Test cases

  public void testPrefixOrdering() throws IOException {
    // bug #1933: the namespace prefixes created for some namespaces
    // take the form of 'preXX', and these are not predictable, as
    // they depend on the order the topics are processed in. this test
    // verifies that we are rid of this problem.

    // iso and xtm are already taken, so it can't be used...
    TopicIF type1 = builder.makeTopic();
    type1.addSubjectIdentifier(new URILocator("http://iso/foo"));
    TopicIF type2 = builder.makeTopic();
    type2.addSubjectIdentifier(new URILocator("http://xtm/bar"));

    TopicIF topic1 = builder.makeTopic(type1);
    TopicIF topic2 = builder.makeTopic(type2);

    // trying one order
    TMXMLWriter writer = new TMXMLWriter(new StringWriter(), "utf-8");
    writer.gatherPrefixes(topic1);
    writer.gatherPrefixes(topic2);

    Map nsuris = writer.getNamespaceURIMapping();
    String isopre = (String) nsuris.get("http://iso/");
    String xtmpre = (String) nsuris.get("http://xtm/");

    // then trying another
    writer = new TMXMLWriter(new StringWriter(), "utf-8");
    writer.gatherPrefixes(topic2);
    writer.gatherPrefixes(topic1);

    nsuris = writer.getNamespaceURIMapping();
    assertEquals("namespace prefixes not consistent,",
                 nsuris.get("http://iso/"), isopre); 
    assertEquals("namespace prefixes not consistent",
                 nsuris.get("http://xtm/"), xtmpre);
  }

  public void testWriterClosing() throws IOException {
    // make sure the writer does not close writer objects passed in to it
    MockWriter mock = new MockWriter();
    TMXMLWriter writer = new TMXMLWriter(mock);
    writer.close();
    assertFalse("Exporter closes external writer object", mock.isClosed());
  }

  public void testWriterClosing2() throws IOException {
    // make sure the writer does not close writer objects passed in to it
    MockWriter mock = new MockWriter();
    TMXMLWriter writer = new TMXMLWriter(mock, "iso-8859-1");
    writer.close();
    assertFalse("Exporter closes external writer object", mock.isClosed());
  }

  public void testFileClosing() throws IOException, SAXException {
    // make sure the writer closes streams it creates
    String file = getAbsoluteFilename("closing.tmx");
    TMXMLWriter writer = new TMXMLWriter(file);
    writer.setDocumentElement("test");
    writer.startTopicMap(topicmap);
    writer.endTopicMap();
    writer.close();

    File f = new File(file);
    assertTrue("Close method does not close stream",
               f.length() > 0);
  }

  public void testFileClosing2() throws IOException, SAXException {
    // make sure the writer closes streams it creates
    String file = getAbsoluteFilename("closing.tmx");
    TMXMLWriter writer = new TMXMLWriter(file, "iso-8859-1");
    writer.setDocumentElement("test");
    writer.startTopicMap(topicmap);
    writer.endTopicMap();
    writer.close();

    File f = new File(file);
    assertTrue("Close method does not close stream",
               f.length() > 0);
  }

  public void testFileClosing3() throws IOException, SAXException {
    // make sure the writer closes streams it creates
    String file = getAbsoluteFilename("closing.tmx");
    File f = new File(file);
    TMXMLWriter writer = new TMXMLWriter(f);
    writer.setDocumentElement("test");
    writer.startTopicMap(topicmap);
    writer.endTopicMap();
    writer.close();

    assertTrue("Close method does not close stream",
               f.length() > 0);
  }

  public void testBug2116() throws IOException, SAXException {
    MockWriter mock = new MockWriter();
    TMXMLWriter writer = new TMXMLWriter(mock, "utf-8");
    writer.write(topicmap);
    // if bug 2116 occurs we'll get an NPE on the previous line and never
    // get here.
  }
  
  // --- Helpers

  private String getAbsoluteFilename(String file) {
    String root = AbstractCanonicalTestCase.getTestDirectory();
    verifyDirectory(root, "tmxmlWriter", "out");
    String base = root + File.separator + "tmxmlWriter" + File.separator;
    return base + "out" + File.separator + file;
  }
  
  // --- Mock writer

  class MockWriter extends Writer {
    private boolean closed;

    public MockWriter() {
      closed = false;
    }

    public void close() throws IOException {
      closed = true;
    }

    public boolean isClosed() {
      return closed;
    }
    
    public void write(char[] attr0, int attr1, int attr2) throws IOException {
    }
    public void flush() throws IOException {
    }
  }
}
