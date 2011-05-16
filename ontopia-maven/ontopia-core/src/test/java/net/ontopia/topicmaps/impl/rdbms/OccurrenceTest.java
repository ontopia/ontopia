package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.topicmaps.core.TestFactoryIF;

public class OccurrenceTest extends net.ontopia.topicmaps.core.OccurrenceTest {

  public OccurrenceTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

}
