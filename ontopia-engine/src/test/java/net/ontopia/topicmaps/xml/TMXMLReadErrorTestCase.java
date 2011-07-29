
package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.runners.Parameterized.Parameters;

public class TMXMLReadErrorTestCase extends AbstractCanonicalTests {

  private final static String testdataDirectory = "tmxml";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "invalid", ".xml");
  }

  protected String getFileDirectory() {
    return "invalid";
  }

  protected void canonicalize(String infile, String outfile) {
    // not used
  }
  
  // --- Test case class

    public TMXMLReadErrorTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
      this._testdataDirectory = testdataDirectory;
    }

    public void testFile() throws IOException {
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "invalid", filename);
      TMXMLReader reader = new TMXMLReader(in);

      try {
        reader.read();
        Assert.fail("succeeded in importing bad file " + filename);
      } catch (IOException e) {
        // ok
      } catch (OntopiaRuntimeException e) {
        if (!(e.getCause() instanceof org.xml.sax.SAXParseException))
          throw e;
      }
    }
  
}
