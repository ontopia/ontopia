package net.ontopia.topicmaps.viz;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.StringifierIF;

/** Utility class to display topics in a List. */
class TopicListItem {

  private String name;

  TopicIF topic;

  public TopicListItem(String name) {

    this.name = name;
  }

  /**
   * Create a topic list item generating the name from topic using stringifier.
   * @param topic The topic of this list item.
   * @param stringifier Generates the name from the topic. 
   */
  public TopicListItem(TopicIF topic, StringifierIF stringifier) {
    this.topic = topic;
    this.name = stringifier.toString(topic);
  }

  /**
   * Create a topic list item with the specific topic and name.
   * @param topic The topic of this list item.
   * @param name The name of this list item.
   */
  public TopicListItem(TopicIF topic, String name) {
    this.topic = topic;
    this.name = name;
  }

  public TopicIF getTopic() {

    return topic;
  }

  public String toString() {

    return name;
  }

  /**
   * INTERNAL: PRIVATE: Purpose: A simple Comparator which sorts objects based
   * on #toString() Description: Examples:
   */
  static class TopicListItemComparator implements Comparator {

    static TopicListItemComparator instance = new TopicListItemComparator();

    public int compare(Object first, Object second) {

      if (first == null)
        return 1;
      if (second == null)
        return -1;

      String firstString = first.toString();
      String secondString = second.toString();

      return firstString.compareToIgnoreCase(secondString);
    }

    public static TopicListItemComparator getInstance() {

      return instance;
    }

  }

  public static void sort(List list) {
    
    Collections.sort(list, TopicListItemComparator.getInstance());
    
  }
}
