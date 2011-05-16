
// $Id: ContentStoreUtils.java,v 1.3 2003/10/30 11:18:29 grove Exp $

package net.ontopia.infoset.content;

import java.util.Map;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Utility methods for accessing the content store.
 */
public class ContentStoreUtils {

  /**
   * Returns a content store for content stored in the given topic map.
   * @param topicmap The topic map
   * @param properties String properties for configuring the store.
   */
  public static ContentStoreIF getContentStore(TopicMapIF topicmap, Map<?, ?> properties) {
    if (topicmap instanceof net.ontopia.topicmaps.impl.rdbms.TopicMap)
      return JDBCContentStore.getInstance(topicmap);
    else
      return InMemoryContentStore.getInstance(topicmap);
  }
  
}
