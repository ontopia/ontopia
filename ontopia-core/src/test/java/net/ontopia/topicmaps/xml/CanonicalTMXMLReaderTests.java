
// $Id: CanonicalTMXMLReaderTests.java,v 1.4 2008/01/11 13:29:36 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * INTERNAL: Test case generator based on the cxtm-tests external test
 * suite, thus relying on the download-tmxml ant build target.
 */
public class CanonicalTMXMLReaderTests extends AbstractCanonicalTests {
  
  // --- Canonicalization type methods

  protected String getBaseDirectory() {
    String root = AbstractOntopiaTestCase.getTestDirectory();
    return root + File.separator + "tmxml" + File.separator;
  }

  // this is actually the file name of the baseline file
  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }
  
  protected boolean filter(String filename) {
    return filename.endsWith(".xml");
  }

  protected void canonicalize(String infile, String outfile)
    throws IOException {
    TMXMLReader reader = new TMXMLReader(infile);
    reader.setValidate(true); // we do want to validate
    TopicMapIF source = reader.read();

    FileOutputStream fos = new FileOutputStream(outfile);
    CanonicalXTMWriter cwriter = new CanonicalXTMWriter(fos);
    cwriter.write(source);

    fos.close();
    source.getStore().close();
  }  
}
