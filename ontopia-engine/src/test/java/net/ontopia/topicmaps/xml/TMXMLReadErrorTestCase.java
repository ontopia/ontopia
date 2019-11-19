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
import java.net.URL;
import java.util.List;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TMXMLReadErrorTestCase {

  private final static String testdataDirectory = "tmxml";

  private final String filename;
  private final URL inputFile;

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getFilteredTestInputURLs(".xml", testdataDirectory, "invalid");
  }

  public TMXMLReadErrorTestCase(URL inputFile, String filename) {
    this.filename = filename;
    this.inputFile = inputFile;
  }

  @Test
  public void testFile() throws IOException {
    TMXMLReader reader = new TMXMLReader(inputFile);

    try {
      reader.read();
      Assert.fail("succeeded in importing bad file " + filename);
    } catch (IOException e) {
      // ok
    } catch (OntopiaRuntimeException e) {
      if (!(e.getCause() instanceof org.xml.sax.SAXParseException)) {
        throw e;
      }
    }
  }
}
