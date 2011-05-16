
// $Id: RDBMSSingleTopicMapSourceTest.java,v 1.2 2007/09/03 07:38:18 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.topicmaps.entry.AbstractTopicMapSourceTest;

public class RDBMSSingleTopicMapSourceTest extends AbstractTopicMapSourceTest {

  public RDBMSSingleTopicMapSourceTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
    super.setUp();
  }

  // --- Test cases

  public void testSource() {
    RDBMSSingleTopicMapSource source = new RDBMSSingleTopicMapSource();
    source.setId("fooid");
    source.setTitle("footitle");
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));

    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(source);
  }
  
}
