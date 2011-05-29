
package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class AssociationPredicateTest
  extends net.ontopia.topicmaps.query.core.AssociationPredicateTest {
  
  public AssociationPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
