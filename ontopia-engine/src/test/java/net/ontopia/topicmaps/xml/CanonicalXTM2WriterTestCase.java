
package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.ImportExportUtils;

import java.util.List;
import net.ontopia.utils.URIUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CanonicalXTM2WriterTestCase {

  private final static String testdataDirectory = "xtm2";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".xtm");
  }

  // --- Test case class

    private String base;
    private String filename;
      
    public CanonicalXTM2WriterTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }
  
    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
   
      // Path to the input topic map
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      // Path to the baseline
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", 
        filename + ".cxtm");
      // Path to the canonicalized output.
      String out = base + File.separator + "out" + File.separator 
        + "tmp-" + filename + ".cxtm";
      // Path to the temporary file
      String tmp = base + File.separator + "out" + File.separator 
        + "tmp-" + filename;
  
      // Import topic map from arbitrary source.
      TopicMapIF tm = new XTMTopicMapReader(URIUtils.getURI(in)).read();
      LocatorIF base = tm.getStore().getBaseAddress();

      // Export to XTM 2.0
      XTMTopicMapWriter writer = new XTMTopicMapWriter(tmp);
      writer.setVersion(XTMVersion.XTM_2_0);
      // Do not omit the item identifiers
      writer.setExportSourceLocators(true);
      writer.write(tm);

      // Import again from exported file
      tm = ImportExportUtils.getReader(tmp).read();

      // Fix item identifiers for canonicalization
      TestUtils.fixItemIds(tm, base);

      // Output CXTM
      FileOutputStream os = new FileOutputStream(out);
      new CanonicalXTMWriter(os).write(tm);
      os.close();
      
      // compare results
      Assert.assertTrue("The test file " + filename + " is different from the baseline: " + out + " " + baseline,
              FileUtils.compareFileToResource(out, baseline));
    }
}
