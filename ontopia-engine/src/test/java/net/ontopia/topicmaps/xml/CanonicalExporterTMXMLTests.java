
package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;

import java.util.List;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;
import org.junit.runners.Parameterized.Parameters;

public class CanonicalExporterTMXMLTests
  extends AbstractCanonicalExporterTests {
  
  private final static String testdataDirectory = "canonical";

  public CanonicalExporterTMXMLTests(String root, String filename) {
    this.filename = filename;
    this.base = FileUtils.getTestdataOutputDirectory() + testdataDirectory;
    this._testdataDirectory = testdataDirectory;
  }

  @Parameters
  public static List generateTests() {
    ResourcesFilterIF filter = new ResourcesFilterIF() {
      public boolean ok(String resourcePath) {
        // Ignore importInto-specific file.
        if (resourcePath.endsWith("multiple-tms-read.xtm") ||
            resourcePath.endsWith("bug750.xtm") ||
            resourcePath.endsWith("empty-member.xtm") ||
            resourcePath.endsWith("empty.xtm") ||
            resourcePath.endsWith("whitespace.xtm"))
          return false;

        return resourcePath.endsWith(".xtm");
      }
    };
    return FileUtils.getTestInputFiles(testdataDirectory, "in", filter);
  }

  protected String getTestdataDirectory() {
    return testdataDirectory;
  }

  // --- Canonicalization type methods

  protected TopicMapIF exportAndReread(TopicMapIF topicmap, String outfile)
    throws IOException {
    // First we export
    TMXMLWriter writer = new TMXMLWriter(outfile);
    writer.write(topicmap);
    writer.close();

    // Then we read back in
    TopicMapIF topicmap2 = getStoreFactory().createStore().getTopicMap();
    TMXMLReader reader = new TMXMLReader(outfile);
    reader.importInto(topicmap2);
    return topicmap2;
  }  
}
