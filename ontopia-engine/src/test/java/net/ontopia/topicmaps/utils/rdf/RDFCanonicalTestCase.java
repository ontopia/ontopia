
package net.ontopia.topicmaps.utils.rdf;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RDFCanonicalTestCase {

  private final static String testdataDirectory = "rdf";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", ".rdf|.n3|.nt");
  }

  // --- Canonical test case class

    private String base;
    private String filename;
    private String syntax;
        
    public RDFCanonicalTestCase(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
      this.syntax = 
        (filename.endsWith(".n3")) ? "N3" :
        (filename.endsWith(".nt")) ? "N-TRIPLE" :
        null;
    }

    @Test
    public void testFile() throws IOException {
      FileUtils.verifyDirectory(base, "out");
      
      // produce canonical output
      String in = FileUtils.getTestInputFile(testdataDirectory, "in", filename);
      String out = base + File.separator + "out" + File.separator + filename;

      TopicMapIF source = new RDFTopicMapReader(URIUtils.getURI(in), syntax).read();
      new CanonicalTopicMapWriter(out).write(source);

      // compare results
      String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline", filename);
      Assert.assertTrue("test file " + filename + " canonicalized wrongly",
             FileUtils.compareFileToResource(out, baseline));
    }

}
