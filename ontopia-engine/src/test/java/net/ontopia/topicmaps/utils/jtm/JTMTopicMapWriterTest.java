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
package net.ontopia.topicmaps.utils.jtm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JTMTopicMapWriterTest {

  private TopicMapBuilderIF builder;
  private TopicMapIF tm;
  
  @Before
  public void setUp() {
    tm = new InMemoryTopicMapStore().getTopicMap();
    builder = tm.getBuilder();
  }

  /**
   * TODO: rewrite tests, this is just a proof-of-concept test.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testImportExport() throws IOException {
    String json = "{\"version\":\"1.0\", \"item_type\":\"topicmap\", " +
    "\"topics\":[ {\"subject_identifiers\":[\"http://psi.topincs.com/movies/dear-wendy\"], \"names\":[ {\"value\":\"Dear Wendy\", \"type\":\"si:http://psi.topincs.com/title\", \"scope\":[ \"si:http://www.topicmaps.org/xtm/1.0/country.xtm#US\", \"si:http://www.topicmaps.org/xtm/1.0/country.xtm#DE\"]}], \"occurrences\":[ {\"value\":\"2005\", \"type\":\"si:http://psi.topincs.com/publication-year\", \"datatype\":\"http://www.w3.org/2001/XMLSchema#gYear\"}]}], \"associations\":[ {\"type\":\"si:http://psi.topicmaps.org/iso13250/model/type-instance\", \"roles\":[ {\"player\":\"si:http://psi.topincs.com/movies/dear-wendy\", \"type\":\"si:http://psi.topicmaps.org/iso13250/model/instance\"}, {\"player\":\"si:http://psi.topincs.com/movie\", \"type\":\"si:http://psi.topicmaps.org/iso13250/model/type\"}]}]}";

    Reader r = new StringReader(json);

    LocatorIF base = URILocator.create("http://www.test.org");
    JTMTopicMapReader reader = new JTMTopicMapReader(r, base);
    TopicMapIF tm = reader.read();

    TopicIF wendy = tm.getTopicBySubjectIdentifier(URILocator.create("http://psi.topincs.com/movies/dear-wendy"));
    TopicIF movie = tm.getTopicBySubjectIdentifier(URILocator.create("http://psi.topincs.com/movie"));
    
    Collection<TopicIF> types = wendy.getTypes();
    Assert.assertEquals(1, types.size());
    Assert.assertEquals(movie, types.iterator().next());

    //JTMTopicMapWriter writer = new JTMTopicMapWriter(System.out);
    // LocatorIF base = tm.getStore().getBaseAddress();
    // TMObjectIF obj =
    // tm.getObjectByItemIdentifier(base.resolveAbsolute("#mother"));
    //writer.write((TopicNameIF) wendy.getTopicNames().iterator().next());
  }

  @Test
  public void testWriteToFile() throws IOException {
    builder.makeTopic();
    File file = TestFileUtils.getTestOutputFile("jtm", "io-f.jtm");
    new JTMTopicMapWriter(file).write(tm);
    Assert.assertTrue(Files.size(file.toPath()) > 0);
  }

  @Test
  public void testWriteToOutputStream() throws IOException {
    builder.makeTopic();
    File file = TestFileUtils.getTestOutputFile("jtm", "io-o.jtm");
    new JTMTopicMapWriter(new FileOutputStream(file)).write(tm);
    Assert.assertTrue(Files.size(file.toPath()) > 0);
  }

  @Test
  public void testWriteToWriter() throws IOException {
    builder.makeTopic();
    File file = TestFileUtils.getTestOutputFile("jtm", "io-w.jtm");
    new JTMTopicMapWriter(new FileWriter(file)).write(tm);
    Assert.assertTrue(Files.size(file.toPath()) > 0);
  }
}
