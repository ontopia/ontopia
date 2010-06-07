
package net.ontopia.topicmaps.xml.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.xml.XTM2TopicMapWriter;
import net.ontopia.topicmaps.nav.utils.deciders.TMDecider;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.utils.FileUtils;

public class XTM2WriterFilterTestGenerator implements TestCaseGeneratorIF {

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "canonical" + File.separator;

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

    // Return all the test cases that were generated.
    return tests.iterator();
  }

  // --- Test case class

  /**
   * Exports a file from the directory 'filter-in' to an xtm file in
   * 'filter-xtm'. Canonicalizes the xtm file into the directory 'filter-out'.
   * Compares the file in 'filter-out' with a baseline file in
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
      verifyDirectory(base, "filter-xtm2");

      // Path to the input topic map document.
      String in = base + File.separator + "filter-in" + File.separator
          + filename;
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = base + File.separator + "filter-baseline"
          + File.separator + filename + ".cxtm";
      // Path to the exported xtm topic map document.
      String xtm = base + File.separator + "filter-xtm2" + File.separator
          + filename + ".xtm";
      // Path to the output (canonicalized output of exported xtm topic map).
      String out = base + File.separator + "filter-out" + File.separator
          + filename + ".xtm2.cxtm";

      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      // Export document
      XTM2TopicMapWriter xtmWriter = new XTM2TopicMapWriter(xtm);

      // Set this writer to filter out the following topics.
      xtmWriter.setFilter(new TMDecider());

      // Export the topic map to xtm.
      xtmWriter.write(sourceMap);

      // Reimport the exported xtm.
      TopicMapIF xtmMap = ImportExportUtils.getReader(xtm).read();

      // Fix item IDs
      TestUtils.fixItemIds(xtmMap, sourceMap.getStore().getBaseAddress());

      // Canonicalize the reimported xtm.
      (new CanonicalXTMWriter(new FileOutputStream(out))).write(xtmMap);

      // compare results
      assertTrue("canonicalizing the test file " + filename +
                 " into " + out + " is different from " + baseline,
                 FileUtils.compare(out, baseline));
    }
  }
}
