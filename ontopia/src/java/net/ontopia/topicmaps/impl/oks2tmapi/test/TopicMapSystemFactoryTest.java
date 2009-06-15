
// $Id: TopicMapSystemFactoryTest.java,v 1.2 2005/03/21 18:40:16 larsga Exp $

package net.ontopia.topicmaps.impl.oks2tmapi.test;

import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.TopicMapSystem;

/**
 * INTERNAL.
 */
public class TopicMapSystemFactoryTest 
  extends net.ontopia.test.AbstractOntopiaTestCase {

  public TopicMapSystemFactoryTest(String name) {
    super(name);
  }

  public void testFactory() throws org.tmapi.core.TMAPIException {
    TopicMapSystemFactory tmsf = TopicMapSystemFactory.newInstance();
    assertTrue("TopicMapSystemFactory is not net.ontopia.topicmaps.impl.oks2tmapi.TopicMapSystemFactory", 
	       tmsf instanceof net.ontopia.topicmaps.impl.oks2tmapi.TopicMapSystemFactory);

    TopicMapSystem ts = tmsf.newTopicMapSystem();
    assertTrue("TopicMapSystem is not net.ontopia.topicmaps.impl.oks2tmapi.TopicMapSystem", 
	       ts instanceof net.ontopia.topicmaps.impl.oks2tmapi.TopicMapSystem);

    ts.close();
  }

}
