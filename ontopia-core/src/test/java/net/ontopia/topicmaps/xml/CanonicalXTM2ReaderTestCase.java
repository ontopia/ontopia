
// $Id: CanonicalXTM2ReaderTestGenerator.java,v 1.1 2008/04/23 11:43:45 lars.garshol Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.runners.Parameterized.Parameters;

public class CanonicalXTM2ReaderTestCase extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "xtm2";

  public CanonicalXTM2ReaderTestCase(String root, String filename) {
    this.filename = filename;
    this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", ".xtm");
  }

  // --- Canonicalization type methods

  protected void canonicalize(String infile, String outfile)
    throws IOException {
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(infile));
    reader.setValidation(false);
    // FIXME: should we do a setXTM2Required(true) or something?
    reader.setStoreFactory(sfactory);
    TopicMapIF source = reader.read();

    FileOutputStream out = new FileOutputStream(outfile);
    CanonicalXTMWriter cwriter = new CanonicalXTMWriter(out);
    cwriter.write(source);
    out.close();

    source.getStore().close();
  }

  protected String getOutFilename(String infile) {
    return infile + ".cxtm";
  }
}
