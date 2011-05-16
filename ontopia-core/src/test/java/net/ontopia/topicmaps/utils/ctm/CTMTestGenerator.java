
// $Id: CTMTestGenerator.java,v 1.2 2009/02/12 11:52:17 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

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
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.utils.FileUtils;

public class CTMTestGenerator implements TestCaseGeneratorIF {
  
  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "ctm" + File.separator;
        
    File indir = new File(base + "in" + File.separator);
        
    File[] infiles = indir.listFiles();
    for (int ix = 0; infiles != null && ix < infiles.length; ix++) {
      String name = infiles[ix].getName();

      if (name.endsWith(".ctm")) 
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

      TopicMapIF source = null;
      try {
        source = new CTMTopicMapReader(new File(in)).read();
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Error in " + in, e);
      }

      DuplicateSuppressionUtils.removeDuplicates(source);
      try {
        new CanonicalXTMWriter(new FileOutputStream(out)).write(source);
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Error in " + in, e);
      }
  
      // compare results
      assertTrue("test file " + filename + " canonicalized wrongly",
                 FileUtils.compare(out, base + File.separator + "baseline" +
                                   File.separator + filename + ".cxtm"));
    }
  }
}
