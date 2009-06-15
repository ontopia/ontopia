package net.ontopia.topicmaps.nav2.taglibs.tolog.test;

import java.util.Collection;
import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;
import net.ontopia.topicmaps.nav2.impl.basic.ContextManager;
import net.ontopia.topicmaps.nav2.taglibs.tolog.ContextManagerMapWrapper;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;

public class ContextManagerMapWrapperTest extends AbstractTopicMapTestCase {
  
  public ContextManagerMapWrapperTest(String name) {
    super(name);
  }
  
  public void testRemove() {
    ContextManagerMapWrapper contextManagerMapWrapper = new 
    ContextManagerMapWrapper(new ContextManager());
    contextManagerMapWrapper.put("the key", "the value");
    contextManagerMapWrapper.remove("the key");
    Collection value = (Collection)contextManagerMapWrapper.get("the key");
    assertTrue("Expected empty Collection, but found Collection of size" +
        value.size(), value.isEmpty());
    
    if (contextManagerMapWrapper.remove("nonExistent") != null)
      fail("Removing a non-existent variable should return null");
    if (contextManagerMapWrapper.get("nonExistent") != null)
      fail("Removing a non-existent variable bound the variable to null,"
          + " but should have done nothing.");
  }
  
  public void testGet() {
    ContextManagerMapWrapper contextManagerMapWrapper = new 
        ContextManagerMapWrapper(new ContextManager());
    assertNull(contextManagerMapWrapper.get("the key"));
  }
}
