
// $Id: ComparisonPredicateTests.java,v 1.1 2004/02/02 17:19:04 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class ComparisonPredicateTests
  extends net.ontopia.topicmaps.query.core.ComparisonPredicateTests {
  
  public ComparisonPredicateTests(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
