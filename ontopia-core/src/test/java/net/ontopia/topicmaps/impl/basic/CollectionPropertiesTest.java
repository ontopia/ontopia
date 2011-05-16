package net.ontopia.topicmaps.impl.basic;

import net.ontopia.topicmaps.core.TestFactoryIF;

public class CollectionPropertiesTest extends net.ontopia.topicmaps.core.CollectionPropertiesTest {

  public CollectionPropertiesTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new BasicTestFactory();
  }

}
