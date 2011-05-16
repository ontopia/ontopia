
// $Id: TMXMLWriterTestGenerator.java,v 1.16 2008/12/04 11:32:29 lars.garshol Exp $

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
public class TMXMLWriterGeneralTestCase {

  protected boolean recanonicalizeSource = false;

  private final static String testdataDirectory = "tmxmlWriter";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm|.rdf|.xtm");
  }

  // --- Test case class

    private String base;
    private String filename;

    public TMXMLWriterGeneralTestCase(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    /**
     * Exports a file from the directory 'in' to a TM/XML file in
     * 'tmxml'. Canonicalizes the TMXML file into the directory 'out'.
     * Compares the file in 'out' with a baseline file in 'baseline'.
     * If recanonicalizeSource is set to true, then the source file is
     * also canonicalized directly into 'baseline' and used as
     * baseline.
     */
    @Test
    public void testFile() throws IOException {
      FileUtils.verifyDirectory(base, "out");
      FileUtils.verifyDirectory(base, "tmxml");

      // Path to the input topic map document.
      String in = FileUtils.getTestInputFile(testdataDirectory, "in", filename);
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline", 
        filename + ".cxtm");
      // Path to the exported tmxml topic map document.
      String tmxml = base + File.separator + "tmxml" + File.separator + filename
          + ".xml";
      // Path to the output (canonicalized output of exported tmxml topic map).
      String out = base + File.separator + "out" + File.separator + filename
          + ".xml.cxtm";

      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      if (recanonicalizeSource) {
        // Canonicalize the source topic map.
        FileOutputStream fos = new FileOutputStream(baseline);
        (new CanonicalXTMWriter(fos)).write(sourceMap);
        fos.close();
      }

      // Export the topic map to tmxml.
      TopicMapWriterIF writer = new TMXMLWriter(tmxml);
      writer.write(sourceMap);

      // Reimport the exported tmxml.
      TopicMapIF tmxmlMap = ImportExportUtils.getReader(tmxml).read();

      // Canonicalize the reimported tmxml.
      FileOutputStream fos = new FileOutputStream(out);
      (new CanonicalXTMWriter(fos)).write(tmxmlMap);
      fos.close();

      // compare results
      Assert.assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the tmxml export: "
          + out + " " + baseline, FileUtils.compareFileToResource(out, baseline));
    }

}
