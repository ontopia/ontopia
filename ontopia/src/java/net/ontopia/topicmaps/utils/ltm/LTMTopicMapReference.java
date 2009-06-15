
// $Id: LTMTopicMapReference.java,v 1.8 2005/07/13 09:00:31 grove Exp $

package net.ontopia.topicmaps.utils.ltm;

import java.io.IOException;
import java.net.URL;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.AbstractURLTopicMapReference;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;

/**
 * INTERNAL: An LTM file topic map reference.
 */

public class LTMTopicMapReference extends AbstractURLTopicMapReference {
  
  public LTMTopicMapReference(URL url, String id, String title) {
    super(id, title, url, null);
  }
  
  public LTMTopicMapReference(URL url, String id, String title, LocatorIF base_address) {
    super(id, title, url, base_address);
  }

  protected TopicMapIF loadTopicMap(boolean readonly) throws IOException {
    LTMTopicMapReader reader;
    if (base_address == null)
      reader = new LTMTopicMapReader(url.toString());
    else
      reader = new LTMTopicMapReader(new org.xml.sax.InputSource(url.toString()), base_address);      
    
    // Load topic map
    TopicMapStoreIF store = new InMemoryTopicMapStore();
    TopicMapIF tm = store.getTopicMap();
    reader.importInto(tm);
    
    if (tm == null)
      throw new IOException("No topic map was found in: " + url);
    // Suppress duplicates
    if (getDuplicateSuppression())
      DuplicateSuppressionUtils.removeDuplicates(tm);
    return tm;
  }
  
}
