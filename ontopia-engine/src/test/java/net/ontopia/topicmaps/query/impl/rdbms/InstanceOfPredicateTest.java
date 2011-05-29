
package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class InstanceOfPredicateTest
  extends net.ontopia.topicmaps.query.core.InstanceOfPredicateTest {
  
  public InstanceOfPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
