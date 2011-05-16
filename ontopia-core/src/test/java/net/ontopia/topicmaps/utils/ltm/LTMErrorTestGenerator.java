
// $Id: LTMErrorTestGenerator.java,v 1.6 2005/03/30 11:45:47 opland Exp $

package net.ontopia.topicmaps.utils.ltm;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ltm.*;
import net.ontopia.topicmaps.xml.test.*;

public class LTMErrorTestGenerator implements TestCaseGeneratorIF {

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "ltm" + File.separator;
        
    File indir = new File(base + "error" + File.separator);
        
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
        
    for (int ix = 0; ix < infiles.length; ix++) {
      String name = infiles[ix].getName();
      if (name.endsWith(".ltm")) 
        tests.add(new ErrorTestCase(name, base));
    }

    return tests.iterator();
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
      // produce canonical output
      String in = base + File.separator + "error" + File.separator +
        filename;

      try {
        new LTMTopicMapReader(new File(in)).read();
        fail("test file " + filename + " parsed without error");
      } catch (java.io.IOException e) {
      } catch (net.ontopia.topicmaps.core.UniquenessViolationException e) {
      } catch (net.ontopia.utils.OntopiaRuntimeException e) {
      }
    }
  }
}
