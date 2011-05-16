
// $Id: ValueLikePredicateTest.java,v 1.2 2005/03/21 07:03:33 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class ValueLikePredicateTest
  extends net.ontopia.topicmaps.query.core.ValueLikePredicateTest {
  
  public ValueLikePredicateTest(String name) {
    super(name);
  }

  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }

  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }

}
