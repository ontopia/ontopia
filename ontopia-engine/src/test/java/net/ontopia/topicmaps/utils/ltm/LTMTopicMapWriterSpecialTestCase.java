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

package net.ontopia.topicmaps.utils.ltm;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LTMTopicMapWriterSpecialTestCase {

  private final static String testdataDirectory = "ltmWriter";

  private String base;
  private String filename;

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "x-in", ".ltm|.rdf|.xtm");
  }

  // --- Test case class

  /**
   * Exports a file from the directory 'x-in' to an ltm file in 'x-ltm'.
   * Canonicalizes the ltm file into the directory 'x-out'. Compares the file in
   * 'x-out' with a baseline file in 'x-baseline'. The baseline must be created
   * manually, or by inspecting the file in 'out'.
   */
    public LTMTopicMapWriterSpecialTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "x-out");
      TestFileUtils.verifyDirectory(base, "x-ltm");

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "x-in", filename);
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "x-baseline", 
          filename + ".cxtm");
      // Path to the exported ltm topic map document.
      File ltm = new File(base + File.separator + "x-ltm" + File.separator + filename
          + ".ltm");
      // Path to the output (canonicalized output of exported ltm topic map).
      File out = new File(base + File.separator + "x-out" + File.separator + filename
          + ".cxtm");

      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      // Export the topic map to ltm.
      LTMTopicMapWriter ltmWriter = new LTMTopicMapWriter(ltm);
      ltmWriter.setPreserveIds(!filename.startsWith("generateId-"));
      ltmWriter.write(sourceMap);

      // Reimport the exported ltm.
      TopicMapIF ltmMap = ImportExportUtils.getReader(ltm).read();

      // Canonicalize the reimported ltm.
      new CanonicalXTMWriter(out).write(ltmMap);

      // compare results
      Assert.assertTrue("canonicalizing the test file " + filename +
          " produces " + out + " which is different from " +
          baseline, TestFileUtils.compareFileToResource(out, baseline));
    }

}
