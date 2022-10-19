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

package net.ontopia.topicmaps.utils.rdf;

import java.io.IOException;
import java.util.List;
import net.ontopia.utils.TestFileUtils;
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
    return TestFileUtils.getTestInputFiles(testdataDirectory, "err", ".rdf");
  }

  // --- Error test case class

    private String filename;
        
    public RDFErrorTestCase(String root, String filename) {
      this.filename = filename;
    }

    @Test
    public void testFile() throws IOException {
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "err", filename);

      try {
        new RDFTopicMapReader(TestFileUtils.getTestInputURL(in)).read();
        Assert.fail("Read in '" + filename + "' with no errors");
      } catch (RDFMappingException e) {
      }
    }

}
