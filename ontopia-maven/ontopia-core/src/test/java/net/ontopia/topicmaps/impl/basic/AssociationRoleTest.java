package net.ontopia.topicmaps.impl.basic;

import net.ontopia.topicmaps.core.TestFactoryIF;

public class AssociationRoleTest extends net.ontopia.topicmaps.core.AssociationRoleTest {

  public AssociationRoleTest(String name) {
    super(name);
  }

  protected TestFactoryIF getFactory() throws Exception {
    return new BasicTestFactory();
  }

}
