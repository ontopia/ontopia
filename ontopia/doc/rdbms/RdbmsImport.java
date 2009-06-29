
import java.io.File;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;

/**
 * EXAMPLE: A simple command line tool that imports an XTM topic map
 * document into a relational database.
 */

public class RdbmsImport {

  public static void main(String[] argv) throws Exception {

    // Usage:
    // 
    //    java RdbmsImport <propfile> <xtmfile>
    //    java RdbmsImport /tmp/myprops.xml /tmp/hello.xtm

    System.err.println("Connecting...");
    String propfile = argv[0];
    String xtmfile = argv[1];
    
    // Create a new topic map store that will add a new topic map to
    // the database. Note that we are not using store pooling nor a
    // shared cache here, because there are no other users. After the
    // import things might be different and we should consider
    // enabling both store pooling and shared cache.
    TopicMapStoreIF store = new RDBMSTopicMapStore(propfile);

    // Get the new topic map object
    TopicMapIF tm = store.getTopicMap();
    
    // Import the XTM document into the topic map object
    TopicMapImporterIF reader = new XTMTopicMapReader(new File(xtmfile));
    reader.importInto(tm);
    System.err.println("Imported (id " + tm.getObjectId() + ").");

    // Commit the transaction
    store.commit();
    
    // Close store (and database connection)
    store.close();

    System.err.println("Done.");    
  }
}
