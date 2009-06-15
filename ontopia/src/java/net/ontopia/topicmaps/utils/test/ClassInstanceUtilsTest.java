
// $Id: ClassInstanceUtilsTest.java,v 1.3 2008/06/13 08:36:29 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.test;

import java.util.*;
import java.io.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;
import net.ontopia.topicmaps.utils.ClassInstanceUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;

public class ClassInstanceUtilsTest extends AbstractTopicMapTestCase {
  protected TopicMapIF        topicmap; 
  protected LocatorIF         base;

  public ClassInstanceUtilsTest(String name) {
    super(name);
  }

  protected void load(String dir, String filename) throws IOException {
    File file = new File(resolveFileName(dir, filename));
    topicmap = ImportExportUtils.getReader(file).read();
    base = topicmap.getStore().getBaseAddress();
  }

  protected TopicIF getTopicById(String id) {
    return (TopicIF) topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#" + id));
  }
  
  // --- Test cases

  public void testGetInstancesOfEmpty() throws IOException {
    load("various", "small-test.ltm");
    Collection instances = ClassInstanceUtils.getInstancesOf(Collections.EMPTY_SET,
                                                             getTopicById("person"));
    assertTrue("found instances in empty set", instances.isEmpty());
  }

  public void testGetInstancesOfTopic() throws IOException {
    load("various", "small-test.ltm");
    Collection instances = ClassInstanceUtils.getInstancesOf(topicmap.getTopics(),
                                                             getTopicById("person"));
    assertTrue("wrong number of instances", instances.size() == 3);
    assertTrue("instances does not contain 'lmg'",
               instances.contains(getTopicById("lmg")));
    assertTrue("instances does not contain 'gra'",
               instances.contains(getTopicById("gra")));
    assertTrue("instances does not contain 'grove'",
               instances.contains(getTopicById("grove")));
  }
}
