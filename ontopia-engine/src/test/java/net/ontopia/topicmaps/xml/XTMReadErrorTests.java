
package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.Assert;
import org.junit.runners.Parameterized.Parameters;

public class XTMReadErrorTests extends AbstractCanonicalTests {

  private final static String testdataDirectory = "canonical";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "errors", ".xtm");
  }
  
  protected String getFileDirectory() {
    return "errors";
  }
  
  protected void canonicalize(String infile, String outfile) {
    // not used
  }
  
  // --- Test case class

    public XTMReadErrorTests(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
      this._testdataDirectory = testdataDirectory;
    }

    public void testFile() throws IOException {
      String in = FileUtils.getTestInputFile(testdataDirectory, "errors", filename);
      XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(in));

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
