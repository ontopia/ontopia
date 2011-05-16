
// $Id: ParsedQueryTest.java,v 1.5 2005/07/13 08:54:59 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class ParsedQueryTest
  extends net.ontopia.topicmaps.query.core.ParsedQueryTest {
  
  public ParsedQueryTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
