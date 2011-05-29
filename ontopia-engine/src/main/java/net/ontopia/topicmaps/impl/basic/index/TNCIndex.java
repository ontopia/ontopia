
package net.ontopia.topicmaps.impl.basic.index;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;

/**
 * INTERNAL: Index providing lookups from base name value + scope to
 * the topic that has such base name characteristics. (TNC = Topic
 * Naming Constraint).
 */

public class TNCIndex implements IndexIF {

  protected NameIndexIF nameix;
  
  public TNCIndex(NameIndexIF nameix) {
    this.nameix = nameix;
  }

  public TNCIndex(TopicMapIF topicmap) {
    nameix = (NameIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");
  }

  // --- TNCIndex methods

  /**
   * INTERNAL: Returns the topics that have a basename with the given
   * string value in the given scope.<p>
   *
   * This method is used to look up topics in the socalled topic name
   * space. Note that whether a single topic is returned depends on
   * whether the topic map have been completely processed or not.
   */
  public Collection getTopics(String basename_string, Collection scope) {
    HashSet topics = new HashSet();
    Iterator it = nameix.getTopicNames(basename_string).iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();
      if (bn.getScope().equals(scope))
        topics.add(bn.getTopic());
    }

    return topics;
  }

}
