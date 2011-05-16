
// $Id: CanonicalXTMreadTests.java,v 1.10 2008/01/11 13:29:36 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.infoset.impl.basic.URILocator;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;
import net.ontopia.utils.URIUtils;
import org.junit.runners.Parameterized.Parameters;

public class CanonicalXTMreadTests extends AbstractCanonicalTests {
  
  private final static String testdataDirectory = "canonical";

  public CanonicalXTMreadTests(String root, String filename) {
    this.filename = filename;
    this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    ResourcesFilterIF filter = new ResourcesFilterIF() {
      public boolean ok(String resourcePath) {
        // Ignore importInto-specific file.
        if (resourcePath.endsWith("multiple-tms-importInfo.xtm")) return false;

        return resourcePath.endsWith(".xtm");
      }
    };
    return FileUtils.getTestInputFiles(testdataDirectory, "in", filter);
  }

  // --- Canonicalization type methods

  protected void canonicalize(String infile, String outfile) throws IOException {
    TopicMapStoreFactoryIF sfactory = getStoreFactory();
    XTMTopicMapReader reader = new XTMTopicMapReader(URIUtils.getURI(infile));
    reader.setValidation(false);
    reader.setStoreFactory(sfactory);
    TopicMapIF source = reader.read();

    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(file2URL(infile)));      
    cwriter.write(source);

    source.getStore().close();
  }
  
}
