
package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.utils.deciders.TMDecider;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.FileUtils;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TMXMLWriterFilterTestCase {

  protected boolean recanonicalizeSource = false;

  private final static String testdataDirectory = "tmxmlWriter";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "filter-in", ".ltm|.rdf|.xtm");
  }

  // --- Test case class

  /**
   * Exports a file from the directory 'filter-in' to an TMXML file in
   * 'filter-tmxml'. Canonicalizes the TMXML file into the directory
   * 'filter-out'. Compares the file in 'filter-out' with a baseline file in
   * 'filter-baseline'. The baseline must be created manually, or by inspecting
   * the file in 'filter-out'.
   * @throws IOException
   */
    private String base;
    private String filename;

    public TMXMLWriterFilterTestCase(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testFile() throws IOException {
      FileUtils.verifyDirectory(base, "filter-out");
      FileUtils.verifyDirectory(base, "filter-tmxml");

      // Path to the input topic map document.
      String in = FileUtils.getTestInputFile(testdataDirectory, "filter-in", 
          filename);
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = FileUtils.getTestInputFile(testdataDirectory, "filter-baseline", 
          filename + ".cxtm");
      // Path to the exported TMXML topic map document.
      String tmxml = base + File.separator + "filter-tmxml" + File.separator
          + filename + ".xml";
      // Path to the output (canonicalized output of exported tmxml topic map).
      String out = base + File.separator + "filter-out" + File.separator
          + filename + ".xml.cxtm";

      // Import topic map from arbitrary source
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      // Export the topic map to TMXML
      TMXMLWriter tmxmlWriter = new TMXMLWriter(tmxml);
      tmxmlWriter.setFilter(new TMDecider());
      tmxmlWriter.write(sourceMap);

      // Reimport the exported TMXML
      TopicMapIF tmxmlMap = ImportExportUtils.getReader(tmxml).read();

      // Canonicalize the reimported TMXML
      (new CanonicalXTMWriter(new FileOutputStream(out))).write(tmxmlMap);

      // compare results
      Assert.assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the tmxml export: "
          + out + " " + baseline, FileUtils.compareFileToResource(out, baseline));
    }
}
