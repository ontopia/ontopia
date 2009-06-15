
// $Id: CanonicalXTMreadTests.java,v 1.10 2008/01/11 13:29:36 geir.gronmo Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.URILocator;

public class CanonicalXTMreadTests extends AbstractCanonicalTests {
  
  // --- Canonicalization type methods

  protected boolean filter(String filename) {
    // Ignore importInto-specific file.
    if (filename.equals("multiple-tms-importInfo.xtm")) return false;
    
    return filename.endsWith(".xtm");
  }

  protected void canonicalize(String infile, String outfile) throws IOException {
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    XTMTopicMapReader reader = new XTMTopicMapReader(new File(infile));
    reader.setValidation(false);
    reader.setStoreFactory(sfactory);
    TopicMapIF source = reader.read();

    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(file2URL(infile)));      
    cwriter.write(source);

    source.getStore().close();
  }
  
}
