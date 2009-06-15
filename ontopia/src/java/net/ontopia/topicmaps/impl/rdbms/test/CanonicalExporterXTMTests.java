
// $Id: CanonicalExporterXTMTests.java,v 1.8 2004/11/19 12:52:47 grove Exp $

package net.ontopia.topicmaps.impl.rdbms.test;

import java.io.File;
import java.io.IOException;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.infoset.impl.basic.URILocator;

public class CanonicalExporterXTMTests extends net.ontopia.topicmaps.xml.test.CanonicalExporterXTMTests {

  protected void canonicalize(String infile, String tmpfile, String outfile) throws IOException {
    // Import document
    TopicMapStoreIF store1 = new RDBMSTopicMapStore();
    TopicMapIF source1 = store1.getTopicMap();

    // Get hold of topic map id
    long topicmap_id1 = Long.parseLong(source1.getObjectId().substring(1));
    
    XTMTopicMapReader reader = new XTMTopicMapReader(new File(infile));
    reader.setValidation(false);
    reader.importInto(source1);
    store1.commit();
    store1.close();

    // Export document
    TopicMapStoreIF store2 = new RDBMSTopicMapStore(topicmap_id1);
    ((AbstractTopicMapStore)store2).setBaseAddress(new URILocator(new File(infile).toURL()));
    TopicMapIF source2 = store2.getTopicMap();
    new XTMTopicMapWriter(new File(tmpfile)).write(source2);
    store2.delete(true);

    // Read exported document
    TopicMapStoreIF store3 = new RDBMSTopicMapStore();
    TopicMapIF source3 = store3.getTopicMap();
    
    reader = new XTMTopicMapReader(new File(tmpfile));
    reader.setValidation(false);
    reader.importInto(source3);
    store3.commit();
    store3.close();

    // Get hold of topic map id
    long topicmap_id3 = Long.parseLong(source3.getObjectId().substring(1));

    // Canonicalize document
    TopicMapStoreIF store4 = new RDBMSTopicMapStore(topicmap_id3);
    TopicMapIF source4 = store4.getTopicMap();

    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(file2URL(tmpfile)));      
    cwriter.write(source4);

    // Make sure topic map goes away
    store4.delete(true);
    //! store4.close();
  }
  
}





