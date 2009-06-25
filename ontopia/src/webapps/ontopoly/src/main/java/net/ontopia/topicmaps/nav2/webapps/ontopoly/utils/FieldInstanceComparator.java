package net.ontopia.topicmaps.nav2.webapps.ontopoly.utils;

import java.util.Comparator;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldInstance;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldAssignment;

public class FieldInstanceComparator implements Comparator {
	
	public static final FieldInstanceComparator INSTANCE = new FieldInstanceComparator();

	private FieldInstanceComparator() {
	}

	public int compare(Object o1, Object o2) {
		FieldInstance fi1 = (FieldInstance)o1;
		FieldInstance fi2 = (FieldInstance)o2;
		FieldAssignment fa1 = fi1.getFieldAssignment();
		FieldAssignment fa2 = fi2.getFieldAssignment();
		return (fa1.getOrder() < fa2.getOrder() ? -1 : 1);
  }
}
