
package net.ontopia.topicmaps.utils.rdf;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RDFErrorTestCase {

  private final static String testdataDirectory = "rdf";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "err", ".rdf");
  }

  // --- Error test case class

    private String filename;
        
    public RDFErrorTestCase(String root, String filename) {
      this.filename = filename;
    }

    @Test
    public void testFile() throws IOException {
      String in = FileUtils.getTestInputFile(testdataDirectory, "err", filename);

      try {
        new RDFTopicMapReader(URIUtils.getURI(in)).read();
        Assert.fail("Read in '" + filename + "' with no errors");
      } catch (RDFMappingException e) {
      }
    }

}
