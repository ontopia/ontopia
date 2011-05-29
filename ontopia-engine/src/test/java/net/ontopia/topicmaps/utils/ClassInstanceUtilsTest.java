
package net.ontopia.topicmaps.utils;

import java.util.*;
import java.io.*;
import junit.framework.TestCase;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.FileUtils;

public class ClassInstanceUtilsTest extends TestCase {
  protected TopicMapIF        topicmap; 
  protected LocatorIF         base;

  private final static String testdataDirectory = "various";

  public ClassInstanceUtilsTest(String name) {
    super(name);
  }

  protected void load(String dir, String filename) throws IOException {
    filename = FileUtils.getTestInputFile(dir, filename);
    topicmap = ImportExportUtils.getReader(filename).read();
    base = topicmap.getStore().getBaseAddress();
  }

  protected TopicIF getTopicById(String id) {
    return (TopicIF) topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#" + id));
  }
  
  // --- Test cases

  public void testGetInstancesOfEmpty() throws IOException {
    load(testdataDirectory, "small-test.ltm");
    Collection instances = ClassInstanceUtils.getInstancesOf(Collections.EMPTY_SET,
                                                             getTopicById("person"));
    assertTrue("found instances in empty set", instances.isEmpty());
  }

  public void testGetInstancesOfTopic() throws IOException {
    load(testdataDirectory, "small-test.ltm");
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
