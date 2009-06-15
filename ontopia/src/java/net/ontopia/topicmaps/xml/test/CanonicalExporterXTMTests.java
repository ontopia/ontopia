
// $Id: CanonicalExporterXTMTests.java,v 1.10 2008/06/25 11:28:58 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.URILocator;

public class CanonicalExporterXTMTests extends AbstractCanonicalExporterTests {
  
  // --- Canonicalization type methods

  protected boolean filter(String filename) {
    // Ignore importInto-specific file.
    if (filename.equals("multiple-tms-read.xtm")) return false;
    if (filename.equals("bug750.xtm")) return false;
    
    return filename.endsWith(".xtm");
  }
  
  protected TopicMapIF exportAndReread(TopicMapIF topicmap, String outfile)
    throws IOException {
    // First we export
    XTMTopicMapWriter writer = new XTMTopicMapWriter(outfile);
    writer.setVersion(1);
    writer.write(topicmap);

    // Then we read back in
    TopicMapIF topicmap2 = getStoreFactory().createStore().getTopicMap();
    XTMTopicMapReader reader = new XTMTopicMapReader(new File(outfile));
    reader.setValidation(false);
    reader.importInto(topicmap2);

    return topicmap2;
  }
}
