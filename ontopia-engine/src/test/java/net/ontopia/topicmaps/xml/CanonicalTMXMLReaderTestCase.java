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
import net.ontopia.utils.TestFileUtils;
import org.junit.runners.Parameterized.Parameters;

/**
 * INTERNAL: Test case generator based on the cxtm-tests external test
 * suite, thus relying on the download-tmxml ant build target.
 */
public class CanonicalTMXMLReaderTestCase extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "tmxml";

  public CanonicalTMXMLReaderTestCase(URL inputFile, String filename) {
    this.inputFile = inputFile;
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getFilteredTestInputURLs(".xml", testdataDirectory, "in");
  }

  // --- Canonicalization type methods

  // this is actually the file name of the baseline file
  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }
  
  protected boolean filter(String filename) {
    return filename.endsWith(".xml");
  }

  protected void canonicalize(URL infile, File outfile)
    throws IOException {
    TMXMLReader reader = new TMXMLReader(infile);
    reader.setValidate(true); // we do want to validate
    TopicMapIF source = reader.read();

    CanonicalXTMWriter cwriter = new CanonicalXTMWriter(outfile);
    cwriter.write(source);

    source.getStore().close();
  }  
}
