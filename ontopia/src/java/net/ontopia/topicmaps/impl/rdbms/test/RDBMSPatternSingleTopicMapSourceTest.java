
// $Id: RDBMSPatternSingleTopicMapSourceTest.java,v 1.1 2007/09/03 07:38:18 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms.test;

import net.ontopia.topicmaps.impl.rdbms.RDBMSPatternSingleTopicMapSource;
import net.ontopia.topicmaps.entry.test.AbstractTopicMapSourceTest;

public class RDBMSPatternSingleTopicMapSourceTest extends AbstractTopicMapSourceTest {

  public RDBMSPatternSingleTopicMapSourceTest(String name) {
    super(name);
  }

  // --- Test cases

  public void testSource() {
    RDBMSPatternSingleTopicMapSource source = new RDBMSPatternSingleTopicMapSource();
    source.setId("foosource");
    source.setTitle("footitle");
    source.setReferenceId("foo");
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
    source.setMatch("comments");
    source.setPattern("My comment");
    
    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(source);
  }
  
}
