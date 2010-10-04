package ontopoly.utils;

import java.util.Comparator;

import ontopoly.model.FieldAssignmentIF;

public class FieldAssignmentOrderComparator implements Comparator<FieldAssignmentIF> {
	
  public static final FieldAssignmentOrderComparator INSTANCE = new FieldAssignmentOrderComparator();

  private FieldAssignmentOrderComparator() {
  }

  public int compare(FieldAssignmentIF fa1, FieldAssignmentIF fa2) {
    return (fa1.getOrder() < fa2.getOrder() ? -1 : 1);
  }
}
