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
import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TMXMLWriterGeneralTestCase {

  private final static String testdataDirectory = "tmxmlWriter";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm|.rdf|.xtm");
  }

  // --- Test case class

    private String base;
    private String filename;

    public TMXMLWriterGeneralTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    /**
     * Exports a file from the directory 'in' to a TM/XML file in
     * 'tmxml'. Canonicalizes the TMXML file into the directory 'out'.
     * Compares the file in 'out' with a baseline file in 'baseline'.
     * If recanonicalizeSource is set to true, then the source file is
     * also canonicalized directly into 'baseline' and used as
     * baseline.
     */
    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
      TestFileUtils.verifyDirectory(base, "tmxml");

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", 
        filename + ".cxtm");
      // Path to the exported tmxml topic map document.
      File tmxml = new File(base + File.separator + "tmxml" + File.separator + filename
          + ".xml");
      // Path to the output (canonicalized output of exported tmxml topic map).
      File out = new File(base + File.separator + "out" + File.separator + filename
          + ".xml.cxtm");

      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

      // Export the topic map to tmxml.
      TopicMapWriterIF writer = new TMXMLWriter(tmxml);
      writer.write(sourceMap);

      // Reimport the exported tmxml.
      TopicMapIF tmxmlMap = ImportExportUtils.getReader(tmxml).read();

      // Canonicalize the reimported tmxml.
      new CanonicalXTMWriter(out).write(tmxmlMap);

      // compare results
      Assert.assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the tmxml export: "
          + out + " " + baseline, FileUtils.compareFileToResource(out, baseline));
    }

}
