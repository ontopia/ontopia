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
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CanonicalXTM21WriterTestCase {

  private final static String testdataDirectory = "xtm21";

  protected String base;
  protected String filename;

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".xtm");
  }

  public CanonicalXTM21WriterTestCase(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
  }

  @Test
  public void testFile() throws IOException, URISyntaxException {
    TestFileUtils.verifyDirectory(base, "out");

    // Path to the input topic map
    String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
    // Path to the baseline
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline",
            filename + ".cxtm");
    // Path to the canonicalized output.
    File out = new File(base + File.separator + "out" + File.separator
            + "tmp-" + filename + ".cxtm");
    // Path to the temporary file
    File tmp = new File(base + File.separator + "out" + File.separator
            + "tmp-" + filename);

    // Import topic map from arbitrary source.
    TopicMapIF tm = new XTMTopicMapReader(TestFileUtils.getTestInputURL(in)).read();
    LocatorIF base = tm.getStore().getBaseAddress();

    // Export to XTM 2.1
    XTMTopicMapWriter writer = new XTMTopicMapWriter(tmp);
    writer.setVersion(XTMVersion.XTM_2_1);
    // Do not omit the item identifiers
    writer.setExportSourceLocators(true);
    writer.write(tm);

    // Import again from exported file
    tm = ImportExportUtils.getReader(tmp).read();

    // Fix item identifiers for canonicalization
    TestUtils.fixItemIds(tm, base);

    // Output CXTM
    new CanonicalXTMWriter(out).write(tm);

    // compare results
    Assert.assertTrue("The test file " + filename + " is different from the baseline: " + out + " " + baseline,
            TestFileUtils.compareFileToResource(out, baseline));
  }
}
