
// $Id: TopicMapSynchronizerTests.java,v 1.2 2008/06/13 08:36:29 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ontopia.test.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;

public class TopicMapSynchronizerTests implements TestCaseGeneratorIF {
  
  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractOntopiaTestCase.getTestDirectory();
    String base = root + File.separator + "tmsync" + File.separator;
        
    File indir = new File(base + "in" + File.separator);
    if (!indir.exists())
      throw new OntopiaRuntimeException("Directory '" + indir +
                                        "' does not exist!");
    
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
        
    for (int ix = 0; ix < infiles.length; ix++) {
      if (!infiles[ix].isDirectory() &&
          infiles[ix].getName().endsWith("-target.ltm"))
        tests.add(new CanonicalTestCase(infiles[ix].getName(), base));
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

      String suffix = "-target.ltm";
      
      // setup canonicalization filenames
      String in1 = base + File.separator + "in" + File.separator + filename;
      String testname =
        filename.substring(0, filename.length() - suffix.length());
      String in2 = base + File.separator + "in" + File.separator + 
                   testname + "-source.ltm";
      
      String out = base + File.separator + "out" + File.separator + filename;
      
      // produce canonical output
      canonicalize(in1, in2, out);
      
      // compare results
      assertTrue("test file " + filename + " canonicalized wrongly",
                 FileUtils.compare(out, base + File.separator + "baseline" +
                         File.separator + filename));
    }

    private void canonicalize(String infile1, String infile2, String outfile)
      throws IOException {
      TopicMapIF target = ImportExportUtils.getReader(infile1).read();
      TopicMapIF source = ImportExportUtils.getReader(infile2).read();

      LocatorIF base = source.getStore().getBaseAddress();
      TopicIF sourcet = (TopicIF)
        source.getObjectByItemIdentifier(base.resolveAbsolute("#source"));

      TopicMapSynchronizer.update(target, sourcet);

      FileOutputStream out = new FileOutputStream(outfile);
      new CanonicalXTMWriter(out).write(target);
      out.close();
    }
  }
}
