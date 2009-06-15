
// $Id: XTMPathTopicMapSourceTest.java,v 1.1 2005/07/04 10:19:26 grove Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.File;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.entry.test.AbstractTopicMapSourceTest;
import net.ontopia.topicmaps.xml.XTMPathTopicMapSource;

public class XTMPathTopicMapSourceTest extends AbstractTopicMapSourceTest {

  public XTMPathTopicMapSourceTest(String name) {
    super(name);
  }

  // --- Test cases

  public void testSource() {
    XTMPathTopicMapSource source = new XTMPathTopicMapSource();
    source.setId("fooid");
    source.setTitle("footitle");
    source.setPath(AbstractOntopiaTestCase.getTestDirectory() + File.separator + "canonical" + File.separator + "in");
    source.setSuffix(".xtm");
    
    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(source);
  }
  
}
