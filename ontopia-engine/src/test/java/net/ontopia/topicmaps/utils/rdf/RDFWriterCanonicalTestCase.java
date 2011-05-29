
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
public class RDFWriterCanonicalTestCase {

  private final static String testdataDirectory = "tm2rdf";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", ".xtm|.ltm|.ctm");
  }

  // --- Canonical test case class

    private String base;
    private String filename;

    public RDFWriterCanonicalTestCase(String root, String filename) {
      this.filename = filename;
      this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    }

    @Test
    public void testFile() throws IOException {
      FileUtils.verifyDirectory(base, "out");
      FileUtils.verifyDirectory(base, "tmp");

      // export
      String in = FileUtils.getTestInputFile(testdataDirectory, "in", filename);
      String tmp = base + File.separator + "tmp" + File.separator + filename;
      String bline = FileUtils.getTestInputFile(testdataDirectory, "baseline",
          filename);
      TopicMapReaderIF reader = ImportExportUtils.getReader(in);
      if (reader instanceof XTMTopicMapReader)
        ((XTMTopicMapReader) reader).setValidation(false);
      TopicMapIF tm = reader.read();
      FileOutputStream fos = new FileOutputStream(tmp);
      new RDFTopicMapWriter(fos).write(tm);
      fos.close();

      // read in base line and export
      Model baseline = ModelFactory.createDefaultModel().read(StreamUtils.getInputStream(bline),
          bline, "RDF/XML");
      Model result = ModelFactory.createDefaultModel().read(new FileInputStream(tmp), "file:"
          + tmp, "RDF/XML");

      // compare results
      Assert.assertTrue("test file " + filename + " produced non-isomorphic model: " + 
								 bline + " " + tmp,
          result.isIsomorphicWith(baseline));
    }
}
