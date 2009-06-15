
// $Id: ScopePredicateTest.java,v 1.1 2003/10/27 07:51:02 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.IOException;

public class ScopePredicateTest
  extends net.ontopia.topicmaps.query.core.test.ScopePredicateTest {
  
  public ScopePredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
