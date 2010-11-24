// $Id: ObjectLookupTests.java,v 1.7 2008/06/13 08:36:26 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms.test;

import java.io.File;
import java.io.IOException;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.URIUtils;

/**
 * INTERNAL: Tests that verify that LocatorIF lookups work correctly
 * in a committed and reopened topic map store.
 */

public class ObjectLookupTests extends AbstractTopicMapTestCase {
  
  public ObjectLookupTests(String name) {
    super(name);
  }

  public void testLookups() throws IOException {
    
    File file = new File(resolveFileName("various", "topicmap-object-lookup.xtm"));
    URILocator base = new URILocator(URIUtils.toURL(file));

    // Load topic map, commit and close
    RDBMSTopicMapStore store = new RDBMSTopicMapStore();
    TopicMapIF tm = store.getTopicMap();
    TopicMapImporterIF importer = new XTMTopicMapReader(file);
    importer.importInto(tm);
    long topicmap_id = Long.parseLong(tm.getObjectId().substring(1));    
    store.commit();
    store.close();
    
    // Reopen store
    store = new RDBMSTopicMapStore(topicmap_id);
    tm = store.getTopicMap();

    // Test TopicMapIF.getTopicBySubjectIdentifier
    TopicIF topic = tm.getTopicBySubjectIdentifier(new URILocator("http://test.ontopia.net/indicator1"));
    assertTrue("topic not found by indicator [1]", topic != null);
    topic = tm.getTopicBySubjectIdentifier(new URILocator("http://test.ontopia.net/indicator2"));
    assertTrue("topic not found by indicator [2]", topic != null);
    
    // Test TopicMapIF.getObjectByItemIdentifier
    TMObjectIF tmobject = tm.getObjectByItemIdentifier(base.resolveAbsolute("#topicA"));
    assertTrue("tmobject not found by source locator [A]", tmobject != null);
    tmobject = tm.getObjectByItemIdentifier(base.resolveAbsolute("#topicB"));
    assertTrue("tmobject not found by source locator [B]", tmobject != null);
    tmobject = tm.getObjectByItemIdentifier(base.resolveAbsolute("#topicC"));
    assertTrue("tmobject not found by source locator [C]", tmobject != null);

    // Test TopicMapIF.getTopicBySubject
    topic = tm.getTopicBySubjectLocator(new URILocator("http://test.ontopia.net/subject"));
    assertTrue("topic not found by subject", topic != null);

    store.delete(true);
    //! store.close();    
  }
  
}





