package net.ontopia.topicmaps.impl.rdbms.index;

import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTestFactory;

public class IndexTest extends net.ontopia.topicmaps.core.index.IndexTest {

  public IndexTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

}
