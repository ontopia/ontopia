package net.ontopia.topicmaps.impl.basic;

import net.ontopia.topicmaps.core.TestFactoryIF;

public class TopicMapStoreTest extends net.ontopia.topicmaps.core.TopicMapStoreTest {

  public TopicMapStoreTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new BasicTestFactory();
  }

}
