
package net.ontopia.topicmaps.utils.ctm;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

import java.util.List;
import net.ontopia.utils.URIUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CTMInvalidTestCase {
  
  private final static String testdataDirectory = "ctm";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "invalid", ".ctm");
  }

    private String filename;
        
    public CTMInvalidTestCase(String root, String filename) {
      this.filename = filename;
    }

    @Test
    public void testFile() throws IOException {
      // produce canonical output
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "invalid", 
        filename);

      try {
        new CTMTopicMapReader(URIUtils.getURI(in)).read();
        Assert.fail("no error in reading " + filename);
      } catch (IOException e) {
      } catch (InvalidTopicMapException e) {
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Error reading: " + in, e);
      }
    }
}
