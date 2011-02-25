
package net.ontopia.topicmaps.utils.sdshare.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
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
    ref.createStore(false);
    tracker = new TopicMapTracker(ref);
    TopicMapEvents.addTopicListener(ref, tracker);
    ref.createStore(false); // turn on events (grrr)
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
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF topic = builder.makeTopic();

    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("only made one change, but more changes in feed",
                 1, changes.size());
    ChangedTopic change = changes.get(0);
    assertTrue("wrong object ID on recorded change",
               change.getObjectId().equals(topic.getObjectId()));
  }

  public void testRemovedTopic() {
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF jill = getTopicById("jill");
    TopicIF ontopia = getTopicById("ontopia");
    String oid = jill.getObjectId();
    jill.remove(); // this causes two changes, not one (because of assoc)
    
    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("only changed two topics, but more changes in feed",
                 2, changes.size());
    ChangedTopic jillchange = changes.get(0);
    ChangedTopic ontopiachange = changes.get(1);
    // since order is unpredictable, must allow for both possibilities
    if (!jillchange.getObjectId().equals(oid)) {
      jillchange = changes.get(1);
      ontopiachange = changes.get(0);
    }
    
    assertTrue("wrong object ID on recorded change to jill",
               jillchange.getObjectId().equals(oid));
    assertTrue("jill change not recorded as object deletion",
               jillchange instanceof DeletedTopic);
    assertTrue("wrong object ID on recorded change to ontopia",
               ontopiachange.getObjectId().equals(ontopia.getObjectId()));
    assertTrue("ontopia change recorded as object deletion",
               !(ontopiachange instanceof DeletedTopic));
  }

  public void testTwoChanges() {
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF topic = builder.makeTopic();
    TopicIF jill = getTopicById("jill");
    builder.makeTopicName(jill, "Another name");

    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("made two changes, but more changes in feed",
                 2, changes.size());

    ChangedTopic change = changes.get(0);
    assertTrue("wrong object ID on first recorded change",
               change.getObjectId().equals(topic.getObjectId()));

    change = changes.get(1);
    assertTrue("wrong object ID on second recorded change",
               change.getObjectId().equals(jill.getObjectId()));
  }

  public void testDuplicateChanges() {
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF topic = builder.makeTopic();
    TopicIF jill = getTopicById("jill");
    TopicNameIF tn = builder.makeTopicName(jill, "Another name");

    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("made two changes, but more changes in feed",
                 2, changes.size());

    ChangedTopic change = changes.get(0);
    assertTrue("wrong object ID on first recorded change",
               change.getObjectId().equals(topic.getObjectId()));

    change = changes.get(1);
    assertTrue("wrong object ID on second recorded change",
               change.getObjectId().equals(jill.getObjectId()));

    // making another change
    tn.remove();
    assertEquals("changed two topics, but more changes in feed",
                 2, changes.size());
    change = changes.get(0);
    assertEquals("wrong object ID on first recorded change",
                 change.getObjectId(), topic.getObjectId());
    change = changes.get(1);
    assertEquals("wrong object ID on second recorded change",
                 change.getObjectId(), jill.getObjectId());
  }

  public void testDuplicateChanges2() {
    // different order from previous test
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF jill = getTopicById("jill");
    TopicNameIF tn = builder.makeTopicName(jill, "Another name");
    TopicIF topic = builder.makeTopic();

    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("made two changes, but more changes in feed",
                 2, changes.size());

    ChangedTopic change = changes.get(0);
    assertTrue("wrong object ID on first recorded change",
               change.getObjectId().equals(jill.getObjectId()));

    change = changes.get(1);
    assertTrue("wrong object ID on second recorded change",
               change.getObjectId().equals(topic.getObjectId()));

    // making another change
    tn.remove();
    assertEquals("changed two topics, but more changes in feed",
                 2, changes.size());
    change = changes.get(0);
    assertEquals("wrong object ID on first recorded change",
                 change.getObjectId(), topic.getObjectId());
    change = changes.get(1);
    assertEquals("wrong object ID on second recorded change",
                 change.getObjectId(), jill.getObjectId());
  }

  public void testChangeAllTopics() {
    List<String> changed = new ArrayList<String>();
    for (TopicIF topic : topicmap.getTopics()) {
      if (topic.getTopicNames().isEmpty())
        continue;
      changed.add(topic.getObjectId());
      TopicNameIF tn = topic.getTopicNames().iterator().next();
      tn.setValue(topic.getObjectId());
    }

    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("wrong number of changes listed",
                 changed.size(), changes.size());

    int ix = 0;
    for (ChangedTopic change : changes)
      assertEquals("change " + ix + " is wrong",
                   change.getObjectId(), changed.get(ix++));

  }

  public void testChangeAllTopicsDuplicate() {
    // first we change all topics in the topic map
    List<String> changed = new ArrayList<String>();
    for (TopicIF topic : topicmap.getTopics()) {
      if (topic.getTopicNames().isEmpty())
        continue;
      changed.add(topic.getObjectId());
      TopicNameIF tn = topic.getTopicNames().iterator().next();
      tn.setValue(topic.getObjectId());
    }

    // then we make a list of the topics in random order
    changed.clear();
    List<TopicIF> topics = new ArrayList(topicmap.getTopics());
    Collections.shuffle(topics);

    // then we change the topics in that order
    for (TopicIF topic : topics) {
      if (topic.getTopicNames().isEmpty())
        continue;
      changed.add(topic.getObjectId());
      TopicNameIF tn = topic.getTopicNames().iterator().next();
      tn.setValue(topic.getObjectId() + "_");
    }

    // ok, now we can check
    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("wrong number of changes listed",
                 changed.size(), changes.size());

    int ix = 0;
    for (ChangedTopic change : changes)
      assertEquals("change " + ix + " is wrong",
                   change.getObjectId(), changed.get(ix++));
  }
  
  public void testFirstChangeThenDelete() {
    // testing because this requires a ChangedTopic to be replaced by
    // a DeletedTopic
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF jill = getTopicById("jill");
    TopicNameIF tn = builder.makeTopicName(jill, "Another name");

    // verify normal change
    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("made one change, but more changes in feed",
                 1, changes.size());
    assertEquals("wrong topic recorded as changed",
                 jill.getObjectId(), changes.get(0).getObjectId());

    // now delete the topic
    jill.remove();
    // note: this also modified "#ontopia", since they share an association

    // verify that new change is a DeletedTopic
    changes = tracker.getChangeFeed();
    assertEquals("change two topics, but more changes in feed.",
                 2, changes.size());

    // it's not predictable which order the two topics will be recorded in,
    // so we simply search for jill
    for (ChangedTopic change : changes) {
      if (jill.getObjectId().equals(change.getObjectId())) {
        assertTrue("deletion not recorded as such",
                   change instanceof DeletedTopic);
        return; // we're finished testing
      }
    }

    // if we got here it means we didn't find jill at all
    fail("change for 'jill' topic not found");
  }

  public void testChangeExpiry() {
    // setting a fairly long expiry time so that changes don't magically
    // disappear while we are working
    tracker.setExpiryTime(10);

    // change all topics
    testChangeAllTopics();

    // wait 100 ms
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }

    // change a topic, and observe that all old changes are gone
    testAddedTopic();
  }

  public void testChangeExpiry2() {
    // setting a fairly long expiry time so that changes don't magically
    // disappear while we are working
    tracker.setExpiryTime(10);

    // change two topics
    testTwoChanges();

    // wait 6 ms
    try {
      Thread.sleep(6);
    } catch (InterruptedException e) {
    }

    // create a new topic
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF topic = builder.makeTopic();

    // wait another 6 ms
    try {
      Thread.sleep(6);
    } catch (InterruptedException e) {
    }

    // now only the new topic should be visible
    List<ChangedTopic> changes = tracker.getChangeFeed();
    assertEquals("only made one change, but more changes in feed",
                 1, changes.size());
    ChangedTopic change = changes.get(0);
    assertTrue("wrong object ID on recorded change",
               change.getObjectId().equals(topic.getObjectId()));
  }

  
  // ===== UTILITIES

  private TopicIF getTopicById(String id) {
    LocatorIF ii = topicmap.getStore().getBaseAddress().resolveAbsolute('#' + id);
    return (TopicIF) topicmap.getObjectByItemIdentifier(ii);
  }
  
}