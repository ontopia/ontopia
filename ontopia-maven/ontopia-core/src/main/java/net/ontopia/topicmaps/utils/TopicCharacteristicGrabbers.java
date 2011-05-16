
// $Id: TopicCharacteristicGrabbers.java,v 1.13 2008/06/12 14:37:23 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import net.ontopia.utils.*;

/**
 * INTERNAL: A convenience class for creating grabbers that grab
 * specific topic characteristics, using various criteria, including
 * scope.
 * @since 1.1
 */

public class TopicCharacteristicGrabbers {

  private TopicCharacteristicGrabbers() {
    // don't call me
  }

  /**
   * INTERNAL: Returns a grabber that will grab the name most suitable
   * for display from a topic.  If the topic has a display name that name
   * will be chosen. If not, the base name in the least constrained
   * scope will be chosen.
   * @return A TopicNameIF or VariantNameIF object; null if the topic
   *         has no base names.
   */
  public static GrabberIF getDisplayNameGrabber() {
    // Note: Grabs variant name if it exists.
    NameGrabber grabber = new NameGrabber(PSI.getXTMDisplay(), true);
    grabber.setGrabOnlyTopicName(false);
    return grabber;
  }

  /**
   * INTERNAL: Returns a grabber that will grab the name most suitable
   * for sorting from a topic.  If the topic has a sort name that name
   * will be chosen. If not, the base name in the least constrained
   * scope will be chosen.
   * @return A TopicNameIF or VariantNameIF object; null if the topic
   *         has no base names.
   */
  public static GrabberIF getSortNameGrabber() {
    // Note: Grabs variant name if it exists.
    NameGrabber grabber = new NameGrabber(PSI.getXTMSort(), true);
    grabber.setGrabOnlyTopicName(false);
    return grabber;
  }
}
