
// $Id: LTMTestGenerator.java,v 1.11 2006/01/19 12:35:35 grove Exp $

package net.ontopia.topicmaps.utils.ltm.test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.ltm.*;
import net.ontopia.topicmaps.xml.test.*;
import net.ontopia.utils.FileUtils;

public class LTMTestGenerator implements TestCaseGeneratorIF {

  /**
    * @return true iff the test-case in fileName was added to test features
    * after LTM1.3 was implemented.
    */
  public static boolean ltm13(String fileName) {
    if (fileName.endsWith("-1.3.ltm")) return true;
    return false;
  }
  
  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "ltm" + File.separator;
        
    File indir = new File(base + "in" + File.separator);
        
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
        
    for (int ix = 0; ix < infiles.length; ix++) {
      String name = infiles[ix].getName();
      if (name.endsWith(".ltm")) 
        tests.add(new CanonicalTestCase(name, base));
    }

    return tests.iterator();
  }

  // --- Test case class

  public class CanonicalTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
        
    public CanonicalTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public void testFile() throws IOException {
      verifyDirectory(base, "out");
      
      // produce canonical output
      String in = base + File.separator + "in" + File.separator +
        filename;
      String out = base + File.separator + "out" + File.separator +
        filename;
      
      TopicMapIF source = new LTMTopicMapReader(new File(in)).read();
      
      if (ltm13(filename)) {
        out += ".cxtm";
        new CanonicalXTMWriter(new FileOutputStream(out)).write(source);
  
        // compare results
        assertTrue("test file " + filename + " canonicalized wrongly",
              FileUtils.compare(out, base + File.separator + "baseline" +
                      File.separator + filename + ".cxtm"));
      } else {
        new CanonicalTopicMapWriter(out).write(source);
  
        // compare results
        assertTrue("test file " + filename + " canonicalized wrongly",
              FileUtils.compare(out, base + File.separator + "baseline" +
                      File.separator + filename));
      }
    }
  }
}
