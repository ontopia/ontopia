
// $Id: AbstractCanonicalExporterTests.java,v 1.16 2008/01/11 13:29:36 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import net.ontopia.utils.URIUtils;

@RunWith(Parameterized.class)
public abstract class AbstractCanonicalExporterTests {

  // --- Canonicalization type methods

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
      XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(infile));
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

  // -- internal

  protected String file2URL(String filename) {
    return AbstractCanonicalTests.file2URL(filename);
  }
  
  // --- Test case class

    protected String base;
    protected String filename;
    protected String _testdataDirectory;

    @Test
    public void testExport() throws IOException {
      FileUtils.verifyDirectory(base, "out");
      
      // setup canonicalization filenames
      String in = FileUtils.getTestInputFile(_testdataDirectory, "in", 
        filename);
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
      String baseline = FileUtils.getTestInputFile(_testdataDirectory, "baseline", 
                        filename);
      Assert.assertTrue("test file " + filename + " canonicalized wrongly (" + baseline
                 + " != " + out + "), tmp=" + tmp,
                 FileUtils.compareFileToResource(out,baseline));
      // NOTE: we compare out/exp-* and baseline/*
  }
  
}
