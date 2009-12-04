package net.ontopia.topicmaps.utils.jtm.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.utils.jtm.JTMTopicMapReader;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.utils.FileUtils;

public class JTMTestGenerator implements TestCaseGeneratorIF {

  @SuppressWarnings("unchecked")
  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "jtm" + File.separator;

    // Create test cases from each topic map file in 'in'.
    File indir = new File (base + "in" + File.separator);
    File[] infiles = indir.listFiles ();
    if (infiles != null)
      for (int i = 0; i < infiles.length; i++) {
        String name = infiles[i].getName ();
        if (name.endsWith (".jtm") && !name.contains("detached"))
          tests.add (new JTMReaderTestCase (name, base));
      }

    // Return all the test cases that were generated.
    return tests.iterator();
  }

  // --- Test case class

  public class JTMReaderTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;

    public JTMReaderTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    /**
     * Canonicalizes the jtm file into the directory 'out'. Compares the file in
     * 'out' with a baseline file in 'baseline'.
     */
    public void testFile() throws IOException {
      verifyDirectory(base, "out");

      // Path to the input topic map document.
      String in = base + File.separator + "in" + File.separator + filename;
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = base + File.separator + "baseline" + File.separator
          + filename + ".cxtm";
      // Path to the output (canonicalized output of exported jtm topic map).
      String out = base + File.separator + "out" + File.separator + filename
          + ".cxtm";

      TopicMapIF jtmMap = new JTMTopicMapReader(new File(in)).read();

      // Canonicalize the imported jtm.
      FileOutputStream fos = new FileOutputStream(out);
      (new CanonicalXTMWriter(fos)).write(jtmMap);
      fos.close();

      // compare results
      assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the jtm export of "
          + filename + ".", FileUtils.compare(out, baseline));
    }
  }
}
