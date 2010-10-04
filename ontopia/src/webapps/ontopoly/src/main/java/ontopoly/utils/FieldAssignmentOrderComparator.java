package ontopoly.utils;

import java.util.Comparator;

import ontopoly.model.FieldAssignment;

public class FieldAssignmentOrderComparator implements Comparator<FieldAssignment> {
	
	public static final FieldAssignmentOrderComparator INSTANCE = new FieldAssignmentOrderComparator();

	private FieldAssignmentOrderComparator() {
	}

	public int compare(FieldAssignment fa1, FieldAssignment fa2) {
      return (fa1.getOrder() < fa2.getOrder() ? -1 : 1);
  }
}
