
import java.util.Iterator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource;

/**
 * EXAMPLE: A simple command line tool that lists the topic maps that
 * are stored in a relational database.
 */

public class RdbmsList {

  public static void main(String[] argv) throws Exception {

    // Usage:
    // 
    //    java RdbmsList <propfile>
    //    java RdbmsList /tmp/myprops.xml 

    System.err.println("Connecting...");
    String propfile = argv[0];

    // create a source which can enumerate the topic maps
    RDBMSTopicMapSource source = new RDBMSTopicMapSource();
    source.setPropertyFile(propfile);
    Iterator tms = source.getReferences().iterator();

    while (tms.hasNext()) {
      TopicMapReferenceIF ref = null;
      TopicMapStoreIF store = null;

      try {
        ref = (TopicMapReferenceIF) tms.next();
        store = ref.createStore(true);
        TopicMapIF tm = store.getTopicMap();
        
        System.err.println("Topic Map ID: " + tm.getObjectId());
        System.err.println("  Topics: " + tm.getTopics().size());
        System.err.println("  Associations: " + tm.getAssociations().size());
        store.close();
      } 
      finally {
        if (store != null) store.close();
        if (ref != null) ref.close();
      }
    }
    
    System.err.println("Done.");    
  }
}
