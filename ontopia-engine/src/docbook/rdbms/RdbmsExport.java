
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;

/**
 * EXAMPLE: A simple command line tool that exports an topic map
 * stored in a relational database to an XTM document. The output will
 * be written to stdout.
 */

public class RdbmsExport {

  public static void main(String[] argv) throws Exception {

    // Usage:
    // 
    //    java RdbmsExport <propfile> <topicmap-id>
    //    java RdbmsExport /tmp/myprops.xml 8552

    System.err.println("Connecting...");
    String propfile = argv[0];
    int topicmap_id = Integer.parseInt(argv[1]);
    
    // Create a new topic map store that references the topic map with
    // the given id. Note that we are not using store pooling nor a
    // shared cache here, but if this had been a multi-user
    // application then should have used one.
    TopicMapStoreIF store = new RDBMSTopicMapStore(propfile, topicmap_id);

    // Get the referenced topic map object
    TopicMapIF tm = store.getTopicMap();
    
    // Export the topic map in XTM form
    XTMTopicMapWriter writer = new XTMTopicMapWriter(System.out, "utf-8");
    writer.write(tm);
    
    // Close store (and database connection)
    store.close();

    System.err.println("Done.");    
  }
}
