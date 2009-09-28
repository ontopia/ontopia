
// $Id: AbstractXMLTestCase.java,v 1.11 2003/03/28 15:44:04 larsga Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;

public abstract class AbstractXMLTestCase extends AbstractTopicMapTestCase {
  protected TopicMapBuilderIF builder;
  protected TopicMapIF topicmap;
  protected LocatorIF sourceLoc;
  protected LocatorIF tmbase;
  protected File tmfile;
  protected int version; // which XTM version to output

  public AbstractXMLTestCase(String name) {
    super(name);
  }
    
  protected void setUp() throws Exception {
    String root = getTestDirectory();
    verifyDirectory(root, "canonical", "out");
  }

  protected void prepareTopicMap() throws IOException {
    tmfile = new File(resolveFileName("canonical" + File.separator + "out", "tmid.xtm"));
    tmbase = new URILocator(tmfile.toURL());
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
}
