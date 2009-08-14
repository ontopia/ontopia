
// $Id: CanonicalXTM2ReaderTestGenerator.java,v 1.1 2008/04/23 11:43:45 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.URILocator;

public class CanonicalXTM2ReaderTestGenerator extends AbstractCanonicalTests {
  
  // --- Canonicalization type methods

  protected boolean filter(String filename) {
    return filename.endsWith(".xtm");   
  }

  protected void canonicalize(String infile, String outfile)
    throws IOException {
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    XTMTopicMapReader reader = new XTMTopicMapReader(new File(infile));
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

  protected String getBaseDirectory() {
    String root = AbstractOntopiaTestCase.getTestDirectory();
    return root + File.separator + "xtm2" + File.separator;
  }
  
  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }
}
