
// $Id: CanonicalExporterTMXMLTests.java,v 1.3 2007/09/03 08:08:39 geir.gronmo Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.URILocator;

public class CanonicalExporterTMXMLTests
  extends AbstractCanonicalExporterTests {
  
  // --- Canonicalization type methods

  protected boolean filter(String filename) {
    // Ignore importInto-specific file.
    if (filename.equals("multiple-tms-read.xtm") ||
        filename.equals("bug750.xtm") ||
        filename.equals("empty-member.xtm") ||
        filename.equals("empty.xtm") ||
        filename.equals("whitespace.xtm"))
      return false;
    
    return filename.endsWith(".xtm");
  }

  protected TopicMapIF exportAndReread(TopicMapIF topicmap, String outfile)
    throws IOException {
    // First we export
    TMXMLWriter writer = new TMXMLWriter(outfile);
    writer.write(topicmap);
    writer.close();

    // Then we read back in
    TopicMapIF topicmap2 = getStoreFactory().createStore().getTopicMap();
    TMXMLReader reader = new TMXMLReader(outfile);
    reader.importInto(topicmap2);
    return topicmap2;
  }  
}
