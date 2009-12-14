
// $Id: CTMInvalidTestGenerator.java,v 1.1 2009/04/27 11:05:08 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm.test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.ctm.*;
import net.ontopia.topicmaps.xml.test.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

public class CTMInvalidTestGenerator implements TestCaseGeneratorIF {
  
  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "ctm" + File.separator;
        
    File indir = new File(base + "invalid" + File.separator);
        
    File[] infiles = indir.listFiles();
    for (int ix = 0; infiles != null && ix < infiles.length; ix++) {
      String name = infiles[ix].getName();
      if (name.endsWith(".ctm")) 
        tests.add(new InvalidTestCase(name, base));
    }

    return tests.iterator();
  }

  // --- Test case class

  public class InvalidTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
        
    public InvalidTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public void testFile() throws IOException {
      // produce canonical output
      String in = base + File.separator + "invalid" + File.separator +
        filename;

      try {
        new CTMTopicMapReader(new File(in)).read();
        fail("no error in reading " + filename);
      } catch (IOException e) {
      } catch (InvalidTopicMapException e) {
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Error reading: " + in, e);
      }
    }
  }
}
