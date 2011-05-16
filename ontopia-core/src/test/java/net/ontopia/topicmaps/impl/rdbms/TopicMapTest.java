package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.topicmaps.core.TestFactoryIF;

public class TopicMapTest extends net.ontopia.topicmaps.core.TopicMapTest {

  public TopicMapTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

}
