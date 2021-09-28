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

public class InvalidXTM21ReaderValidatingTestCase extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "xtm21";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "invalid", ".xtm");
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
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
      this._testdataDirectory = testdataDirectory;
    }

    public void testFile() throws IOException {
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "invalid", filename);
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
