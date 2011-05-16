
// $Id: RDFWriterTestGenerator.java,v 1.10 2009/05/25 05:48:17 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.rdf;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.utils.deciders.TMDecider;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import net.ontopia.utils.FileUtils;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.URIUtils;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RDFWriterFilterTestCase {

  private final static String testdataDirectory = "tm2rdf";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "filter-in", ".ltm|.rdf|.xtm");
  }

  // --- Canonical test case class

    private String base;
    private String filename;

    public RDFWriterFilterTestCase(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testFile() throws IOException {
      // setup
      FileUtils.verifyDirectory(base, "out");
      FileUtils.verifyDirectory(base, "filter-tmp");
      String in = FileUtils.getTestInputFile(testdataDirectory, "filter-in",
          filename);
      String tmp = base + File.separator + "filter-tmp" + File.separator
          + filename + ".rdf";
      String bline = FileUtils.getTestInputFile(testdataDirectory, "filter-baseline",
          filename + ".rdf");

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
      Model baseline = ModelFactory.createDefaultModel().read(StreamUtils.getInputStream(bline), 
          bline, "RDF/XML");
      Model result = ModelFactory.createDefaultModel().read(new FileInputStream(tmp), "file:"
          + tmp, "RDF/XML");

      // compare results
      Assert.assertTrue("test file " + filename + " produced non-isomorphic model",
          result.isIsomorphicWith(baseline));
    }
}
