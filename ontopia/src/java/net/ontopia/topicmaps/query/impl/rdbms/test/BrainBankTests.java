
// $Id: BrainBankTests.java,v 1.1 2003/08/06 15:57:08 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.IOException;

public class BrainBankTests
  extends net.ontopia.topicmaps.query.core.test.BrainBankTests {
  
  public BrainBankTests(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
