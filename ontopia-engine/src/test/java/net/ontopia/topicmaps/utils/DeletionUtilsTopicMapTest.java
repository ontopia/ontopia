
package net.ontopia.topicmaps.utils;

import java.io.*;
import java.util.*;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DeletionUtilsTopicMapTest {

  private final static String testdataDirectory = "canonical";

  @Parameters
  public static List generateTests() {
    return FileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm|.xtm");
  }

  private String filename;

  public DeletionUtilsTopicMapTest(String root, String filename) {
    this.filename = filename;
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    return store.getTopicMap();
  }

  // --- Test cases

  @Test
  public void testTopicMapDeletion() throws Exception {
    String name = FileUtils.getTestInputFile(testdataDirectory, "in", filename);
    TopicMapIF tm = makeTopicMap();
    TopicMapImporterIF importer = ImportExportUtils.getImporter(name);
    if (name.endsWith(".xtm"))
      ((XTMTopicMapReader) importer).setValidation(false);
    try {
      importer.importInto(tm);
    } catch (OntopiaRuntimeException ore) {
      // catch and re-throw to add filename to message
      throw new OntopiaRuntimeException(ore.getMessage() + " in " + name, ore);
    }
    clearTopicMap(tm);
    tm.getStore().close();
  }

  // --- Helper methods
  
  private void clearTopicMap(TopicMapIF tm) throws Exception {

    // Remove all the objects from the topic map
    tm.clear();

    Assert.assertTrue("Not all topics was deleted", tm.getTopics().isEmpty());
    Assert.assertTrue("Not all associations was deleted", tm.getAssociations().isEmpty());
  }
  
}
