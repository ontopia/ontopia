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
public class TMXMLWriterSpecialTestCase {

  protected boolean recanonicalizeSource = false;

  protected final static String testdataDirectory = "tmxmlWriter";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "x-in", ".ltm|.xtm");
  }

  // --- Test case class

  /**
   * Exports a file from the directory 'x-in' to an TMXML file in
   * 'x-tmxml'.  Canonicalizes the tmxml file into the directory
   * 'x-out'. Compares the file in 'x-out' with a baseline file in
   * 'x-baseline'. The baseline must be created manually, or by
   * inspecting the file in 'out'.
   */
    private String base;
    private String filename;

    public TMXMLWriterSpecialTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "x-out");
      TestFileUtils.verifyDirectory(base, "x-tmxml");

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "x-in", filename);
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "x-baseline", 
          filename + ".cxtm");
      // Path to the exported TMXML topic map document.
      File tmxml = new File(base + File.separator + "x-tmxml" + File.separator +
          filename + ".xml");
      // Path to the output (canonicalized output of exported tmxml topic map).
      String out = base + File.separator + "x-out" + File.separator + filename
          + ".xml.cxtm";

      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      // Export the topic map to TMXML.
      TMXMLWriter tmxmlWriter = new TMXMLWriter(tmxml);
      tmxmlWriter.write(sourceMap);

      // Reimport the exported TMXML.
      TopicMapIF tmxmlMap = ImportExportUtils.getReader(tmxml).read();

      // Canonicalize the reimported TMXML.
      (new CanonicalXTMWriter(new FileOutputStream(out))).write(tmxmlMap);

      // compare results
      Assert.assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the tmxml export of "
          + out + " " + baseline, FileUtils.compareFileToResource(out, baseline));
    }

}
