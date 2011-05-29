
package net.ontopia.topicmaps.impl.tmapi2.index;

import java.util.HashMap;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.impl.tmapi2.NameImpl;
import net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl;

import org.tmapi.core.Name;

/**
 * Helper class to index the {@link TopicNameIF} to there {@link Name} wrapper.
 */

public class NameIndex {

  private final TopicMapImpl topicMap;
  private Map<TopicNameIF, NameImpl> map = new HashMap<TopicNameIF, NameImpl>();

  public NameIndex(TopicMapImpl topicMap) {
    this.topicMap = topicMap;
  }
  
  public NameImpl getName(TopicNameIF name) {
    return map.get(name);
  }

  public void addName(Name name) {
    map.put(topicMap.unwrapName(name), (NameImpl) name);
  }
}
