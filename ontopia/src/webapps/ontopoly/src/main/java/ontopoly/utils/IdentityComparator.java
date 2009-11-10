package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.StringUtils;

public class IdentityComparator implements Comparator<Object>, Serializable {

  public static final IdentityComparator INSTANCE = new IdentityComparator();
  
  public int compare(Object o1, Object o2) {
    LocatorIF i1 = (LocatorIF)o1;
    LocatorIF i2 = (LocatorIF)o2;
    return StringUtils.compareToIgnoreCase(i1.getAddress(), i2.getAddress());
  }

}
