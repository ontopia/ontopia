
// $Id: NotEqualsPredicateTest.java,v 1.1 2004/02/27 14:15:53 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class NotEqualsPredicateTest
  extends net.ontopia.topicmaps.query.core.NotEqualsPredicateTest {
  
  public NotEqualsPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
