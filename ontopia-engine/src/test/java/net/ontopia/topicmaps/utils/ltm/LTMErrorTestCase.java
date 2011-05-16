
// $Id: LTMErrorTestGenerator.java,v 1.6 2005/03/30 11:45:47 opland Exp $

package net.ontopia.topicmaps.utils.ltm;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ltm.*;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LTMErrorTestCase {

  private final static String testdataDirectory = "ltm";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "error", ".ltm");
  }

    private String filename;
        
    public LTMErrorTestCase(String root, String filename) {
      this.filename = filename;
    }

    @Test
    public void testFile() throws IOException {
      // produce canonical output
      String in = FileUtils.getTestInputFile(testdataDirectory, "error", 
        filename);

      try {
        new LTMTopicMapReader(URIUtils.getURI(in)).read();
        Assert.fail("test file " + filename + " parsed without error");
      } catch (java.io.IOException e) {
      } catch (net.ontopia.topicmaps.core.UniquenessViolationException e) {
      } catch (net.ontopia.utils.OntopiaRuntimeException e) {
      }
    }
}
