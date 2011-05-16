
// $Id: AbstractXMLTestCase.java,v 1.11 2003/03/28 15:44:04 larsga Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.utils.FileUtils;
import org.junit.Before;
import net.ontopia.utils.URIUtils;

public abstract class AbstractXMLTestCase {
  protected TopicMapBuilderIF builder;
  protected TopicMapIF topicmap;
  protected LocatorIF sourceLoc;
  protected LocatorIF tmbase;
  protected File tmfile;
  protected int version; // which XTM version to output

  @Before
  public void setUp() throws Exception {
    String root = FileUtils.getTestdataOutputDirectory();
    FileUtils.verifyDirectory(root, "canonical", "out");
  }

  protected void prepareTopicMap() throws IOException {
    tmfile = FileUtils.getTestOutputFile("canonical", "out", "tmid.xtm");
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
