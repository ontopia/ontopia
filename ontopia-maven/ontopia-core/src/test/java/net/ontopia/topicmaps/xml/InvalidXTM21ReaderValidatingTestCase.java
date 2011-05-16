
// $Id: InvalidXTM21ReaderTestGenerator.java 1099 2010-06-06 12:42:25Z lars.heuer $

package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.Assert;
import org.junit.runners.Parameterized.Parameters;

public class InvalidXTM21ReaderValidatingTestCase extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "xtm21";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "invalid", ".xtm");
  }

  // --- Canonicalization type methods

  protected void canonicalize(String infile, String outfile)
    throws IOException {
    // not used, since we are not canonicalizing
  }

  protected String getFileDirectory() {
    return "invalid";
  }

  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }

  // --- Test case class

    public InvalidXTM21ReaderValidatingTestCase(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
      this._testdataDirectory = testdataDirectory;
    }

    public void testFile() throws IOException {
      String in = FileUtils.getTestInputFile(testdataDirectory, "invalid", filename);
      XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(in));
      reader.setValidation(true);
      // FIXME: should we do a setXTM2Required(true) or something?

      try {
        reader.read();
        Assert.fail("Reader accepted invalid topic map: " + filename);
      } catch (InvalidTopicMapException e) {
        // goodie
      } catch (IOException e) {
        // ok
      } catch (OntopiaRuntimeException e) {
        if (!(e.getCause() instanceof org.xml.sax.SAXParseException))
          throw e;
      }
    }
}
