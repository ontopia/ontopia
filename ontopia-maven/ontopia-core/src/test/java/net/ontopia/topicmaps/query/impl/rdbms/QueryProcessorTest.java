
// $Id: QueryProcessorTest.java,v 1.9 2008/01/11 12:58:56 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class QueryProcessorTest
  extends net.ontopia.topicmaps.query.core.QueryProcessorTest {
  
  public QueryProcessorTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
