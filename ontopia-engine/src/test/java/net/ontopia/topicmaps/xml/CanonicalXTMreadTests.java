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
import java.util.List;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;
import net.ontopia.utils.TestFileUtils;
import org.junit.runners.Parameterized.Parameters;

public class CanonicalXTMreadTests extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "canonical";

  public CanonicalXTMreadTests(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    ResourcesFilterIF filter = new ResourcesFilterIF() {
      public boolean ok(String resourcePath) {
        // Ignore importInto-specific file.
        if (resourcePath.endsWith("multiple-tms-importInfo.xtm")) return false;

        return resourcePath.endsWith(".xtm");
      }
    };
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", filter);
  }

  // --- Canonicalization type methods

  protected void canonicalize(String infile, String outfile) throws IOException {
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    XTMTopicMapReader reader = new XTMTopicMapReader(TestFileUtils.getTestInputURL(infile));
    reader.setValidation(false);
    reader.setStoreFactory(sfactory);
    TopicMapIF source = reader.read();

    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(file2URL(infile)));      
    cwriter.write(source);

    source.getStore().close();
  }
  
}
