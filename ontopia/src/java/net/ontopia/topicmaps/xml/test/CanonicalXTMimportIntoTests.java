
// $Id: CanonicalXTMimportIntoTests.java,v 1.9 2004/10/18 10:32:29 grove Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.URILocator;

public class CanonicalXTMimportIntoTests extends AbstractCanonicalTests {
  
  // --- Canonicalization type methods

  protected boolean filter(String filename) {
    // Ignore importInto-specific file.
    if (filename.equals("multiple-tms-read.xtm")) return false;
    
    if (filename.endsWith(".xtm"))
      return true;
    else
      return false;
  }

  protected void canonicalize(String infile, String outfile) throws IOException {
    // Get store factory
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    TopicMapStoreIF store = sfactory.createStore();

    // Read document
    TopicMapIF source = store.getTopicMap();
    XTMTopicMapReader reader = new XTMTopicMapReader(new File(infile));
    reader.setValidation(false);
    reader.importInto(source);

    // Canonicalize document
    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(file2URL(infile)));      
    cwriter.write(source);

    store.close();
  }
  
}
