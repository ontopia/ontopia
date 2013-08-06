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
import java.io.IOException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.utils.TestFileUtils;
import org.junit.Before;

public abstract class AbstractXMLTestCase {
  protected TopicMapBuilderIF builder;
  protected TopicMapIF topicmap;
  protected LocatorIF sourceLoc;
  protected LocatorIF tmbase;
  protected File tmfile;
  protected int version; // which XTM version to output

  @Before
  public void setUp() throws Exception {
    String root = TestFileUtils.getTestdataOutputDirectory();
    TestFileUtils.verifyDirectory(root, "canonical", "out");
  }

  protected void prepareTopicMap() throws IOException {
    tmfile = TestFileUtils.getTestOutputFile("canonical", "out", "tmid.xtm");
    tmbase = new URILocator(tmfile);
    sourceLoc = tmbase.resolveAbsolute("#id");
    
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    store.setBaseAddress(tmbase);
    topicmap = store.getTopicMap();
    builder = topicmap.getBuilder();
  }

  protected void export() throws IOException {
    XTMTopicMapWriter writer = new XTMTopicMapWriter(tmfile);
    writer.setVersion(version);
    writer.setAddIds(true);
    writer.write(topicmap);
  }

  public TopicIF getTopicById(TopicMapIF topicmap, String id) {
    LocatorIF base = topicmap.getStore().getBaseAddress();
    return (TopicIF)
      topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }
  
  protected TMObjectIF getObjectById(TopicMapIF topicmap, String id) {
    LocatorIF base = topicmap.getStore().getBaseAddress();
    return topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }

  public TopicIF getTopicById(TopicMapIF topicmap, LocatorIF base, String id) {
    return (TopicIF)
      topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }

  public TMObjectIF getObjectById(TopicMapIF topicmap, LocatorIF base, String id) {
    return topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }

}
