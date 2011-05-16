package net.ontopia.topicmaps.impl.basic.index;

import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.impl.basic.BasicTestFactory;

public class IndexTest extends net.ontopia.topicmaps.core.index.IndexTest {

  public IndexTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new BasicTestFactory();
  }

}
