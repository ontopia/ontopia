
// $Id: TestUtils.java,v 1.1 2008/07/04 10:22:30 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.util.Iterator;
import java.util.ArrayList;
import java.net.MalformedURLException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.AbstractTopicMapExporter;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

/**
 * INTERNAL: Utility methods used by the various tests in this package.
 */
public class TestUtils {

  /**
   * INTERNAL: Fixes up internal item identifiers on topic maps that
   * have been imported, exported to a temporary XTM, and then
   * reimported.  The idea is to ensure that they canonicalize
   * correctly.
   */
  public static void fixItemIds(TopicMapIF tm, LocatorIF origbaseloc)
    throws MalformedURLException {
    String origbase = origbaseloc.getAddress();
    String base = tm.getStore().getBaseAddress().getAddress();
    Iterator it = tm.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = new ArrayList(topic.getItemIdentifiers()).iterator();
      while (it2.hasNext()) {
        LocatorIF itemid = (LocatorIF) it2.next();
        String iid = itemid.getAddress();
        if (iid.startsWith(base)) {
          if (AbstractTopicMapExporter.mayCollide(iid.substring(base.length() + 1)))
            topic.removeItemIdentifier(itemid);
          else {
            topic.removeItemIdentifier(itemid);
            String tmp = iid.substring(base.length());
            topic.addItemIdentifier(new URILocator(origbase + tmp));
          }
        }
      }
    }

    if (tm.getStore() instanceof InMemoryTopicMapStore)
      ((InMemoryTopicMapStore) tm.getStore()).setBaseAddress(origbaseloc);
  }
  
}
