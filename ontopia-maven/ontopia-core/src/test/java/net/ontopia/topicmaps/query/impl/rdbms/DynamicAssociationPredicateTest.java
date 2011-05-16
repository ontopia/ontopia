
// $Id: DynamicAssociationPredicateTest.java,v 1.1 2003/11/21 13:23:44 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class DynamicAssociationPredicateTest
  extends net.ontopia.topicmaps.query.core.DynamicAssociationPredicateTest {
  
  public DynamicAssociationPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
