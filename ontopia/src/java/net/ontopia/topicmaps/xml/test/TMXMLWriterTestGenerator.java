
// $Id: TMXMLWriterTestGenerator.java,v 1.16 2008/12/04 11:32:29 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.xml.TMXMLWriter;
import net.ontopia.topicmaps.nav.utils.deciders.TMDecider;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.utils.FileUtils;

public class TMXMLWriterTestGenerator implements TestCaseGeneratorIF {

  protected boolean recanonicalizeSource = true;

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "tmxmlWriter" + File.separator;

    // Create test cases from each topic map file in 'in'.
    File indir = new File (base + "in" + File.separator);
    File[] infiles = indir.listFiles ();
    if (infiles != null)
      for (int i = 0; i < infiles.length; i++) {
        String name = infiles[i].getName();
        if (name.endsWith (".ltm") ||
            name.endsWith (".rdf") ||
            name.endsWith (".xtm"))
          tests.add(new GeneralTestCase(name, base));
      }

    // Create test cases from each topic map file in 'filter-in'.
    File filterIndir = new File(base + "filter-in" + File.separator);
    File[] filterInfiles = filterIndir.listFiles();
    if (filterInfiles != null)
      for (int i = 0; i < filterInfiles.length; i++) {
        String name = filterInfiles[i].getName();
        if (name.endsWith(".ltm") ||
            name.endsWith(".rdf") ||
            name.endsWith(".xtm"))
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
     * Exports a file from the directory 'in' to a TM/XML file in
     * 'tmxml'. Canonicalizes the TMXML file into the directory 'out'.
     * Compares the file in 'out' with a baseline file in 'baseline'.
     * If recanonicalizeSource is set to true, then the source file is
     * also canonicalized directly into 'baseline' and used as
     * baseline.
     */
    public void testFile() throws IOException {
      verifyDirectory(base, "out");
      verifyDirectory(base, "tmxml");

      // Path to the input topic map document.
      String in = base + "in" + File.separator + filename;
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = base + "baseline" + File.separator
          + filename + ".cxtm";
      // Path to the exported tmxml topic map document.
      String tmxml = base + "tmxml" + File.separator + filename
          + ".xml";
      // Path to the output (canonicalized output of exported tmxml topic map).
      String out = base + "out" + File.separator + filename
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
      assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the tmxml export: "
          + out + " " + baseline, FileUtils.compare(out, baseline));
    }
  }

  /**
   * Exports a file from the directory 'x-in' to an TMXML file in
   * 'x-tmxml'.  Canonicalizes the tmxml file into the directory
   * 'x-out'. Compares the file in 'x-out' with a baseline file in
   * 'x-baseline'. The baseline must be created manually, or by
   * inspecting the file in 'out'.
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
      verifyDirectory(base, "x-tmxml");

      // Path to the input topic map document.
      String in = base + "x-in" + File.separator + filename;
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = base + "x-baseline" + File.separator
          + filename + ".cxtm";
      // Path to the exported TMXML topic map document.
      String tmxml = base + "x-tmxml" + File.separator +
          filename + ".xml";
      // Path to the output (canonicalized output of exported tmxml topic map).
      String out = base + "x-out" + File.separator + filename
          + ".xml.cxtm";

      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      // Export the topic map to TMXML.
      TMXMLWriter tmxmlWriter = new TMXMLWriter(tmxml);
      tmxmlWriter.write(sourceMap);

      // Reimport the exported TMXML.
      TopicMapIF tmxmlMap = ImportExportUtils.getReader(tmxml).read();

      // Canonicalize the reimported TMXML.
      (new CanonicalXTMWriter(new FileOutputStream(out))).write(tmxmlMap);

      // compare results
      assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the tmxml export of "
          + out + " " + baseline, FileUtils.compare(out, baseline));
    }
  }

  /**
   * Exports a file from the directory 'filter-in' to an TMXML file in
   * 'filter-tmxml'. Canonicalizes the TMXML file into the directory
   * 'filter-out'. Compares the file in 'filter-out' with a baseline file in
   * 'filter-baseline'. The baseline must be created manually, or by inspecting
   * the file in 'filter-out'.
   * @throws IOException
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
      verifyDirectory(base, "filter-tmxml");

      // Path to the input topic map document.
      String in = base + "filter-in" + File.separator
          + filename;
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = base + "filter-baseline"
          + File.separator + filename + ".cxtm";
      // Path to the exported TMXML topic map document.
      String tmxml = base + "filter-tmxml" + File.separator
          + filename + ".xml";
      // Path to the output (canonicalized output of exported tmxml topic map).
      String out = base + "filter-out" + File.separator
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
      assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the tmxml export: "
          + out + " " + baseline, FileUtils.compare(out, baseline));
    }
  }
}
