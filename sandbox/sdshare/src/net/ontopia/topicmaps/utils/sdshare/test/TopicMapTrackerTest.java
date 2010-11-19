
package net.ontopia.topicmaps.utils.sdshare.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.events.TopicMapEvents;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.StoreFactoryReference;
import net.ontopia.topicmaps.utils.SameStoreFactory;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;
import net.ontopia.topicmaps.utils.sdshare.*;

public class TopicMapTrackerTest extends AbstractTopicMapTestCase {
  private TopicMapTracker tracker;
  private TopicMapIF topicmap;
  
  public TopicMapTrackerTest(String name) {
    super(name);
  }

  public void setUp() throws IOException {
    // load TM and register tracker
    topicmap = read("jill.xtm");
    SameStoreFactory factory = new SameStoreFactory(topicmap.getStore());
    StoreFactoryReference ref =
      new StoreFactoryReference("jill.xtm", "jill.xtm", factory);
    tracker = new TopicMapTracker(ref);
    TopicMapEvents.addTopicListener(ref, tracker);
  }

  private TopicMapIF read(String file) throws IOException {
    file = resolveFileName("sdshare" + File.separator + "topicmaps" +
                           File.separator + file);
    return ImportExportUtils.getReader(file).read();
  }

  // ===== TESTS

  public void testNoChanges() {
    assertTrue("found changes in feed, despite no changes having been made",
               tracker.getChangeFeed().isEmpty());
  }

  public void testAddedTopic() {
    // TopicMapBuilderIF builder = topicmap.getBuilder();
    // TopicIF topic = builder.makeTopic();
    // builder.makeTopicName(topic, "foo");
    TopicIF topic = topicmap.getTopics().iterator().next();
    topic.remove();

    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("only made one change, but more changes in feed",
                 1, changes.size());
    ChangedTopic change = changes.get(0);
    assertTrue("wrong object ID on recorded change",
               change.getObjectId().equals(topic.getObjectId()));
  }
  
}