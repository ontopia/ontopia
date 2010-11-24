
// $Id: RDBMSTestUtils.java,v 1.7 2008/01/11 12:58:56 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms.test;

import java.io.File;
import java.io.IOException;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.query.core.test.AbstractQueryTest;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;

public class RDBMSTestUtils {
  
  // ===== Helper methods (topic maps)

  public static void load(AbstractQueryTest test, String filename) throws IOException {
    File file = new File(test.resolveFileName("query", filename));    
    
    RDBMSTopicMapStore store = new RDBMSTopicMapStore();
    test.topicmap = store.getTopicMap();    
    test.builder = store.getTopicMap().getBuilder();
    test.base = new URILocator(URIUtils.toURL(file));
    store.setBaseAddress(test.base);
    
    TopicMapImporterIF importer = ImportExportUtils.getImporter(file.toString());
    importer.importInto(test.topicmap);

    // NOTE: Query processor must have base set, because of the way
    // the test suite looks up source locators.
    //! test.processor = new QueryProcessor(test.topicmap, test.base);
    test.processor = QueryUtils.createQueryProcessor(test.topicmap, test.base);
  }
  
  public static void makeEmpty(AbstractQueryTest test) {
    try {
      RDBMSTopicMapStore store = new RDBMSTopicMapStore();
      test.topicmap = store.getTopicMap();
      test.builder = test.topicmap.getBuilder();
      //! test.processor = new QueryProcessor(test.topicmap);
      test.processor = QueryUtils.createQueryProcessor(test.topicmap);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
