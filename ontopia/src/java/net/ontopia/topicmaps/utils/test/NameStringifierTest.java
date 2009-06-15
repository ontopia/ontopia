// $Id: NameStringifierTest.java,v 1.9 2008/06/12 14:37:24 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.test;

import java.util.*;
import net.ontopia.utils.StringifierIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.topicmaps.utils.NameStringifier;
import net.ontopia.topicmaps.utils.PSI;

public class NameStringifierTest extends AbstractTopicMapTestCase {
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




