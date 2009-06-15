
// $Id: CanonicalExporterMultiXTMTests.java,v 1.9 2008/06/25 11:28:58 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import java.util.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.FileUtils;

public class CanonicalExporterMultiXTMTests extends AbstractCanonicalExporterTests {
  
  // --- Canonicalization type methods

  protected boolean filter(String filename) {
    // Only include designated files
    if (filename.endsWith(".xtm.multi"))
      return true;
    else
      return false;
  }

  // canonicalize NOT USED!
  
  protected TopicMapIF exportAndReread(TopicMapIF topicmap, String outfile) {
    return null; // not needed, because we don't use canonicalize
  }

  protected AbstractCanonicalTestCase createTestCase(String name, String base) {
    return new CanonicalTestCase(name, base);
  }
  
  // --- Test case class

  public class CanonicalTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
        
    public CanonicalTestCase(String filename, String base) {
      super("testExport");
      this.filename = filename;
      this.base = base;
    }

    public void testExport() throws IOException {
      verifyDirectory(base, "out");
      
      // setup canonicalization filenames
      String inpath = base + File.separator + "in" + File.separator;
      String outpath = base + File.separator + "out" + File.separator;
      String basepath = base + File.separator + "baseline" + File.separator;
      
      // Get store factory
      TopicMapStoreFactoryIF sfactory = getStoreFactory();
      
      // Read all topic maps from document
      String infile = inpath + filename;
      XTMTopicMapReader reader = new XTMTopicMapReader(new File(infile));
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
        
        // Read exported document (Note: guaranteed to be only one topic map per document)
        TopicMapIF source2 = sfactory.createStore().getTopicMap();
        new XTMTopicMapReader(new File(tempfile)).importInto(source2);
        
        // Canonicalize the result
        String outfile = outpath + "exp-" + filename + "-" + counter;

        CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
        cwriter.setBaseLocator(new URILocator(file2URL(tempfile)));
        cwriter.write(source2);

        source2.getStore().close();
                
        // Compare results
        String basefile = basepath + filename + "-" + counter;
        assertTrue("test file " + filename + " canonicalized wrongly",
                   FileUtils.compare(outfile, basefile));
      }      
    }
  }
  
}
