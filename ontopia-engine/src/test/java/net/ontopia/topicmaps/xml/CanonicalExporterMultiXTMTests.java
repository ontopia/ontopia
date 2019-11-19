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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CanonicalExporterMultiXTMTests {
  
  private final static String testdataDirectory = "canonical";
  private final String filename;
  private final URL inputFile;
  private final File outputDirectory;

  public CanonicalExporterMultiXTMTests(URL inputFile, String filename) {
    this.filename = filename;
    this.inputFile = inputFile;
    this.outputDirectory = TestFileUtils.getOutputDirectory(testdataDirectory, "out");
  }

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getFilteredTestInputURLs(".xtm.multi", testdataDirectory, "in");
  }

  protected String getTestdataDirectory() {
    return testdataDirectory;
  }

  // --- Test case class
    @Test
    public void testExport() throws IOException {
      
      // Get store factory
      TopicMapStoreFactoryIF sfactory = new InMemoryStoreFactory();
      
      // Read all topic maps from document
      XTMTopicMapReader reader = new XTMTopicMapReader(inputFile);
      reader.setValidation(false);
      reader.setStoreFactory(sfactory);
      
      Collection tms = reader.readAll();
      Iterator iter = tms.iterator();
      int counter = 0;    
      while (iter.hasNext()) {
        // Export each topic map
        TopicMapIF tm = (TopicMapIF)iter.next();
        counter++;

        File tempfile = new File(outputDirectory, "tmp-" + filename + "-" + counter);
        
        XTMTopicMapWriter writer = new XTMTopicMapWriter(tempfile);
        writer.setVersion(XTMVersion.XTM_1_0);
        writer.write(tm);
        tm.getStore().close();
        
        // Read exported document (Note: guaranteed to be only one
        // topic map per document)        
        TopicMapIF source2 = sfactory.createStore().getTopicMap();
        new XTMTopicMapReader(tempfile).importInto(source2);
        
        // Canonicalize the result
        File outfile = new File(outputDirectory, "exp-" + filename + "-" + counter);

        CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
        cwriter.setBaseLocator(new URILocator(tempfile));
        cwriter.write(source2);

        source2.getStore().close();
                
        // Compare results
        String basefile = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", filename + "-" + counter);
        Assert.assertTrue("test file " + filename + " canonicalized wrongly, " +
                   outfile + " not equal to " + basefile,
                   TestFileUtils.compareFileToResource(outfile, basefile));
      }      
    }
  
}
