
package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.runners.Parameterized.Parameters;

/**
 * INTERNAL: Test case generator based on the cxtm-tests external test
 * suite, thus relying on the download-tmxml ant build target.
 */
public class CanonicalTMXMLReaderTestCase extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "tmxml";

  public CanonicalTMXMLReaderTestCase(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".xml");
  }

  // --- Canonicalization type methods

  // this is actually the file name of the baseline file
  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }
  
  protected boolean filter(String filename) {
    return filename.endsWith(".xml");
  }

  protected void canonicalize(String infile, String outfile)
    throws IOException {
    TMXMLReader reader = new TMXMLReader(infile);
    reader.setValidate(true); // we do want to validate
    TopicMapIF source = reader.read();

    FileOutputStream fos = new FileOutputStream(outfile);
    CanonicalXTMWriter cwriter = new CanonicalXTMWriter(fos);
    cwriter.write(source);

    fos.close();
    source.getStore().close();
  }  
}
