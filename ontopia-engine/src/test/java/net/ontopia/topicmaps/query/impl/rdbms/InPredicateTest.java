
package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;
import org.junit.Ignore;

@Ignore // disabled: EXPERIMENTAL predicate
public class InPredicateTest
  extends net.ontopia.topicmaps.query.core.InPredicateTest {
  
  public InPredicateTest(String name) {
    super(name);
  }
  
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }
  
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
  
}
