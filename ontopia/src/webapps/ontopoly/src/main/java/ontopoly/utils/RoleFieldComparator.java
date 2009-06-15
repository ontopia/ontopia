package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;

public class RoleFieldComparator implements Comparator, Serializable {

  public static final RoleFieldComparator INSTANCE = new RoleFieldComparator();

  public int compare(Object o1, Object o2) {
    RoleField rf1 = (RoleField)o1;
    RoleField rf2 = (RoleField)o2;

    if (rf1 == null)
      return (rf2 == null ? 0 : -1);
    else if (rf2 == null)
      return 1;

    int retval = TopicComparator.INSTANCE.compare(rf1.getRoleType(), rf2.getRoleType());
    if (retval != 0) return retval;

    return TopicComparator.INSTANCE.compare(rf1.getAssociationField().getAssociationType(), rf2.getAssociationField().getAssociationType());
  }

}
