
// $Id: CanonicalTMXMLReaderTests.java,v 1.4 2008/01/11 13:29:36 geir.gronmo Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.URILocator;

public class CanonicalTMXMLReaderTests extends AbstractCanonicalTests {
  
  // --- Canonicalization type methods
  
  protected boolean filter(String filename) {
    return filename.endsWith(".xml") &&
           !filename.equals("xmltools-tm.xml");
  }

  protected void canonicalize(String infile, String outfile)
    throws IOException {
    TMXMLReader reader = new TMXMLReader(infile);
    reader.setValidate(false);
    TopicMapIF source = reader.read();

    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(file2URL(infile)));      
    cwriter.write(source);

    source.getStore().close();
  }  
}
