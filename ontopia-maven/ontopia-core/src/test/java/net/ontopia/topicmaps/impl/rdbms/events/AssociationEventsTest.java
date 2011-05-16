package net.ontopia.topicmaps.impl.rdbms.events;

import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTestFactory;

public class AssociationEventsTest extends net.ontopia.topicmaps.core.events.AssociationEventsTest {

  public AssociationEventsTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

}
