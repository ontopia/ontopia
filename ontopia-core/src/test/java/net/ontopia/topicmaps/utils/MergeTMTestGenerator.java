
// $Id: MergeTMTestGenerator.java,v 1.14 2006/01/19 12:35:35 grove Exp $

package net.ontopia.topicmaps.utils;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import net.ontopia.test.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.utils.FileUtils;

public class MergeTMTestGenerator implements TestCaseGeneratorIF {
    
  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractOntopiaTestCase.getTestDirectory();
    String base = root + File.separator + "merge" + File.separator;
        
    File indir = new File(base + "in" + File.separator);
        
    File[] infiles = indir.listFiles();

    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
        
    for (int ix = 0; ix < infiles.length; ix++) {
      String filename = infiles[ix].getName();
      if (filename.endsWith(".xtm")) 
        tests.add(createTestCase(filename, base));
    }

    return tests.iterator();
  }

  protected TestCase createTestCase(String filename, String base) {
    return new MergeTMTestCase(filename, base);
  }
  
  // --- Test case class

  public class MergeTMTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
        
    public MergeTMTestCase(String filename, String base) {
      super("testMergeTM");
      this.filename = filename;
      this.base = base;
    }

    public void testMergeTM() throws IOException {
      verifyDirectory(base, "out");
      
      // produce canonical output
      String in = base + File.separator + "in" + File.separator + filename;
      String in2 = base + File.separator + "in" + File.separator + 
        filename.substring(0, filename.length() - 3) + "sub";
      String out = base + File.separator + "out" + File.separator + filename;
            
      TopicMapIF source1 = new XTMTopicMapReader(new File(in)).read();
      TopicMapIF source2 = new XTMTopicMapReader(new File(in2)).read();

      MergeUtils.mergeInto(source1, source2);
      new CanonicalTopicMapWriter(out).write(source1);

      // compare results
      assertTrue("test file " + filename + " canonicalized wrongly",
             FileUtils.compare(out, base + File.separator + "baseline" +
                     File.separator + filename));
    }
  }
}
