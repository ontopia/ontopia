
// $Id: DatatypePredicateTest.java,v 1.1 2008/07/23 14:25:42 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.IOException;

public class DatatypePredicateTest
  extends net.ontopia.topicmaps.query.core.test.DatatypePredicateTest {
  
  public DatatypePredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
