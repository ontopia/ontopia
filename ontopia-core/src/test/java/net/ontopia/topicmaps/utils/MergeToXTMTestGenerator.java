// $Id: MergeToXTMTestGenerator.java,v 1.5 2006/01/19 12:35:35 grove Exp $

package net.ontopia.topicmaps.utils;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.utils.FileUtils;

public class MergeToXTMTestGenerator implements TestCaseGeneratorIF {
    
  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = System.getProperty("net.ontopia.test.root");
    String base = root + File.separator + "merge-to-xtm" + File.separator;
        
    File indir = new File(base + "in" + File.separator);
        
    File[] infiles = indir.listFiles();

    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
        
    for (int ix = 0; ix < infiles.length; ix++) {
      String name = infiles[ix].getName();
      if (name.endsWith(".xtm")) 
        tests.add(new MergeTMTestCase(name, base));
    }

    return tests.iterator();
  }

  // --- Test case class

  public class MergeTMTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
        
    public MergeTMTestCase(String filename, String base) {
      super("testMergeToXTM");
      this.filename = filename;
      this.base = base;
    }

    public void testMergeToXTM() throws IOException {
      verifyDirectory(base, "out");
      verifyDirectory(base, "tmp");

      // load
      String in = base + File.separator + "in" + File.separator + filename;
      String in2 = base + File.separator + "in" + File.separator + 
        filename.substring(0, filename.length() - 3) + "sub";
      TopicMapIF source1 = new XTMTopicMapReader(new File(in)).read();
      TopicMapIF source2 = new XTMTopicMapReader(new File(in2)).read();

      // merge
      MergeUtils.mergeInto(source1, source2);
      
      // produce XTM output
      String tmp = base + File.separator + "tmp" + File.separator + filename;
      new XTMTopicMapWriter(tmp).write(source1);

      // reload and write canonically
      String out = base + File.separator + "out" + File.separator + filename;
      source1 = new XTMTopicMapReader(new File(tmp)).read();
      new CanonicalTopicMapWriter(out).write(source1);

      // compare results
      assertTrue("test file " + filename + " canonicalized wrongly",
             FileUtils.compare(out, base + File.separator + "baseline" +
                     File.separator + filename));
    }
  }
}




