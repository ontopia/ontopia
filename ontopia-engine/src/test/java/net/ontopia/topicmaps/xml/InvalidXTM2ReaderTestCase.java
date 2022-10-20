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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class InvalidXTM2ReaderTestCase extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "xtm2";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getFilteredTestInputURLs(".xtm", testdataDirectory, "invalid");
  }
  
  // --- Canonicalization type methods

  @Override
  protected void canonicalize(URL infile, File outfile)
    throws IOException {
    // not used, since we are not canonicalizing
  }

  @Override
  protected String getFileDirectory() {
    return "invalid";
  }

  @Override
  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }

  // --- Test case class

    public InvalidXTM2ReaderTestCase(URL inputFile, String filename) {
      this.filename = filename;
      this.inputFile = inputFile;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
      this._testdataDirectory = testdataDirectory;
    }

    @Override
    @Test
    public void testFile() throws IOException {
      XTMTopicMapReader reader = new XTMTopicMapReader(inputFile);
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
        if (e.getCause() instanceof ConstraintViolationException) {
          return;
        }
        if (!(e.getCause() instanceof org.xml.sax.SAXParseException)) {
          throw e;
        }
      }
    }
}
