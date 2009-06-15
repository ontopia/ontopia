package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.utils.StringUtils;

public class OccurrenceComparator implements Comparator, Serializable {

  public static final OccurrenceComparator INSTANCE = new OccurrenceComparator();
  
  public int compare(Object o1, Object o2) {
    OccurrenceIF occ1 = (OccurrenceIF)o1;
    OccurrenceIF occ2 = (OccurrenceIF)o2;    
    return StringUtils.compareToIgnoreCase(occ1.getValue(), occ2.getValue());
  }

}
