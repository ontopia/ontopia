// $Id: MergeToXTMTestGenerator.java,v 1.5 2006/01/19 12:35:35 grove Exp $

package net.ontopia.topicmaps.utils;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MergeToXTMTestCase {
    
  private final static String testdataDirectory = "merge";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", ".xtm");
  }

  // --- Test case class

    private String base;
    private String filename;
        
    public MergeToXTMTestCase(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testMergeToXTM() throws IOException {
      FileUtils.verifyDirectory(base, "out");
      FileUtils.verifyDirectory(base, "tmp");

      // load
      String in = FileUtils.getTestInputFile(testdataDirectory, "in", filename);
      String in2 = FileUtils.getTestInputFile(testdataDirectory, "in", 
        filename.substring(0, filename.length() - 3) + "sub");
      String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline", filename);
      TopicMapIF source1 = new XTMTopicMapReader(URIUtils.getURI(in)).read();
      TopicMapIF source2 = new XTMTopicMapReader(URIUtils.getURI(in2)).read();

      // merge
      MergeUtils.mergeInto(source1, source2);
      
      // produce XTM output
      String tmp = base + File.separator + "tmp" + File.separator + filename;
      new XTMTopicMapWriter(tmp).write(source1);

      // reload and write canonically
      String out = base + File.separator + "out" + File.separator + filename;
      source1 = new XTMTopicMapReader(new File(tmp)).read();
      new CanonicalTopicMapWriter(out).write(source1);

      // compare results
      Assert.assertTrue("test file " + filename + " canonicalized wrongly",
             FileUtils.compareFileToResource(out, baseline));
    }
}




