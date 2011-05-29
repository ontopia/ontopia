
package net.ontopia.topicmaps.cmdlineutils;

import java.io.IOException;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;

public class StatsTest extends CommandLineUtilsTest {
  
  public StatsTest(String name) {
    super(name);
  }

  protected void setUp() {

    XTMTopicMapReader reader  = null;

    String filename = FileUtils.getTestInputFile("various", "stats.xtm");

    try {
      reader = new XTMTopicMapReader(URIUtils.getURI(filename));
      tm = reader.read();
    } catch (IOException e) {
      fail("Error reading file\n" + e);
    }

  }

  protected void tearDown() {
    tm = null;
  }
}
