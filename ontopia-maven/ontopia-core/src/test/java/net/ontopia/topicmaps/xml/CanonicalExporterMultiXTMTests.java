
// $Id: CanonicalExporterMultiXTMTests.java,v 1.9 2008/06/25 11:28:58 lars.garshol Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.FileUtils;

import java.util.List;
import org.junit.Assert;
import org.junit.runners.Parameterized.Parameters;
import net.ontopia.utils.URIUtils;

public class CanonicalExporterMultiXTMTests extends AbstractCanonicalExporterTests {
  
  private final static String testdataDirectory = "canonical";

  public CanonicalExporterMultiXTMTests(String root, String filename) {
    this.filename = filename;
    this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", ".xtm.multi");
  }

  protected String getTestdataDirectory() {
    return testdataDirectory;
  }

  // --- Canonicalization type methods

  // canonicalize NOT USED!
  
  protected TopicMapIF exportAndReread(TopicMapIF topicmap, String outfile) {
    return null; // not needed, because we don't use canonicalize
  }

  // --- Test case class

    public void testExport() throws IOException {
      FileUtils.verifyDirectory(base, "out");
      
      // setup canonicalization filenames
      String outpath = base + File.separator + "out" + File.separator;
      
      // Get store factory
      TopicMapStoreFactoryIF sfactory = getStoreFactory();
      
      // Read all topic maps from document
      String infile = FileUtils.getTestInputFile(testdataDirectory, "in", filename);
      XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(infile));
      reader.setValidation(false);
      reader.setStoreFactory(sfactory);
      
      Collection tms = reader.readAll();
      Iterator iter = tms.iterator();
      int counter = 0;    
      while (iter.hasNext()) {
        // Export each topic map
        TopicMapIF tm = (TopicMapIF)iter.next();
        counter++;

        String tempfile = outpath + "tmp-" + filename + "-" + counter;
        
        XTMTopicMapWriter writer = new XTMTopicMapWriter(tempfile);
        writer.setVersion(1);
        writer.write(tm);
        tm.getStore().close();
        
        // Read exported document (Note: guaranteed to be only one
        // topic map per document)        
        TopicMapIF source2 = sfactory.createStore().getTopicMap();
        new XTMTopicMapReader(new File(tempfile)).importInto(source2);
        
        // Canonicalize the result
        String outfile = outpath + "exp-" + filename + "-" + counter;

        CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
        cwriter.setBaseLocator(new URILocator(file2URL(tempfile)));
        cwriter.write(source2);

        source2.getStore().close();
                
        // Compare results
        String basefile = FileUtils.getTestInputFile(testdataDirectory, "baseline", filename + "-" + counter);
        Assert.assertTrue("test file " + filename + " canonicalized wrongly, " +
                   outfile + " not equal to " + basefile,
                   FileUtils.compareFileToResource(outfile, basefile));
      }      
    }
  
}
