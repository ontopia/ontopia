
// $Id: ReifiesPredicateTest.java,v 1.1 2003/07/15 14:14:27 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.IOException;

public class ReifiesPredicateTest
  extends net.ontopia.topicmaps.query.core.test.ReifiesPredicateTest {
  
  public ReifiesPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
