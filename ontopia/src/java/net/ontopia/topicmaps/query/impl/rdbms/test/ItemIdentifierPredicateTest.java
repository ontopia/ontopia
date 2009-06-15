
// $Id: ItemIdentifierPredicateTest.java,v 1.1 2007/06/13 08:31:19 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.IOException;

public class ItemIdentifierPredicateTest
  extends net.ontopia.topicmaps.query.core.test.ItemIdentifierPredicateTest {
  
  public ItemIdentifierPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
