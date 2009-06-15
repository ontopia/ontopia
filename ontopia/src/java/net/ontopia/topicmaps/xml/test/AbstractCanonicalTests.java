
// $Id: AbstractCanonicalTests.java,v 1.15 2008/04/23 11:43:45 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.test.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;
import net.ontopia.utils.FileUtils;

public abstract class AbstractCanonicalTests implements TestCaseGeneratorIF {
  
  public Iterator generateTests() {
    Set tests = new HashSet();
    String base = getBaseDirectory();
        
    File indir = new File(base + getFileDirectory() + File.separator);
    if (!indir.exists())
      throw new OntopiaRuntimeException("Directory '" + indir +
                                        "' does not exist!");
    
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
        
    for (int ix = 0; ix < infiles.length; ix++) {
      if (!infiles[ix].isDirectory() && filter(infiles[ix].getName()))
        tests.add(makeTestCase(infiles[ix].getName(), base));
    }

    return tests.iterator();
  }
  
  // --- Canonicalization type methods

  /**
   * INTERNAL: Returns base directory of tests.
   */
  protected String getBaseDirectory() {
    String root = AbstractOntopiaTestCase.getTestDirectory();
    return root + File.separator + "canonical" + File.separator;
  }
  
  /**
   * INTERNAL: Returns directory to search for files in.
   */
  protected String getFileDirectory() {
    return "in";
  }

  /**
   * INTERNAL: Makes the name of the outfile (without path) given the
   * name of the infile.
   */
  protected String getOutFilename(String infile) {
    return infile;
  }
  
  /**
   * INTERNAL: Create a new test case.
   */
  protected AbstractCanonicalTestCase makeTestCase(String name, String base) {
    return new CanonicalTestCase(name, base);
  }
  
  /**
   * INTERNAL: Should return true if the specified file is to be tested.
   */
  protected abstract boolean filter(String filename);

  /**
   * INTERNAL: Performs the actual canonicalization.
   */
  protected abstract void canonicalize(String infile, String outfile)
    throws IOException;

  /**
   * INTERNAL: Returns the store factory to be used.
   */
  protected TopicMapStoreFactoryIF getStoreFactory() {
    return new InMemoryStoreFactory();
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
      
      // setup canonicalization filenames
      String in = base + File.separator + getFileDirectory() + File.separator +
        filename;
      String out = base + File.separator + "out" + File.separator +
        getOutFilename(filename);
      // produce canonical output
      canonicalize(in, out);
      
      // compare results
      assertTrue("test file " + filename + " canonicalized wrongly",
                 FileUtils.compare(out, base + File.separator + "baseline" +
                                   File.separator + getOutFilename(filename)));
    }
  }

  // -- internal

  public static String file2URL(String filename) {
    try {
      return new File(filename).toURL().toExternalForm();
    } catch (java.net.MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
