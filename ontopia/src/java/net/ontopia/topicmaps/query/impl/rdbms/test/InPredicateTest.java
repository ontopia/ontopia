
// $Id: InPredicateTest.java,v 1.1 2003/11/27 20:49:35 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.IOException;

public class InPredicateTest
  extends net.ontopia.topicmaps.query.core.test.InPredicateTest {
  
  public InPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
