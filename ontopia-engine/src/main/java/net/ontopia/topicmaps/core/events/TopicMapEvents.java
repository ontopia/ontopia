
package net.ontopia.topicmaps.core.events;

import net.ontopia.topicmaps.entry.AbstractTopicMapReference;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

/**
 * PUBLIC: Utility class for registering topic map life cycle
 * events.</p>
 *
 * <p>There is currently only listener support for topics added or
 * removed. The listeners will receive callbacks after the transaction
 * has been committed. This means that the listeners will only see
 * committed changes.</p>
 *
 * <p>Use the <code>addTopicListener</code> and
 * <code>removeTopicListener</code> methods to register and unregister
 * your listeners.</p>
 *
 * <p>Here is an example of how you can register a listener:</p>
 *
 * <pre>
 * TopicMapRepositoryIF rep = TopicMaps.getRepository();
 * String topicmapId = "mytopicmap";
 * TopicMapReferenceIF ref = repository.getReferenceByKey(topicmapId);
 * TopicMapListenerIF myListener = new MyTopicListener();
 * TopicMapEvents.addTopicListener(ref, myListener);
 * </pre>
 *
 * <p>Note that you should only register your listener <i>once</i>.</p>
 *
 * @since 3.1
 */

public class TopicMapEvents {

  private TopicMapEvents() {
    // can't call this one
  }
  
  /**
   * PUBLIC: Call this method to register a topic listener with a
   * given topic map. The topic listener will receive callbacks on the
   * objectAdded and objectRemoved when a transaction has been
   * committed that has added topics to or removed topics from the
   * given topic map.
   */
  public static void addTopicListener(TopicMapReferenceIF topicmapRef, TopicMapListenerIF listener) {
    if (topicmapRef instanceof AbstractTopicMapReference) {
      ((AbstractTopicMapReference)topicmapRef).addTopicListener(listener);
    } else {
      throw new UnsupportedOperationException("Topic map reference does not support events: " + topicmapRef);
    }
  }

  /**
   * PUBLIC: Call this method to unregister a topic listener with a
   * given topic map. This is the opposite of calling
   * addTopicListener.
   */
  public static void removeTopicListener(TopicMapReferenceIF topicmapRef, TopicMapListenerIF listener) {
    if (topicmapRef instanceof AbstractTopicMapReference) {
      ((AbstractTopicMapReference)topicmapRef).removeTopicListener(listener);
    } else {
      throw new UnsupportedOperationException("Topic map reference does not support events: " + topicmapRef);
    }
  }
  
}
