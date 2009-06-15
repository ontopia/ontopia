
// $Id: AbstractRDBMSQueryTest.java,v 1.5 2005/07/13 08:54:59 grove Exp $

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

public class AbstractRDBMSQueryTest extends AbstractQueryTest {
  
  public AbstractRDBMSQueryTest(String name) {
    super(name);
  }
  
  // ===== Helper methods (topic maps)

  protected void load(String filename) throws IOException {
    File file = new File(resolveFileName("query", filename));    

    RDBMSTopicMapStore store = new RDBMSTopicMapStore();
    topicmap = store.getTopicMap();    
    base = new URILocator(file.toURL());
    
    TopicMapImporterIF importer = ImportExportUtils.getImporter(file.toString());
    importer.importInto(topicmap);

    // NOTE: Query processor must have base set, because of the way
    // the test suite looks up source locators.
    //! processor = new QueryProcessor(topicmap, base);
    processor = QueryUtils.createQueryProcessor(topicmap, base);
  }
  
  protected void makeEmpty() {
    try {
      RDBMSTopicMapStore store = new RDBMSTopicMapStore();
      topicmap = store.getTopicMap();
      //! processor = new QueryProcessor(topicmap);
      processor = QueryUtils.createQueryProcessor(topicmap);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
