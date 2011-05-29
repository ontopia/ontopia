
package net.ontopia.topicmaps.core;

/**
 * This class tests a TopicMapStoreIF implementation.
 */

public abstract class TopicMapStoreTest extends AbstractTopicMapTest {
  
  public TopicMapStoreTest(String name) {
    super(name);
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
  





