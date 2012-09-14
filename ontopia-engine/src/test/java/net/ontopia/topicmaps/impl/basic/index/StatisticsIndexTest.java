
package net.ontopia.topicmaps.impl.basic.index;

import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.impl.basic.BasicTestFactory;

public class StatisticsIndexTest extends net.ontopia.topicmaps.core.index.StatisticsIndexTest {

  public StatisticsIndexTest(String name) {
    super(name);
  }

  @Override
  protected TestFactoryIF getFactory() throws Exception {
    return new BasicTestFactory();
  }

}
