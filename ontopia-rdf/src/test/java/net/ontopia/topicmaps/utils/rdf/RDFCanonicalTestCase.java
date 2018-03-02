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

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RDFCanonicalTestCase {

  private final static String testdataDirectory = "rdf";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".rdf|.n3|.nt");
  }

  // --- Canonical test case class

    private String base;
    private String filename;
    private String syntax;
        
    public RDFCanonicalTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
      this.syntax = 
        (filename.endsWith(".n3")) ? "N3" :
        (filename.endsWith(".nt")) ? "N-TRIPLE" :
        null;
    }

    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
      
      // produce canonical output
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      File out = new File(base + File.separator + "out" + File.separator + filename);

      TopicMapIF source = new RDFTopicMapReader(TestFileUtils.getTestInputURL(in), syntax).read();
      new CanonicalTopicMapWriter(out).write(source);

      // compare results
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", filename);
      Assert.assertTrue("test file " + filename + " canonicalized wrongly",
             TestFileUtils.compareFileToResource(out, baseline));
    }

}
