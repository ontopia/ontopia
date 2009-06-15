
// $Id: RDBMSTopicMapSourceTest.java,v 1.3 2007/09/03 07:38:18 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms.test;

import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource;
import net.ontopia.topicmaps.entry.test.AbstractTopicMapSourceTest;

public class RDBMSTopicMapSourceTest extends AbstractTopicMapSourceTest {

  public RDBMSTopicMapSourceTest(String name) {
    super(name);
  }

  // --- Test cases

  public void testSource() {
    RDBMSTopicMapSource source = new RDBMSTopicMapSource();
    source.setId("fooid");
    source.setTitle("footitle");
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
    source.setSupportsCreate(true);
    source.setSupportsDelete(true);
    
    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(source);
  }
  
}
