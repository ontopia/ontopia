
// $Id: AbstractCanonicalExporterTests.java,v 1.16 2008/01/11 13:29:36 geir.gronmo Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.test.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;

public abstract class AbstractCanonicalExporterTests implements TestCaseGeneratorIF {

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractOntopiaTestCase.getTestDirectory();
    String base = root + File.separator + "canonical" + File.separator;
        
    File indir = new File(base + "in" + File.separator);
        
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return java.util.Collections.EMPTY_SET.iterator();
        
    for (int ix = 0; ix < infiles.length; ix++) {
      String name = infiles[ix].getName();
      if (filter(name)) 
        tests.add(createTestCase(name, base));
    }

    return tests.iterator();
  }
  
  // --- Canonicalization type methods

  /**
   * INTERNAL: Should return true if the specified file is to be tested.
   */
  protected abstract boolean filter(String filename);

  /**
   * INTERNAL: Performs the actual canonicalization.
   */
  protected void canonicalize(String infile, String tmpfile, String outfile)
    throws IOException {
    // Get store factory
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    
    // Read document
    TopicMapIF source1 = sfactory.createStore().getTopicMap();
    if (infile.endsWith(".xtm")) {
      XTMTopicMapReader reader = new XTMTopicMapReader(new File(infile));
      reader.setValidation(false);    
      reader.importInto(source1);
    } else
      throw new OntopiaRuntimeException("Unknown syntax: " + infile);

    // Export topic map, then read it back in
    TopicMapIF source2 = exportAndReread(source1, tmpfile);
    source1.getStore().close();

    // Canonicalize reimported document
    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(file2URL(tmpfile)));      
    cwriter.write(source2);

    // Clean up
    source2.getStore().close();
  }

  /**
   * INTERNAL: Exports the topic map using the exporter to be tested,
   * then reads it back in.
   */
  protected abstract TopicMapIF exportAndReread(TopicMapIF tm, String outfile)
    throws IOException;

  /**
   * INTERNAL: Returns the store factory to be used.
   */
  protected TopicMapStoreFactoryIF getStoreFactory() {
    return new InMemoryStoreFactory();
  }

  protected AbstractCanonicalTestCase createTestCase(String name, String base) {
    return new CanonicalTestCase(name, base);
  }

  // -- internal

  protected String file2URL(String filename) {
    return AbstractCanonicalTests.file2URL(filename);
  }
  
  // --- Test case class

  public class CanonicalTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
        
    public CanonicalTestCase(String filename, String base) {
      super("testExport");
      this.filename = filename;
      this.base = base;
    }

    public void testExport() throws IOException {
      verifyDirectory(base, "out");
      
      // setup canonicalization filenames
      String in = base + File.separator + "in" + File.separator +
        filename;
      String tmp = base + File.separator + "out" + File.separator + 
        "tmp-" + filename;
      String out = base + File.separator + "out" + File.separator +
        "exp-" + filename;
      // produce canonical output
      try {
        canonicalize(in, tmp, out);
      } catch (Throwable e) {
        if (e instanceof OntopiaRuntimeException &&
            ((OntopiaRuntimeException) e).getCause() != null)
          e = ((OntopiaRuntimeException) e).getCause();
        throw new OntopiaRuntimeException("Error processing file '" + filename +
                                          "': " + e, e);
      }

      // compare results
      String baseline = base + File.separator + "baseline" + File.separator +
                        filename;
      assertTrue("test file " + filename + " canonicalized wrongly (" + baseline
                 + " != " + out + "), tmp=" + tmp,
                 FileUtils.compare(out, baseline));
      // NOTE: we compare out/exp-* and baseline/*
    }
  }
  
}
