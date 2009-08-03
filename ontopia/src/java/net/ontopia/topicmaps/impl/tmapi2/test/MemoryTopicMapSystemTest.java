package net.ontopia.topicmaps.impl.tmapi2.test;

import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

public class MemoryTopicMapSystemTest 
  extends net.ontopia.test.AbstractOntopiaTestCase {

  private TopicMapSystemFactory tmsf;
  private TopicMapSystem tms;

  private Locator locFirst; 

  public MemoryTopicMapSystemTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    tmsf = TopicMapSystemFactory.newInstance();
    tms = tmsf.newTopicMapSystem();
    
    locFirst = tms.createLocator("http://ontopia.net/first");
  }

  @Override
  protected void tearDown() throws Exception {
    tms.close();
  }

  public void testCreate() {
    try {
      TopicMap tm = tms.createTopicMap(locFirst);
      assertNotNull("could not create new TopicMap", tm);
      
    } catch (TopicMapExistsException e) {
      fail("failed to create new TopicMap in empty TopicMapSystem");
    }
  }
  
  public void testGet() {
    TopicMap tm = tms.getTopicMap(locFirst);
    assertNull("found a TopicMap in an empty TopicMapSystem", tm);
    
    try {
      tm = tms.createTopicMap(locFirst);
      assertNotNull("could not create new TopicMap", tm);
      
    } catch (TopicMapExistsException e) {
      fail("failed to create new TopicMap in empty TopicMapSystem");
    }

    TopicMap tm2 = tms.getTopicMap(locFirst);
    assertNotNull("could not get newly created TopicMap", tm2);
  }
  
  public void testDelete() {
    TopicMap tm;
    
    try {
      tm = tms.createTopicMap(locFirst);
      assertNotNull("could not create new TopicMap", tm);
      
    } catch (TopicMapExistsException e) {
      fail("failed to create new TopicMap in empty TopicMapSystem");
    }

    TopicMap tm2 = tms.getTopicMap(locFirst);
    assertNotNull("could not get newly created TopicMap", tm2);
    
    tm2.remove();
    
    tm = tms.getTopicMap(locFirst);
    assertNull("TopicMap has not been removed from TopicMapSystem after remove operation", tm);
  }
}
