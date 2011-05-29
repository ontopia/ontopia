
package net.ontopia.topicmaps.xml;

import java.io.File;
import net.ontopia.topicmaps.entry.AbstractTopicMapSourceTest;
import net.ontopia.utils.FileUtils;
import org.junit.Test;


public class XTMPathTopicMapSourceTest extends AbstractTopicMapSourceTest {

  private final static String testdataDirectory = "canonical";

  public XTMPathTopicMapSourceTest(String name) {
    super(name);
  }

  // --- Test cases

  @Test
  public void testSource() {
    XTMPathTopicMapSource source = new XTMPathTopicMapSource();
    source.setId("fooid");
    source.setTitle("footitle");
    source.setPath("classpath:" + FileUtils.testdataInputRoot + testdataDirectory + "/" + "in");
    source.setSuffix(".xtm");
    
    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(source);
  }
  
}
