
package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.GrabberIF;

/**
 * INTERNAL: Grabber that grabs the most appropriate basename from a topic.
 */
public class TopicNameGrabber implements GrabberIF<TopicIF, TopicNameIF> {

  /**
   * PROTECTED: The comparator used to sort the base names.
   */
  protected Comparator<TopicNameIF> comparator;
 
  /**
   * INTERNAL: Creates a grabber; uses a BaseComparator with the given
   * scope.
   *
   * @param scope A scope; a collection of TopicIF objects.
   */
  public TopicNameGrabber(Collection<TopicIF> scope) {
    this.comparator = new TopicNameComparator(scope);
  }
  
  /**
   * INTERNAL: Creates a grabber which uses the given comparator.
   *
   * @param comparator The given comparator
   */
  public TopicNameGrabber(Comparator<TopicNameIF> comparator) {
    this.comparator = comparator;
  }
  
  /**
   * INTERNAL: Grabs the most appropriate base name for the given topic,
   * using the comparator established at creation to compare available
   * base names.
   *
   * @param topic A topic; formally an Object, but must implement TopicIF.
   *
   * @exception throws OntopiaRuntimeException if the given topic is not
   *            a TopicIF object.
   */
  public TopicNameIF grab(TopicIF topic) {
    if (topic == null)
      return null;
    
    List<TopicNameIF> basenames = new ArrayList<TopicNameIF>(topic.getTopicNames());

    // If there is no base name return null
    if (basenames.isEmpty())
      return null;

    // If there are multiple basenames rank them.    
    Collections.sort(basenames, comparator);
    return basenames.iterator().next();
  }

}
