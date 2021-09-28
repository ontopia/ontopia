/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.xml;

import java.io.IOException;
import net.ontopia.utils.OntopiaRuntimeException;
import java.util.List;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.Assert;
import org.junit.runners.Parameterized.Parameters;

public class XTMReadErrorTests extends AbstractCanonicalTests {

  private final static String testdataDirectory = "canonical";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "errors", ".xtm");
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
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
      this._testdataDirectory = testdataDirectory;
    }

    public void testFile() throws IOException {
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "errors", filename);
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
