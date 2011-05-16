
//$Id: CanonicalXTMWriterTestGenerator.java,v 1.5 2008/01/09 10:07:35 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.FileUtils;

public class CanonicalXTMWriterTestGenerator implements TestCaseGeneratorIF {

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "cxtm" + File.separator;
     
    File indir = new File(base + "in" + File.separator);
     
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
     
    for (int i = 0; i < infiles.length; i++) {
      String name = infiles[i].getName();
      if (name.endsWith(".ltm") ||
          name.endsWith(".xtm")) 
        tests.add(makeTestCase(name, base));
    }

    return tests.iterator();
  }

  protected AbstractCanonicalTestCase makeTestCase(String name, String base) {
    return new CanonicalTestCase(name, base);
  }

  // --- Test case class

  public class CanonicalTestCase extends AbstractCanonicalTestCase {
    protected String base;
    protected String filename;
      
    public CanonicalTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }
  
    public void testFile() throws IOException {
      verifyDirectory(base, "out");
   
      // Path to the input topic map document.
      String in = base + File.separator + "in" + File.separator + filename;
      // Path to the baseline
      String baseline = base + File.separator + "baseline" + File.separator 
        + filename + ".cxtm";
      // Path to the canonicalized output.
      String out = base + File.separator + "out" + File.separator 
        + filename + ".cxtm";
  
      // Import topic map from arbitrary source.
      TopicMapIF sourceMap = ImportExportUtils.getReader(in).read();
   
      // Canonicalize the source topic map.
      (new CanonicalXTMWriter(new FileOutputStream(out))).write(sourceMap);
   
      // compare results
      assertTrue("The test file " + filename + " is different from the baseline.",
                 FileUtils.compare(out, baseline));
    }
  }
}
