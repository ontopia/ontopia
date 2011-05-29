
package net.ontopia.topicmaps.impl.basic;

import junit.framework.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.URIUtils;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;

public class PackageTest extends TopicMapPackageTest {
  
  private final static String testdataDirectory = "various";

  public PackageTest(String name) {
    super(name);
  }

  protected void setUp() {
    if (tm == null) {
      try {
        TopicMapReaderIF reader =
          new XTMTopicMapReader(URIUtils.getURI(FileUtils.getTestInputFile(testdataDirectory, "package-test.xtm")));
        tm = reader.read();
        base = tm.getStore().getBaseAddress();
      }
      catch (java.io.IOException e) {
        e.printStackTrace();
        throw new RuntimeException("IMPOSSIBLE ERROR! " + e.getMessage());
      }
    }
  }

  protected void tearDown() {
    //tm = null;
  }
  
}





