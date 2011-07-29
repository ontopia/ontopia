
package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.infoset.impl.basic.URILocator;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;
import net.ontopia.utils.URIUtils;
import org.junit.runners.Parameterized.Parameters;

public class CanonicalXTMimportIntoTests extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "canonical";

  public CanonicalXTMimportIntoTests(String root, String filename) {
    this.filename = filename;
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    ResourcesFilterIF filter = new ResourcesFilterIF() {
      public boolean ok(String resourcePath) {
        // Ignore importInto-specific file.
        if (resourcePath.endsWith("multiple-tms-read.xtm")) return false;

        if (resourcePath.endsWith(".xtm"))
          return true;
        else
          return false;
      }
    };
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", filter);
  }

  // --- Canonicalization type methods

  protected void canonicalize(String infile, String outfile) throws IOException {
    // Get store factory
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    TopicMapStoreIF store = sfactory.createStore();

    // Read document
    TopicMapIF source = store.getTopicMap();
    XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(infile));
    reader.setValidation(false);
    reader.importInto(source);

    // Canonicalize document
    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(file2URL(infile)));      
    cwriter.write(source);

    store.close();
  }
  
}
