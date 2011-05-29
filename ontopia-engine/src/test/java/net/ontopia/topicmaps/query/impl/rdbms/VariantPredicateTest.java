
package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class VariantPredicateTest
  extends net.ontopia.topicmaps.query.core.VariantPredicateTest {
  
  public VariantPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
