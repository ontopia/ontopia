
package net.ontopia.topicmaps.utils;

import junit.framework.TestCase;
import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;

public class NameStringifierTest extends TestCase {
  protected TopicMapIF        topicmap; 
  protected TopicIF           topic;
  protected TopicNameIF        basename;
  protected VariantNameIF     variant;
  protected TopicMapBuilderIF builder;
  protected StringifierIF     stringifier;

  public NameStringifierTest(String name) {
    super(name);
  }
    
  public void setUp() {
    topicmap = makeTopicMap();
    topic = builder.makeTopic();
    basename = builder.makeTopicName(topic, "");
    variant = builder.makeVariantName(basename, "");
    stringifier = new NameStringifier();
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }
 
  // --- Test cases

  public void testTopicNameEmpty() {
    assertTrue("base name with no name did not stringify to \"\"",
							 stringifier.toString(basename).equals(""));
  }

  public void testTopicName() {
    basename.setValue("basename");
    assertTrue("base name stringified wrongly",
           stringifier.toString(basename).equals("basename"));
  }

  public void testVariantEmpty() {
    assertTrue("variant with no name did not stringify to \"\"",
							 stringifier.toString(variant).equals(""));
  }

  public void testVariant() {
    variant.setValue("variant");
    assertTrue("variant stringified wrongly",
           stringifier.toString(variant).equals("variant"));
  }
  
}




