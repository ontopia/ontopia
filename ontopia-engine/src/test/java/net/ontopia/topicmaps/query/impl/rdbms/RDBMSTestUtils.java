
package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTestFactory;
import net.ontopia.topicmaps.query.core.AbstractQueryTest;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import org.xml.sax.SAXException;
import org.junit.Ignore;

@Ignore
public class RDBMSTestUtils {

  private final static String testdataDirectory = "query";

  // ===== Helper methods (topic maps)

  public static void load(AbstractQueryTest test, String filename) throws IOException {
    filename = TestFileUtils.getTestInputFile(testdataDirectory, filename);

    checkDatabasePresence();

    RDBMSTopicMapStore store = new RDBMSTopicMapStore();
    test.topicmap = store.getTopicMap();
    test.builder = store.getTopicMap().getBuilder();
    test.base = URIUtils.getURI(filename);
    store.setBaseAddress(test.base);

    TopicMapImporterIF importer = ImportExportUtils.getImporter(filename);
    if (importer instanceof XTMTopicMapReader)
      ((XTMTopicMapReader) importer).setValidation(false);
    importer.importInto(test.topicmap);

    // NOTE: Query processor must have base set, because of the way
    // the test suite looks up source locators.
    //! test.processor = new QueryProcessor(test.topicmap, test.base);
    test.processor = QueryUtils.createQueryProcessor(test.topicmap, test.base);
  }

  private static void checkDatabasePresence() throws IOException {
    try {
      RDBMSTestFactory.checkDatabasePresence();
    } catch (SQLException e) {
      throw new IOException(e);
    } catch (SAXException e) {
      throw new IOException(e);
    }
  }

  public static void makeEmpty(AbstractQueryTest test) {
    try {
      checkDatabasePresence();
      RDBMSTopicMapStore store = new RDBMSTopicMapStore();
      test.topicmap = store.getTopicMap();
      test.builder = test.topicmap.getBuilder();
      //! test.processor = new QueryProcessor(test.topicmap);
      String filename = TestFileUtils.getTestInputFile(testdataDirectory, "");
      test.processor = QueryUtils.createQueryProcessor(test.topicmap, URIUtils.getURI(filename));
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
