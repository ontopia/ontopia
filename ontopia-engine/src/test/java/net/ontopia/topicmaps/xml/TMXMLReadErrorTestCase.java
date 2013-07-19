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
