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
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.infoset.impl.basic.URILocator;
import java.util.List;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;
import net.ontopia.utils.URIUtils;
import org.junit.runners.Parameterized.Parameters;

public class CanonicalXTMimportIntoTests extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "canonical";

  public CanonicalXTMimportIntoTests(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    ResourcesFilterIF filter = new ResourcesFilterIF() {
      public boolean ok(String resourcePath) {
        // Ignore importInto-specific file.
        if (resourcePath.endsWith("multiple-tms-read.xtm")) return false;

        if (resourcePath.endsWith(".xtm"))
          return true;
        else
          return false;
      }
    };
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", filter);
  }

  // --- Canonicalization type methods

  protected void canonicalize(String infile, String outfile) throws IOException {
    // Get store factory
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    TopicMapStoreIF store = sfactory.createStore();

    // Read document
    TopicMapIF source = store.getTopicMap();
    XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(infile));
    reader.setValidation(false);
    reader.importInto(source);

    // Canonicalize document
    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(file2URL(infile)));      
    cwriter.write(source);

    store.close();
  }
  
}
