
// $Id: TopicMapPredicateTest.java,v 1.1 2003/08/23 14:25:28 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.IOException;

public class TopicMapPredicateTest
  extends net.ontopia.topicmaps.query.core.test.TopicMapPredicateTest {
  
  public TopicMapPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
