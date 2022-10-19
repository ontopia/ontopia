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
package net.ontopia.topicmaps.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TopicMapSynchronizerTests {

  private final static String testdataDirectory = "tmsync";

  private String base;
  private String filename;

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", "-target.ltm");
  }

  public TopicMapSynchronizerTests(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
  }

  @Test
  public void testFile() throws IOException {
    TestFileUtils.verifyDirectory(base, "out");

    String suffix = "-target.ltm";

    // setup canonicalization filenames
    String in1 = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
    String testname
            = filename.substring(0, filename.length() - suffix.length());
    String in2 = TestFileUtils.getTestInputFile(testdataDirectory, "in", testname + "-source.ltm");
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", filename);

    String out = base + File.separator + "out" + File.separator + filename;

    // produce canonical output
    canonicalize(in1, in2, out);

    // compare results
    Assert.assertTrue("test file " + filename + " canonicalized wrongly",
            TestFileUtils.compareFileToResource(out, baseline));
  }

  private void canonicalize(String infile1, String infile2, String outfile)
          throws IOException {
    TopicMapIF target = ImportExportUtils.getReader(infile1).read();
    TopicMapIF source = ImportExportUtils.getReader(infile2).read();

    LocatorIF base = source.getStore().getBaseAddress();
    TopicIF sourcet = (TopicIF) source.getObjectByItemIdentifier(base.resolveAbsolute("#source"));

    TopicMapSynchronizer.update(target, sourcet);

    new CanonicalXTMWriter(new File(outfile)).write(target);
  }
}
