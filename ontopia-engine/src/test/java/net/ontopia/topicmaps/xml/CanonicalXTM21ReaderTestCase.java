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

import java.io.FileOutputStream;
import java.io.IOException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import java.util.List;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.runners.Parameterized.Parameters;

public class CanonicalXTM21ReaderTestCase extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "xtm21";

  public CanonicalXTM21ReaderTestCase(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".xtm");
  }

  // --- Canonicalization type methods

  protected void canonicalize(String infile, String outfile)
    throws IOException {
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(infile));
    reader.setValidation(false);
    // FIXME: should we do a setXTM2Required(true) or something?
    reader.setStoreFactory(sfactory);
    TopicMapIF source = reader.read();

    FileOutputStream out = new FileOutputStream(outfile);
    CanonicalXTMWriter cwriter = new CanonicalXTMWriter(out);
    cwriter.write(source);
    out.close();

    source.getStore().close();
  }

  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }
}
