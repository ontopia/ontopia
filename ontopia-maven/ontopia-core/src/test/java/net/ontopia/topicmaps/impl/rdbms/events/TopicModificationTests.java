package net.ontopia.topicmaps.impl.rdbms.events;

import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTestFactory;

public class TopicModificationTests extends net.ontopia.topicmaps.core.events.TopicModificationTests {

  public TopicModificationTests(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

}
