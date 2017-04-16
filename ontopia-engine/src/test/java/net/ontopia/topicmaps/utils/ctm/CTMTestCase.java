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

package net.ontopia.topicmaps.utils.ctm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CTMTestCase {

  private final static String testdataDirectory = "ctm";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".ctm");
  }

    private String base;
    private String filename;
        
    public CTMTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
      
      // produce canonical output
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", 
        filename);
      String out = base + File.separator + "out" + File.separator +
        filename;

      TopicMapIF source = null;
      try {
        source = new CTMTopicMapReader(TestFileUtils.getTestInputURL(in)).read();
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Error in " + in, e);
      }

      DuplicateSuppressionUtils.removeDuplicates(source);
      try {
        new CanonicalXTMWriter(new FileOutputStream(out)).write(source);
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Error in " + in, e);
      }
  
      // compare results
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", 
        filename + ".cxtm");
      Assert.assertTrue("test file " + filename + " canonicalized wrongly",
                 FileUtils.compareFileToResource(out, baseline));
    }

}
