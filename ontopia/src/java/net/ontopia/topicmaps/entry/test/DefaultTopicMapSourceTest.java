// $Id: DefaultTopicMapSourceTest.java,v 1.6 2002/05/29 13:38:38 hca Exp $

package net.ontopia.topicmaps.entry.test;

import java.util.*;
import junit.framework.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.test.AbstractTopicMapTest;

public class DefaultTopicMapSourceTest extends AbstractTopicMapTest {
  TopicMapStoreIF store;
  DefaultTopicMapSource source;

  public DefaultTopicMapSourceTest(String name) {
    super(name);
  }

  public void setUp() {
    store = new InMemoryTopicMapStore();
    source = new DefaultTopicMapSource();
  }
  
  // --- Test cases

  public void testReferences() {
    assertTrue("source not empty by default",
           source.getReferences().size() == 0);

    TopicMapReferenceIF ref =
      new DefaultTopicMapReference("id", "title", store);
    source.addReference(ref);

    assertTrue("source not registered with reference",
               ref.getSource() == source);

    assertTrue("source did not discover add",
           source.getReferences().size() == 1);
    assertTrue("reference identity lost",
           source.getReferences().iterator().next() == ref);

    source.removeReference(ref);
    
    assertTrue("source not deregistered with reference",
               ref.getSource() == null);
    
  }
}






