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
import java.io.FileOutputStream;
import java.io.IOException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CanonicalXTMWriterTestCase {

  private final static String testdataDirectory = "cxtm";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm|.xtm");
  }

    protected String base;
    protected String filename;
      
    public CanonicalXTMWriterTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }
  
    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
   
      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      // Path to the baseline
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", 
        filename + ".cxtm");
      // Path to the canonicalized output.
      String out = base + File.separator + "out" + File.separator 
        + filename + ".cxtm";
  
      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();
   
      // Canonicalize the source topic map.
      (new CanonicalXTMWriter(new FileOutputStream(out))).write(sourceMap);
   
      // compare results
      Assert.assertTrue("The test file " + out + " is different from the baseline.",
                 FileUtils.compareFileToResource(out, baseline));
    }
}
