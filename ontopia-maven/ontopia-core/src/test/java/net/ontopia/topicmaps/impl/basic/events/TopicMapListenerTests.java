package net.ontopia.topicmaps.impl.basic.events;

import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.impl.basic.BasicTestFactory;

public class TopicMapListenerTests extends net.ontopia.topicmaps.core.events.TopicMapListenerTests {

  public TopicMapListenerTests(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new BasicTestFactory();
  }

}
