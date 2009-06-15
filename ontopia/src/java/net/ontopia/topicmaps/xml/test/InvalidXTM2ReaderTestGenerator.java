
// $Id: InvalidXTM2ReaderTestGenerator.java,v 1.1 2008/04/23 11:43:45 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.URILocator;

public class InvalidXTM2ReaderTestGenerator extends AbstractCanonicalTests {
  
  // --- Canonicalization type methods

  protected boolean filter(String filename) {
    return filename.endsWith(".xtm");
  }

  protected AbstractCanonicalTestCase makeTestCase(String name, String base) {
    return new ErrorTestCase(name, base);
  }
  
  protected void canonicalize(String infile, String outfile)
    throws IOException {
    // not used, since we are not canonicalizing
  }

  protected String getFileDirectory() {
    return "invalid";
  }

  protected String getBaseDirectory() {
    String root = AbstractOntopiaTestCase.getTestDirectory();
    return root + File.separator + "xtm2" + File.separator;
  }
  
  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }

  // --- Test case class

  public class ErrorTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
        
    public ErrorTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public void testFile() throws IOException {
      String in = base + File.separator + "invalid" + File.separator + filename;
      XTMTopicMapReader reader = new XTMTopicMapReader(new File(in));
      reader.setValidation(true);
      // FIXME: should we do a setXTM2Required(true) or something?

      try {
        reader.read();
        fail("Reader accepted invalid topic map: " + filename);
      } catch (InvalidTopicMapException e) {
        // goodie
      } catch (IOException e) {
        // ok
      } catch (OntopiaRuntimeException e) {
        if (!(e.getCause() instanceof org.xml.sax.SAXParseException))
          throw e;
      }
    }
  }
}
