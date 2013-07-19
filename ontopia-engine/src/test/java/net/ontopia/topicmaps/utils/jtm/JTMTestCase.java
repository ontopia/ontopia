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
package net.ontopia.topicmaps.utils.jtm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.utils.jtm.JTMTopicMapReader;
import net.ontopia.topicmaps.utils.jtm.JTMTopicMapWriter;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;

import java.util.List;
import net.ontopia.utils.URIUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class JTMTestCase {

  private final static String testdataDirectory = "jtm";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".jtm");
  }

    private String base;
    private String filename;

    public JTMTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    /**
     * Canonicalizes the jtm file into the directory 'out'. Compares the file in
     * 'out' with a baseline file in 'baseline'.
     */
    @Test
    public void testReader() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", 
          filename + ".cxtm");
      // Path to the output (canonicalized output of exported jtm topic map).
      String out = base + File.separator + "out" + filename
          + ".cxtm";

      TopicMapIF jtmMap = new JTMTopicMapReader(URIUtils.getURI(in)).read();

      // Canonicalize the imported jtm.
      FileOutputStream fos = new FileOutputStream(out);
      (new CanonicalXTMWriter(fos)).write(jtmMap);
      fos.close();

      // compare results
      Assert.assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the jtm export of "
          + filename + ".", FileUtils.compareFileToResource(out, baseline));

    }

    /**
     * Deserializes the jtm file, serializes the resulting topic map into 
     * a jtm file in the directory 'jtm-in', reads this file in again and 
     * canonicalizes the result into the directory 'jtm-out'. 
     * Compares the file in 'jtm-out' with a baseline file in 'baseline'.
     */
    @Test
    public void testWriter() throws IOException {
      TestFileUtils.verifyDirectory(base, "jtm-in");
      TestFileUtils.verifyDirectory(base, "jtm-out");    

      // Path to the input topic map document.
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      // Path to the baseline (canonicalized output of the source topic map).
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", 
          filename + ".cxtm");
      
      // Path to the intermediate jtm file
      String jtm = base + File.separator + "jtm-in" + File.separator + filename;
      
      // Path to the output (canonicalized output of exported jtm topic map).
      String out = base + File.separator + "jtm-out" + File.separator + filename
          + ".cxtm";

      TopicMapIF tm = new JTMTopicMapReader(URIUtils.getURI(in)).read();

      // serialize the imported topic map into jtm again
      FileOutputStream fos = new FileOutputStream(jtm);
      (new JTMTopicMapWriter(fos)).write(tm);

      // read in the intermediate jtm file
      tm = new JTMTopicMapReader(new File(jtm)).read();
      
      // Canonicalize the imported jtm.
      fos = new FileOutputStream(out);
      (new CanonicalXTMWriter(fos)).write(tm);
      fos.close();

      // compare results
      Assert.assertTrue("canonicalizing the test file " + filename
          + " gives a different result than canonicalizing the jtm export of "
          + filename + ".", FileUtils.compareFileToResource(out, baseline));
    }
}
