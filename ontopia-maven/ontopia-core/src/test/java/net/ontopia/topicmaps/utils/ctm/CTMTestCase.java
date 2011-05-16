
// $Id: CTMTestGenerator.java,v 1.2 2009/02/12 11:52:17 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.ctm.*;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
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
public class CTMTestCase {

  private final static String testdataDirectory = "ctm";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", ".ctm");
  }

    private String base;
    private String filename;
        
    public CTMTestCase(String root, String filename) {
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

      TopicMapIF source = null;
      try {
        source = new CTMTopicMapReader(URIUtils.getURI(in)).read();
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Error in " + in, e);
      }

      DuplicateSuppressionUtils.removeDuplicates(source);
      try {
        new CanonicalXTMWriter(new FileOutputStream(out)).write(source);
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Error in " + in, e);
      }
  
      // compare results
      String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline", 
        filename + ".cxtm");
      Assert.assertTrue("test file " + filename + " canonicalized wrongly",
                 FileUtils.compareFileToResource(out, baseline));
    }

}
