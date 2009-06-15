// $Id: DefaultTopicMapReferenceTest.java,v 1.5 2002/05/29 13:38:38 hca Exp $

package net.ontopia.topicmaps.entry.test;

import junit.framework.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.test.AbstractTopicMapTest;

public class DefaultTopicMapReferenceTest extends AbstractTopicMapTest {
  TopicMapStoreIF store;
  DefaultTopicMapSource source;
  TopicMapReferenceIF reference;

  public DefaultTopicMapReferenceTest(String name) {
    super(name);
  }

  public void setUp() {
    store = new InMemoryTopicMapStore();
    source = new DefaultTopicMapSource();
    reference = new DefaultTopicMapReference("id", "title", store);
    source.addReference(reference);
  }
  
  // --- Test cases

  public void testId() {
    assertTrue("default id not set", reference.getId().equals("id"));
    reference.setId("newid");
    assertTrue("id not set correctly", reference.getId().equals("newid"));
  }

  public void testTitle() {
    assertTrue("default title not set", reference.getTitle().equals("title"));
    reference.setTitle("newtitle");
    assertTrue("title not set correctly", reference.getTitle().equals("newtitle"));
  }

  public void testStore() throws java.io.IOException {
    assertTrue("default store not set [mutable]", reference.createStore(false) == store);
    assertTrue("default store not set [readonly]", reference.createStore(true) == store);
  }
}






