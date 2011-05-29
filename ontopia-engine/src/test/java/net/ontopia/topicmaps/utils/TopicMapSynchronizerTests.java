
package net.ontopia.topicmaps.utils;

import java.io.*;
import java.util.List;
import java.util.Set;
import net.ontopia.utils.FileUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TopicMapSynchronizerTests {

  private final static String testdataDirectory = "tmsync";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", "-target.ltm");
  }

  // --- Test case class

    private String base;
    private String root;
    private String filename;

    public TopicMapSynchronizerTests(String root, String filename) {
      this.root = root;
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testFile() throws IOException {
      FileUtils.verifyDirectory(base, "out");

      String suffix = "-target.ltm";

      // setup canonicalization filenames
      String in1 = FileUtils.getTestInputFile(testdataDirectory, "in" ,filename);
      String testname =
        filename.substring(0, filename.length() - suffix.length());
      String in2 = FileUtils.getTestInputFile(testdataDirectory, "in", testname + "-source.ltm");
      String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline", filename);

      String out = base + File.separator + "out" + File.separator + filename;

      // produce canonical output
      canonicalize(in1, in2, out);

      // compare results
      Assert.assertTrue("test file " + filename + " canonicalized wrongly",
                 FileUtils.compareFileToResource(out, baseline));
    }

    private void canonicalize(String infile1, String infile2, String outfile)
      throws IOException {
      TopicMapIF target = ImportExportUtils.getReader(infile1).read();
      TopicMapIF source = ImportExportUtils.getReader(infile2).read();

      LocatorIF base = source.getStore().getBaseAddress();
      TopicIF sourcet = (TopicIF)
        source.getObjectByItemIdentifier(base.resolveAbsolute("#source"));

      TopicMapSynchronizer.update(target, sourcet);

      FileOutputStream out = new FileOutputStream(outfile);
      new CanonicalXTMWriter(out).write(target);
      out.close();
    }
}
