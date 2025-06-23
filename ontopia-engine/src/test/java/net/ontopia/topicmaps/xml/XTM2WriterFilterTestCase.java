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
import java.net.URISyntaxException;
import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.deciders.TMDecider;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Exports a file from the directory 'filter-in' to an xtm file in
 * 'filter-xtm'. Canonicalizes the xtm file into the directory 'filter-out'.
 * Compares the file in 'filter-out' with a baseline file in
 * 'filter-baseline'. The baseline must be created manually, or by inspecting
 * the file in 'filter-out'.
 */
@RunWith(Parameterized.class)
public class XTM2WriterFilterTestCase {

  private final static String testdataDirectory = "canonical";

  private String base;
  private String filename;

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "filter-in", ".ltm|.rdf|.xtm");
  }

  public XTM2WriterFilterTestCase(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
  }

  @Test
  public void testFile() throws IOException, URISyntaxException {
    TestFileUtils.verifyDirectory(base, "filter-out");
    TestFileUtils.verifyDirectory(base, "filter-xtm2");

    // Path to the input topic map document.
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "filter-in",
            filename);
    // Path to the baseline (canonicalized output of the source topic map).
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "filter-baseline",
            filename + ".cxtm");
    // Path to the exported xtm topic map document.
    File xtm = new File(base + File.separator + "filter-xtm2" + File.separator
            + filename + ".xtm");
    // Path to the output (canonicalized output of exported xtm topic map).
    File out = new File(base + File.separator + "filter-out" + File.separator
            + filename + ".xtm2.cxtm");

    // Import topic map from arbitrary source.
    TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();

    // Export document
    XTM2TopicMapWriter xtmWriter = new XTM2TopicMapWriter(xtm);

    // Set this writer to filter out the following topics.
    xtmWriter.setFilter(new TMDecider());

    // Export the topic map to xtm.
    xtmWriter.write(sourceMap);

    // Reimport the exported xtm.
    TopicMapIF xtmMap = ImportExportUtils.getReader(xtm).read();

    // Fix item IDs
    TestUtils.fixItemIds(xtmMap, sourceMap.getStore().getBaseAddress());

    // Canonicalize the reimported xtm.
    new CanonicalXTMWriter(out).write(xtmMap);

    // compare results
    Assert.assertTrue("canonicalizing the test file " + filename
            + " into " + out + " is different from " + baseline,
            TestFileUtils.compareFileToResource(out, baseline));
  }
}
