// $Id: TopicMapStoreTest.java,v 1.13 2008/06/11 16:55:57 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.OntopiaUnsupportedException;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * This class tests a TopicMapStoreIF implementation.
 */

public class TopicMapStoreTest extends AbstractTopicMapTest {
  
  protected TopicMapReferenceIF topicmapRef;
  protected TopicMapIF topicmap;
  protected TopicMapStoreIF tmStore;
  protected TopicMapBuilderIF builder;

  public TopicMapStoreTest(String name) {
    super(name);
  }

  protected void setUp() {
    // Get a new topic map object from the factory.
    topicmapRef = factory.makeTopicMapReference();
    try {
      topicmap = topicmapRef.createStore(false).getTopicMap();
      assertTrue("Null topic map!", topicmap != null);
    
      // Get the topic map builder for that topic map.
      tmStore = topicmap.getStore();
      builder = topicmap.getBuilder();
      assertTrue("Null builder!", builder != null);
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void tearDown() {
    // Inform the factory that the topic map is not needed anymore.
    topicmap.getStore().close();
    factory.releaseTopicMapReference(topicmapRef);
  }

  /**
   * Tests that close and open update the status of the store appropriately.
   */
  public void testOpenClose() {
    TopicMapStoreIF _store = factory.makeStandaloneTopicMapStore();
    try {
      assertTrue("Store open", !_store.isOpen());
      _store.open();
      assertTrue("Store not open", _store.isOpen());
      TopicMapIF tm1 = _store.getTopicMap();
      _store.close();
      assertTrue("Store not closed", !_store.isOpen());
      try {
        TopicMapIF tm = _store.getTopicMap();
        // Expected.
        _store.close();
      } catch (StoreNotOpenException ex) {
        fail("Couldn't retrieve topic map [via TopicMapStoreIF.getTopicMap()] from a closed store!");
      }
      _store.open();
      assertTrue("Could not reopen store.", _store.isOpen());
    } finally {
      _store.delete(true);
    }
  }
}
  





