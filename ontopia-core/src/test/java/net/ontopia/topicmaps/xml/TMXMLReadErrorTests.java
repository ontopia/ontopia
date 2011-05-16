
// $Id: TMXMLReadErrorTests.java,v 1.1 2006/05/04 15:41:05 larsga Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.test.*;
import net.ontopia.topicmaps.xml.*;

public class TMXMLReadErrorTests extends AbstractCanonicalTests {

  protected String getBaseDirectory() {
    String root = AbstractOntopiaTestCase.getTestDirectory();
    return root + File.separator + "tmxml" + File.separator;
  }

  protected String getFileDirectory() {
    return "invalid";
  }

  protected boolean filter(String filename) {
    return filename.endsWith(".xml");
  }

  protected AbstractCanonicalTestCase makeTestCase(String name, String base) {
    return new ErrorTestCase(name, base);
  }
  
  protected void canonicalize(String infile, String outfile) {
    // not used
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
      TMXMLReader reader = new TMXMLReader(in);

      try {
        reader.read();
        fail("succeeded in importing bad file " + filename);
      } catch (IOException e) {
        // ok
      } catch (OntopiaRuntimeException e) {
        if (!(e.getCause() instanceof org.xml.sax.SAXParseException))
          throw e;
      }
    }
  }
  
}
