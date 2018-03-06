/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Map;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TMXMLWriterTest {
  private TopicMapIF topicmap;
  private TopicMapBuilderIF builder;
  
  private final static String testdataDirectory = "tmxmlWriter";

  @Before
  public void setUp() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    topicmap = store.getTopicMap();
    builder = topicmap.getBuilder();
  }
 
  // --- Test cases

  @Test
  public void testPrefixOrdering() throws IOException {
    // bug #1933: the namespace prefixes created for some namespaces
    // take the form of 'preXX', and these are not predictable, as
    // they depend on the order the topics are processed in. this test
    // verifies that we are rid of this problem.

    // iso and xtm are already taken, so it can't be used...
    TopicIF type1 = builder.makeTopic();
    type1.addSubjectIdentifier(URILocator.create("http://iso/foo"));
    TopicIF type2 = builder.makeTopic();
    type2.addSubjectIdentifier(URILocator.create("http://xtm/bar"));

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
    Assert.assertEquals("namespace prefixes not consistent,",
                 nsuris.get("http://iso/"), isopre); 
    Assert.assertEquals("namespace prefixes not consistent",
                 nsuris.get("http://xtm/"), xtmpre);
  }

  @Test
  public void testWriterClosing() throws IOException {
    // make sure the writer does not close writer objects passed in to it
    MockWriter mock = new MockWriter();
    TMXMLWriter writer = new TMXMLWriter(mock);
    writer.close();
    Assert.assertFalse("Exporter closes external writer object", mock.isClosed());
  }

  @Test
  public void testWriterClosing2() throws IOException {
    // make sure the writer does not close writer objects passed in to it
    MockWriter mock = new MockWriter();
    TMXMLWriter writer = new TMXMLWriter(mock, "iso-8859-1");
    writer.close();
    Assert.assertFalse("Exporter closes external writer object", mock.isClosed());
  }

  @Test
  public void testFileClosing() throws IOException, SAXException {
    // make sure the writer closes streams it creates
    String file = getAbsoluteFilename("closing.tmx");
    TMXMLWriter writer = new TMXMLWriter(new File(file));
    writer.setDocumentElement("test");
    writer.startTopicMap(topicmap);
    writer.endTopicMap();
    writer.close();

    File f = new File(file);
    Assert.assertTrue("Close method does not close stream",
               f.length() > 0);
  }

  @Test
  public void testFileClosing2() throws IOException, SAXException {
    // make sure the writer closes streams it creates
    String file = getAbsoluteFilename("closing.tmx");
    TMXMLWriter writer = new TMXMLWriter(new File(file), "iso-8859-1");
    writer.setDocumentElement("test");
    writer.startTopicMap(topicmap);
    writer.endTopicMap();
    writer.close();

    File f = new File(file);
    Assert.assertTrue("Close method does not close stream",
               f.length() > 0);
  }

  @Test
  public void testFileClosing3() throws IOException, SAXException {
    // make sure the writer closes streams it creates
    String file = getAbsoluteFilename("closing.tmx");
    File f = new File(file);
    TMXMLWriter writer = new TMXMLWriter(f);
    writer.setDocumentElement("test");
    writer.startTopicMap(topicmap);
    writer.endTopicMap();
    writer.close();

    Assert.assertTrue("Close method does not close stream",
               f.length() > 0);
  }

  @Test
  public void testBug2116() throws IOException, SAXException {
    MockWriter mock = new MockWriter();
    TMXMLWriter writer = new TMXMLWriter(mock, "utf-8");
    writer.write(topicmap);
    // if bug 2116 occurs we'll get an NPE on the previous line and never
    // get here.
    
    Assert.assertTrue(true); // for PMD
  }
  
  @Test
  public void testWriteToFile() throws IOException {
    builder.makeTopic();
    File file = TestFileUtils.getTestOutputFile("tmxml", "io-f.xtm");
    new TMXMLWriter(file).write(topicmap);
    Assert.assertTrue(Files.size(file.toPath()) > 0);
  }

  @Test
  public void testWriteToOutputStream() throws IOException {
    builder.makeTopic();
    File file = TestFileUtils.getTestOutputFile("tmxml", "io-o.xtm");
    new TMXMLWriter(new FileOutputStream(file), "utf-8").write(topicmap);
    Assert.assertTrue(Files.size(file.toPath()) > 0);
  }

  @Test
  public void testWriteToWriter() throws IOException {
    builder.makeTopic();
    File file = TestFileUtils.getTestOutputFile("tmxml", "io-w.xtm");
    new TMXMLWriter(new FileWriter(file), "utf-8").write(topicmap);
    Assert.assertTrue(Files.size(file.toPath()) > 0);
  }  
  
  // --- Helpers

  private String getAbsoluteFilename(String file) {
    String root = TestFileUtils.getTestdataOutputDirectory();
    TestFileUtils.verifyDirectory(root, testdataDirectory, "out");
    String base = root + File.separator + testdataDirectory + File.separator;
    return base + "out" + File.separator + file;
  }
  
  // --- Mock writer

  class MockWriter extends Writer {
    private boolean closed;

    public MockWriter() {
      closed = false;
    }

    @Override
    public void close() throws IOException {
      closed = true;
    }

    public boolean isClosed() {
      return closed;
    }
    
    @Override
    public void write(char[] attr0, int attr1, int attr2) throws IOException {
      // no-op
    }
    @Override
    public void flush() throws IOException {
      // no-op
    }
  }
}
