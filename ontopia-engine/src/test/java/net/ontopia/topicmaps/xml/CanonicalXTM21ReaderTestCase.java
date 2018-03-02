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
import java.net.URL;
import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.utils.TestFileUtils;
import org.junit.runners.Parameterized.Parameters;

public class CanonicalXTM21ReaderTestCase extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "xtm21";

  public CanonicalXTM21ReaderTestCase(URL inputFile, String filename) {
    this.filename = filename;
    this.inputFile = inputFile;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getFilteredTestInputURLs(".xtm", testdataDirectory, "in");
  }

  // --- Canonicalization type methods

  @Override
  protected void canonicalize(URL infile, File outfile)
    throws IOException {
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    XTMTopicMapReader reader = new XTMTopicMapReader(infile);
    reader.setValidation(false);
    // FIXME: should we do a setXTM2Required(true) or something?
    reader.setStoreFactory(sfactory);
    TopicMapIF source = reader.read();

    CanonicalXTMWriter cwriter = new CanonicalXTMWriter(outfile);
    cwriter.write(source);

    source.getStore().close();
  }

  @Override
  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }
}
