
// $Id: DynamicSearcherPredicateTest.java,v 1.1 2006/07/06 10:55:31 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.IOException;

public class DynamicSearcherPredicateTest
  extends net.ontopia.topicmaps.query.core.test.DynamicSearcherPredicateTest {
  
  public DynamicSearcherPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
