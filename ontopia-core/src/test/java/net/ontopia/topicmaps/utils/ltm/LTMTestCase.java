
// $Id: LTMTestGenerator.java,v 1.11 2006/01/19 12:35:35 grove Exp $

package net.ontopia.topicmaps.utils.ltm;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.ltm.*;
import net.ontopia.utils.FileUtils;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LTMTestCase {

  /**
    * @return true iff the test-case in fileName was added to test features
    * after LTM1.3 was implemented.
    */
  public static boolean ltm13(String fileName) {
    if (fileName.endsWith("-1.3.ltm")) return true;
    return false;
  }
  
  private final static String testdataDirectory = "ltm";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm");
  }

    private String base;
    private String filename;
        
    public LTMTestCase(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testFile() throws IOException {
      FileUtils.verifyDirectory(base, "out");
      
      // produce canonical output
      String in = FileUtils.getTestInputFile(testdataDirectory, "in", 
        filename);
      String out = base + File.separator + "out" + File.separator +
        filename;
      
      TopicMapIF source = new LTMTopicMapReader(URIUtils.getURI(in)).read();
      
      if (ltm13(filename)) {
        out += ".cxtm";
        new CanonicalXTMWriter(new FileOutputStream(out)).write(source);
  
        // compare results
        String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline",
          filename + ".cxtm");
        Assert.assertTrue("test file " + filename + " canonicalized wrongly",
              FileUtils.compareFileToResource(out, baseline));
      } else {
        new CanonicalTopicMapWriter(out).write(source);
  
        // compare results
        String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline",
          filename);
        Assert.assertTrue("test file " + filename + " canonicalized wrongly",
              FileUtils.compareFileToResource(out, baseline));
      }
    }
}
