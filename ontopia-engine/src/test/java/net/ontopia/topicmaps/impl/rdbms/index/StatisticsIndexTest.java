
package net.ontopia.topicmaps.impl.rdbms.index;

import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTestFactory;

public class StatisticsIndexTest extends net.ontopia.topicmaps.impl.basic.index.StatisticsIndexTest {

  public StatisticsIndexTest(String name) {
    super(name);
  }

  @Override
  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }
}
