package net.ontopia.topicmaps.nav2.webapps.ontopoly.utils;

import java.util.Comparator;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldAssignment;

public class FieldAssignmentComparator implements Comparator<FieldAssignment> {
	
	public static final FieldAssignmentComparator INSTANCE = new FieldAssignmentComparator();

	private FieldAssignmentComparator() {
	}

	public int compare(FieldAssignment fa1, FieldAssignment fa2) {
      return (fa1.getOrder() < fa2.getOrder() ? -1 : 1);
  }
}
