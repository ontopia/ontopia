
package net.ontopia.topicmaps.utils;

import junit.framework.TestCase;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.core.Locators;
import net.ontopia.topicmaps.core.*;

public class IdentityUtilsTest extends TestCase {
  protected TopicMapIF    topicmap; 
  protected TopicMapBuilderIF builder;

  public IdentityUtilsTest(String name) {
    super(name);
  }
    
  public void setUp() {
    topicmap = makeTopicMap();
    builder = topicmap.getBuilder();
  }
  
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    store.setBaseAddress(Locators.getURILocator("http://example.org/base/"));
    return store.getTopicMap();
  }

  public void testGetObjectBySymbolicId() {
    String symbolicId = "foo";
    TopicIF topic = builder.makeTopic();
    LocatorIF base = topicmap.getStore().getBaseAddress();
    LocatorIF loc = base.resolveAbsolute("#" + symbolicId);
    topic.addItemIdentifier(loc);

    TMObjectIF topic2 = IdentityUtils.getObjectBySymbolicId(topicmap, symbolicId);
    assertEquals("Topic not found by symbolic id", topic, topic2);    
  }

  public void testGetSymbolicIdLocator() {
    String symbolicId = "foo";
    LocatorIF base = topicmap.getStore().getBaseAddress();
    LocatorIF loc = base.resolveAbsolute("#" + symbolicId);
    LocatorIF loc2 = IdentityUtils.getSymbolicIdLocator(topicmap, symbolicId);
    assertEquals("Symbolic locators not equal", loc, loc2);
  }
  
}
