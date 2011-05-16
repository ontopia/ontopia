
// $Id: DynamicOccurrencePredicateTest.java,v 1.1 2003/09/04 12:58:50 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class DynamicOccurrencePredicateTest
  extends net.ontopia.topicmaps.query.core.DynamicOccurrencePredicateTest {
  
  public DynamicOccurrencePredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
