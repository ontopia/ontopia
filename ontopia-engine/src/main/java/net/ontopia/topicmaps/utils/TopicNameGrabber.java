
package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Grabber that grabs the most appropriate basename from a topic.
 */
public class TopicNameGrabber implements GrabberIF {

  /**
   * PROTECTED: The comparator used to sort the base names.
   */
  protected Comparator comparator;
 
  /**
   * INTERNAL: Creates a grabber; uses a BaseComparator with the given
   * scope.
   *
   * @param scope A scope; a collection of TopicIF objects.
   */
  public TopicNameGrabber(Collection scope) {
    this.comparator = new TopicNameComparator(scope);
  }
  
  /**
   * INTERNAL: Creates a grabber which uses the given comparator.
   *
   * @param comparator The given comparator
   */
  public TopicNameGrabber(Comparator comparator) {
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
  public Object grab(Object topic) {
    if (topic == null)
      return null;
    
    TopicIF _topic;
    try {
      _topic = (TopicIF) topic;
    } catch (ClassCastException e) {
      throw new OntopiaRuntimeException(topic + " is not a TopicIF.", e);
    }

    Collection basenames = _topic.getTopicNames();

    // If there is no base name return null
    if (basenames.isEmpty())
      return null;

    // If there are multiple basenames rank them.    
    Object[] _basenames = basenames.toArray();
    if (_basenames.length > 1)
      Arrays.sort(_basenames, comparator);
    return _basenames[0];
  }

}
