
package net.ontopia.topicmaps.core;

import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.utils.OntopiaRuntimeException;

import junit.framework.TestCase;

public abstract class AbstractTopicMapTest extends TestCase {

  protected TestFactoryIF factory;
  protected TopicMapReferenceIF topicmapRef;
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TopicMapBuilderIF builder; // builder used for creating new objects

  public AbstractTopicMapTest(String name) {
    super(name);
  }

  protected abstract TestFactoryIF getFactory() throws Exception;
  
  protected void setUp() throws Exception {
    factory = getFactory();
    // Get a new topic map object from the factory.
    topicmapRef = factory.makeTopicMapReference();
    topicmap = topicmapRef.createStore(false).getTopicMap();
    assertTrue("Null topic map!" , topicmap != null);
    // Get the builder of that topic map.
    builder = topicmap.getBuilder();
    assertTrue("Null builder!", builder != null);
  }

  protected void tearDown() {
    if (topicmapRef != null) {
      // Inform the factory that the topic map is not needed anymore.
      topicmap.getStore().close();
      factory.releaseTopicMapReference(topicmapRef);
      // Reset the member variables.
      topicmapRef = null;
      topicmap = null;
      builder = null;
    }
  }

}





