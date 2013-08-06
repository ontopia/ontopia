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

import java.io.IOException;
import net.ontopia.topicmaps.core.TopicMapIF;
import java.util.List;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;
import org.junit.runners.Parameterized.Parameters;

public class CanonicalExporterTMXMLTests
  extends AbstractCanonicalExporterTests {
  
  private final static String testdataDirectory = "canonical";

  public CanonicalExporterTMXMLTests(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    ResourcesFilterIF filter = new ResourcesFilterIF() {
      public boolean ok(String resourcePath) {
        // Ignore importInto-specific file.
        if (resourcePath.endsWith("multiple-tms-read.xtm") ||
            resourcePath.endsWith("bug750.xtm") ||
            resourcePath.endsWith("empty-member.xtm") ||
            resourcePath.endsWith("empty.xtm") ||
            resourcePath.endsWith("whitespace.xtm"))
          return false;

        return resourcePath.endsWith(".xtm");
      }
    };
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", filter);
  }

  protected String getTestdataDirectory() {
    return testdataDirectory;
  }

  // --- Canonicalization type methods

  protected TopicMapIF exportAndReread(TopicMapIF topicmap, String outfile)
    throws IOException {
    // First we export
    TMXMLWriter writer = new TMXMLWriter(outfile);
    writer.write(topicmap);
    writer.close();

    // Then we read back in
    TopicMapIF topicmap2 = getStoreFactory().createStore().getTopicMap();
    TMXMLReader reader = new TMXMLReader(outfile);
    reader.importInto(topicmap2);
    return topicmap2;
  }  
}
