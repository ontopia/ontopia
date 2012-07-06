package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.events.AbstractTopicMapListener;
import net.ontopia.topicmaps.core.events.TopicMapEvents;

/**
 * Tests if the modifications to the TopicEvents class result in correct behavior of the
 * TopicMapEvents.
 */
public class TopicMapEventsTests extends AbstractTopicMapTest {

  private int added = 0;

  public TopicMapEventsTests(String name) {
    super(name);
  }

  @Override
  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    added = 0;
  }

  public void testTransactionCommit() throws IOException {
    TopicMapEvents.addTopicListener(topicmapRef, new AbstractTopicMapListener() {

      @Override
      public void objectAdded(TMObjectIF snapshot) {
        added++;
      }
      
    });
    
    TopicMapStoreIF store = null;
    try {
      store = topicmapRef.createStore(false);
      store.getTopicMap().getBuilder().makeTopic();

      // before commit: 0 calls should have been made to objectAdded
      assertEquals(0, added);

      store.commit();
      
      // after commit: 1 call should have been made to objectAdded
      assertEquals(1, added);
      
    } finally {
      store.close();
    }
  }
  
  public void testTransactionAbort() throws IOException {
    TopicMapEvents.addTopicListener(topicmapRef, new AbstractTopicMapListener() {

      @Override
      public void objectAdded(TMObjectIF snapshot) {
        added++;
      }
      
    });
    
    TopicMapStoreIF store = null;
    try {
      store = topicmapRef.createStore(false);
      store.getTopicMap().getBuilder().makeTopic();
      
      // before commit: 0 calls should have been made to objectAdded
      assertEquals(0, added);

      store.abort();
      
      // after abort: 0 calls should have been made to objectAdded
      assertEquals(0, added);
      
    } finally {
      store.close();
    }
  }
}
