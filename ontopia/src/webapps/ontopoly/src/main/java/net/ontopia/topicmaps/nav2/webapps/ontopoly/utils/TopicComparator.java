package net.ontopia.topicmaps.nav2.webapps.ontopoly.utils;

import java.util.Comparator;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;

public class TopicComparator implements Comparator {
	
	public static final TopicComparator INSTANCE = new TopicComparator();

	private TopicComparator() {
	}

	public int compare(Object o1, Object o2) {
		Topic t1 = (Topic) o1;
		Topic t2 = (Topic) o2;
		
		return t1.getName().compareTo(t2.getName());
  }
}
