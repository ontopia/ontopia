package net.ontopia.topicmaps.impl.basic.index;

import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.impl.basic.BasicTestFactory;

public class ScopeIndexTest extends net.ontopia.topicmaps.core.index.ScopeIndexTest {

  public ScopeIndexTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new BasicTestFactory();
  }

}
