
// $Id: RDFWriterTestGenerator.java,v 1.10 2009/05/25 05:48:17 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.rdf.test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.nav.utils.deciders.TMDecider;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.utils.ltm.test.LTMTopicMapWriterTestGenerator.FilterTestCase;
import net.ontopia.topicmaps.utils.rdf.*;
import net.ontopia.topicmaps.xml.test.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RDFWriterTestGenerator implements TestCaseGeneratorIF {

  public Iterator generateTests() {
    Set tests = new HashSet();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "tm2rdf" + File.separator;

    File indir = new File(base + "in" + File.separator);

    // canonical tests
    File[] infiles = indir.listFiles();
    if (infiles != null) {
      for (int ix = 0; ix < infiles.length; ix++) {
        String name = infiles[ix].getName();
        if (name.endsWith(".xtm") || name.endsWith(".ltm"))
          tests.add(new CanonicalTestCase(name, base));
      }
    }

    // Create test cases from each topic map file in 'filter-in'.
    File filterIndir = new File(base + "filter-in" + File.separator);
    File[] filterInfiles = filterIndir.listFiles();
    if (filterInfiles != null)
      for (int i = 0; i < filterInfiles.length; i++) {
        String name = filterInfiles[i].getName();
        if (name.endsWith(".ltm")
            || name.endsWith(".rdf") || name.endsWith(".xtm"))
          tests.add(new FilterTestCase(name, base));
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
      verifyDirectory(base, "tmp");

      // export
      String in = base + File.separator + "in" + File.separator + filename;
      String tmp = base + File.separator + "tmp" + File.separator + filename;
      String bline = base + File.separator + "baseline" + File.separator
          + filename;
      TopicMapReaderIF reader = ImportExportUtils.getReader(in);
      if (reader instanceof XTMTopicMapReader)
        ((XTMTopicMapReader) reader).setValidation(false);
      TopicMapIF tm = reader.read();
      new RDFTopicMapWriter(new FileOutputStream(tmp)).write(tm);

      // read in base line and export
      Model baseline = ModelFactory.createDefaultModel().read(new FileInputStream(bline), "file:"
          + bline, "RDF/XML");
      Model result = ModelFactory.createDefaultModel().read(new FileInputStream(tmp), "file:"
          + tmp, "RDF/XML");

      // compare results
      assertTrue("test file " + filename + " produced non-isomorphic model: " + 
								 bline + " " + tmp,
          result.isIsomorphicWith(baseline));
    }
  }

  // --- Canonical test case class

  public class FilterTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
    private String syntax;

    public FilterTestCase(String filename, String base) {
      super("testFile");
      this.filename = filename;
      this.base = base;
    }

    public FilterTestCase(String filename, String base, String syntax) {
      this(filename, base);
      this.syntax = syntax;
    }

    public void testFile() throws IOException {
      // setup
      verifyDirectory(base, "out");
      verifyDirectory(base, "filter-tmp");
      String in = base + File.separator + "filter-in" + File.separator
          + filename;
      String tmp = base + File.separator + "filter-tmp" + File.separator
          + filename + ".rdf";
      String bline = base + File.separator + "filter-baseline" + File.separator
          + filename + ".rdf";

      // Import
      TopicMapIF tm = ImportExportUtils.getReader(in).read();

      // Export the topic map to rdf
      RDFTopicMapWriter rdfWriter = new RDFTopicMapWriter(new FileOutputStream(
          tmp));
      rdfWriter.setFilter(new TMDecider());
      try {
        rdfWriter.write(tm);
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Exception in RDF file '" + tmp, e);
      }
      // read in base line and export
      Model baseline = ModelFactory.createDefaultModel().read(new FileInputStream(bline), "file:"
          + bline, "RDF/XML");
      Model result = ModelFactory.createDefaultModel().read(new FileInputStream(tmp), "file:"
          + tmp, "RDF/XML");

      // compare results
      assertTrue("test file " + filename + " produced non-isomorphic model",
          result.isIsomorphicWith(baseline));
    }
  }
}
