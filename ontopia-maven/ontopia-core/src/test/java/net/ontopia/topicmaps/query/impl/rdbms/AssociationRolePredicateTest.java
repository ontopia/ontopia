
// $Id: AssociationRolePredicateTest.java,v 1.1 2003/08/18 10:16:39 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class AssociationRolePredicateTest
  extends net.ontopia.topicmaps.query.core.AssociationRolePredicateTest {
  
  public AssociationRolePredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
