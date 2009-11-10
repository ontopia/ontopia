package net.ontopia.topicmaps.nav2.webapps.ontopoly.utils;

import java.util.Comparator;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;

public class TopicComparator implements Comparator<Topic> {
	
	public static final TopicComparator INSTANCE = new TopicComparator();

	private TopicComparator() {
	}

	public int compare(Topic t1, Topic t2) {
		return t1.getName().compareTo(t2.getName());
  }
}
