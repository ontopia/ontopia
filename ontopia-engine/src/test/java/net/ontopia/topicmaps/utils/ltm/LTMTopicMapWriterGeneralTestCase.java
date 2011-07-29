
package net.ontopia.topicmaps.utils.ltm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.utils.deciders.TMDecider;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LTMTopicMapWriterGeneralTestCase {
  protected boolean recanonicalizeSource = false;

  private final static String testdataDirectory = "ltmWriter";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm|.rdf|.xtm");
  }

  // --- Test case class

    private String base;
    private String filename;

    public LTMTopicMapWriterGeneralTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    /**
     * Exports a file from the directory 'in' to an ltm file in 'ltm'.
     * Canonicalizes the ltm file into the directory 'out'. Compares the file in
     * 'out' with a baseline file in 'baseline'. If recanonicalizeSource is set
     * to true, then the source file is also canonicalized directly into
     * 'baseline' and used as baseline.
     */
    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
      TestFileUtils.verifyDirectory(base, "ltm");

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline",
          filename + ".cxtm");
      // Path to the exported ltm topic map document.
      String ltm = base + File.separator + "ltm" + File.separator + filename
          + ".ltm";
      // Path to the output (canonicalized output of exported ltm topic map).
      String out = base + File.separator + "out" + File.separator + filename
          + ".cxtm";

      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      if (recanonicalizeSource) {
        // Canonicalize the source topic map.
        FileOutputStream fos = new FileOutputStream(baseline);
        (new CanonicalXTMWriter(fos))
            .write(sourceMap);
        fos.close();
      }

      // Export the topic map to ltm.
      FileOutputStream fos = new FileOutputStream(ltm);
      (new LTMTopicMapWriter(fos)).write(sourceMap);
      fos.close();

      // Reimport the exported ltm.
      TopicMapIF ltmMap = ImportExportUtils.getReader(ltm).read();

      // Canonicalize the reimported ltm.
      fos = new FileOutputStream(out);
      (new CanonicalXTMWriter(fos)).write(ltmMap);
      fos.close();

      // compare results
      Assert.assertTrue("canonicalizing the test file " + filename +
          " produces " + out + " which is different from " +
          baseline, FileUtils.compareFileToResource(out, baseline));
    }

}
