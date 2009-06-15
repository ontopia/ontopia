package net.ontopia.topicmaps.nav2.webapps.ontopoly.utils;

import java.util.Comparator;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldAssignment;

public class FieldAssignmentComparator implements Comparator {
	
	public static final FieldAssignmentComparator INSTANCE = new FieldAssignmentComparator();

	private FieldAssignmentComparator() {
	}

	public int compare(Object o1, Object o2) {
      FieldAssignment fa1 = (FieldAssignment) o1;
      FieldAssignment fa2 = (FieldAssignment) o2;
      return (fa1.getOrder() < fa2.getOrder() ? -1 : 1);
  }
}