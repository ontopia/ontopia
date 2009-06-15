
// $Id: RDFTestGenerator.java,v 1.5 2006/01/19 12:35:35 grove Exp $

package net.ontopia.topicmaps.utils.rdf.test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.rdf.*;
import net.ontopia.topicmaps.xml.test.*;
import net.ontopia.utils.FileUtils;

public class RDFTestGenerator implements TestCaseGeneratorIF {

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "rdf" + File.separator;
        
    File indir = new File(base + "in" + File.separator);

    // canonical tests
    File[] infiles = indir.listFiles();
    if (infiles != null) {
      for (int ix = 0; ix < infiles.length; ix++) {
        String name = infiles[ix].getName();
        if (name.endsWith(".rdf")) 
          tests.add(new CanonicalTestCase(name, base));
      }
    }

    // error reporting tests
    indir = new File(base + "err" + File.separator);        
    infiles = indir.listFiles();
    if (infiles != null) {
      for (int ix = 0; ix < infiles.length; ix++) {
        String name = infiles[ix].getName();
        if (name.endsWith(".rdf")) 
          tests.add(new ErrorTestCase(name, base));
      }
    }

    // n3 tests
    indir = new File(base + "in" + File.separator);
    infiles = indir.listFiles();
    if (infiles != null) {
      for (int ix = 0; ix < infiles.length; ix++) {
        String name = infiles[ix].getName();
        if (name.endsWith(".n3")) 
          tests.add(new CanonicalTestCase(name, base, "N3"));
      }
    }    

    // n-triple tests
    indir = new File(base + "in" + File.separator);
    infiles = indir.listFiles();
    if (infiles != null) {
      for (int ix = 0; ix < infiles.length; ix++) {
        String name = infiles[ix].getName();
        if (name.endsWith(".nt")) 
          tests.add(new CanonicalTestCase(name, base, "N-TRIPLE"));
      }
    }    
    
    return tests.iterator();
  }

  // --- Canonical test case class

  public class CanonicalTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
    private String syntax;
        
    public CanonicalTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public CanonicalTestCase(String filename, String base, String syntax) {
      this(filename, base);
      this.syntax = syntax;
    }
    
    public void testFile() throws IOException {
      verifyDirectory(base, "out");
      
      // produce canonical output
      String in = base + File.separator + "in" + File.separator + filename;
      String out = base + File.separator + "out" + File.separator + filename;

      TopicMapIF source = new RDFTopicMapReader(new File(in), syntax).read();
      new CanonicalTopicMapWriter(out).write(source);

      // compare results
      assertTrue("test file " + filename + " canonicalized wrongly",
             FileUtils.compare(out, base + File.separator + "baseline" +
                               File.separator + filename));
    }
  }

  // --- Error test case class

  public class ErrorTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
        
    public ErrorTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public void testFile() throws IOException {
      String in = base + File.separator + "err" + File.separator + filename;

      try {
        new RDFTopicMapReader(new File(in)).read();
        fail("Read in '" + filename + "' with no errors");
      } catch (RDFMappingException e) {
      }
    }
  }
}
