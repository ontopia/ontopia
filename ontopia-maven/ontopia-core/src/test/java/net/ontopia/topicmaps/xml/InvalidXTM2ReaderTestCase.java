
// $Id: InvalidXTM2ReaderTestGenerator.java,v 1.1 2008/04/23 11:43:45 lars.garshol Exp $

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

public class InvalidXTM2ReaderTestCase extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "xtm2";

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

    public InvalidXTM2ReaderTestCase(String root, String filename) {
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
