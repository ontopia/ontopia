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

package net.ontopia.topicmaps.utils.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.TestFileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RDFWriterCanonicalTestCase {

  private final static String testdataDirectory = "tm2rdf";

  private String base;
  private String filename;

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".xtm|.ltm|.ctm");
  }

  // --- Canonical test case class

    public RDFWriterCanonicalTestCase(String root, String filename) {
      this.filename = filename;
      this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
      TestFileUtils.verifyDirectory(base, "tmp");

      // export
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      String tmp = base + File.separator + "tmp" + File.separator + filename;
      String bline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline",
          filename);
      TopicMapReaderIF reader = ImportExportUtils.getReader(in);
      if (reader instanceof XTMTopicMapReader) {
        ((XTMTopicMapReader) reader).setValidation(false);
      }
      TopicMapIF tm = reader.read();
      FileOutputStream fos = new FileOutputStream(tmp);
      new RDFTopicMapWriter(fos).write(tm);
      fos.close();

      // read in base line and export
      Model baseline = ModelFactory.createDefaultModel().read(StreamUtils.getInputStream(bline),
          bline, "RDF/XML");
      Model result = ModelFactory.createDefaultModel().read(new FileInputStream(tmp), "file:"
          + tmp, "RDF/XML");

      // compare results
      Assert.assertTrue("test file " + filename + " produced non-isomorphic model: " + 
								 bline + " " + tmp,
          result.isIsomorphicWith(baseline));
    }
}
