package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.topicmaps.core.TestFactoryIF;

public class TopicTest extends net.ontopia.topicmaps.core.TopicTest {

  public TopicTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

}
