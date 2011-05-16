
package net.ontopia.topicmaps.utils.ltm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.nav.utils.deciders.TMDecider;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.utils.FileUtils;

public class LTMTopicMapWriterTestGenerator implements TestCaseGeneratorIF {
  protected boolean recanonicalizeSource = false;

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "ltmWriter" + File.separator;

    // Create test cases from each topic map file in 'in'.
    File indir = new File (base + "in" + File.separator);
    File[] infiles = indir.listFiles ();
    if (infiles != null)
      for (int i = 0; i < infiles.length; i++) {
        String name = infiles[i].getName ();
        if (name.endsWith (".ltm")
            || name.endsWith (".rdf") || name.endsWith (".xtm"))
          tests.add (new GeneralTestCase (name, base));
      }

    // Create test cases from each topic map file in 'filter-in'.
    File filterIndir = new File(base + "filter-in" + File.separator);
    File[] filterInfiles = filterIndir.listFiles();
    if (filterInfiles != null)
      for (int i = 0; i < filterInfiles.length; i++) {
        String name = filterInfiles[i].getName();
        if (name.endsWith(".ltm")
            || name.endsWith(".rdf") || name.endsWith(".xtm"))
          tests.add(new FilterTestCase(name, base));
      }

    // Create test cases from each topic map file in 'x-in'.
    File xIndir = new File (base + "x-in" + File.separator);
    File[] xInfiles = xIndir.listFiles ();
    if (xInfiles != null)
      for (int i = 0; i < xInfiles.length; i++) {
        String name = xInfiles[i].getName ();
        if (name.endsWith (".ltm")
            || name.endsWith (".rdf") || name.endsWith (".xtm"))
          tests.add (new SpecialTestCase (name, base));
      }

    // Return all the test cases that were generated.
    return tests.iterator();
  }

  // --- Test case class

  public class GeneralTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;

    public GeneralTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    /**
     * Exports a file from the directory 'in' to an ltm file in 'ltm'.
     * Canonicalizes the ltm file into the directory 'out'. Compares the file in
     * 'out' with a baseline file in 'baseline'. If recanonicalizeSource is set
     * to true, then the source file is also canonicalized directly into
     * 'baseline' and used as baseline.
     */
    public void testFile() throws IOException {
      verifyDirectory(base, "out");
      verifyDirectory(base, "ltm");

      // Path to the input topic map document.
      String in = base + File.separator + "in" + File.separator + filename;
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = base + File.separator + "baseline" + File.separator
          + filename + ".cxtm";
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
      assertTrue("canonicalizing the test file " + filename +
          " produces " + out + " which is different from " +
          baseline, FileUtils.compare(out, baseline));
    }
  }

  /**
   * Exports a file from the directory 'x-in' to an ltm file in 'x-ltm'.
   * Canonicalizes the ltm file into the directory 'x-out'. Compares the file in
   * 'x-out' with a baseline file in 'x-baseline'. The baseline must be created
   * manually, or by inspecting the file in 'out'.
   */
  public class SpecialTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;

    public SpecialTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public void testFile() throws IOException {
      verifyDirectory(base, "x-out");
      verifyDirectory(base, "x-ltm");

      // Path to the input topic map document.
      String in = base + File.separator + "x-in" + File.separator + filename;
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = base + File.separator + "x-baseline" + File.separator
          + filename + ".cxtm";
      // Path to the exported ltm topic map document.
      String ltm = base + File.separator + "x-ltm" + File.separator + filename
          + ".ltm";
      // Path to the output (canonicalized output of exported ltm topic map).
      String out = base + File.separator + "x-out" + File.separator + filename
          + ".cxtm";

      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      // Export the topic map to ltm.
      LTMTopicMapWriter ltmWriter = new LTMTopicMapWriter(new FileOutputStream(
          ltm));
      ltmWriter.setPreserveIds(!filename.startsWith("generateId-"));
      ltmWriter.write(sourceMap);

      // Reimport the exported ltm.
      TopicMapIF ltmMap = ImportExportUtils.getReader(ltm).read();

      // Canonicalize the reimported ltm.
      FileOutputStream fos = new FileOutputStream(out);
      (new CanonicalXTMWriter(fos)).write(ltmMap);
      fos.close();

      // compare results
      assertTrue("canonicalizing the test file " + filename +
          " produces " + out + " which is different from " +
          baseline, FileUtils.compare(out, baseline));
    }
  }

  /**
   * Exports a file from the directory 'filter-in' to an ltm file in
   * 'filter-ltm'. Canonicalizes the ltm file into the directory 'filter-out'.
   * Compares the file in 'filter-out' with a baseline file in
   * 'filter-baseline'. The baseline must be created manually, or by inspecting
   * the file in 'filter-out'.
   */
  public class FilterTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;

    public FilterTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public void testFile() throws IOException {
      verifyDirectory(base, "filter-out");
      verifyDirectory(base, "filter-ltm");

      // Path to the input topic map document.
      String in = base + File.separator + "filter-in" + File.separator
          + filename;
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = base + File.separator + "filter-baseline"
          + File.separator + filename + ".cxtm";
      // Path to the exported ltm topic map document.
      String ltm = base + File.separator + "filter-ltm" + File.separator
          + filename + ".ltm";
      // Path to the output (canonicalized output of exported ltm topic map).
      String out = base + File.separator + "filter-out" + File.separator
          + filename + ".cxtm";

      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      FileOutputStream fos = new FileOutputStream(ltm);
      LTMTopicMapWriter ltmWriter = new LTMTopicMapWriter(fos);

      // Set this writer to filter out the following topics.
      TMDecider tmFilter = new TMDecider();
      ltmWriter.setFilter(tmFilter);

      ltmWriter.setPreserveIds(!filename.startsWith("generateId-"));

      // Export the topic map to ltm.
      ltmWriter.write(sourceMap);
      fos.close();

      // Reimport the exported ltm.
      TopicMapIF ltmMap = ImportExportUtils.getReader(ltm).read();

      // Canonicalize the reimported ltm.
      fos = new FileOutputStream(out);
      (new CanonicalXTMWriter(fos)).write(ltmMap);
      fos.close();

      // compare results
      assertTrue("canonicalizing the test file " + filename +
          " produces " + out + " which is different from " +
          baseline, FileUtils.compare(out, baseline));
    }
  }
}
