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

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MergeTMTestCase {
    
  private final static String testdataDirectory = "merge";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".xtm");
  }

  // --- Test case class

    protected String base;
    protected String filename;
        
    public MergeTMTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testMergeTM() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
      
      // produce canonical output
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      String in2 = TestFileUtils.getTestInputFile(testdataDirectory, "in", 
        filename.substring(0, filename.length() - 3) + "sub");
      String out = base + File.separator + "out" + File.separator + filename;
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", filename);
            
      TopicMapIF source1 = new XTMTopicMapReader(URIUtils.getURI(in)).read();
      TopicMapIF source2 = new XTMTopicMapReader(URIUtils.getURI(in2)).read();

      MergeUtils.mergeInto(source1, source2);
      new CanonicalTopicMapWriter(out).write(source1);

      // compare results
      Assert.assertTrue("test file " + filename + " canonicalized wrongly",
             FileUtils.compareFileToResource(out, baseline));
    }
}
