
// $Id: RolePredicateTest.java,v 1.1 2003/08/05 15:47:14 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.IOException;

public class RolePredicateTest
  extends net.ontopia.topicmaps.query.core.test.RolePredicateTest {
  
  public RolePredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
