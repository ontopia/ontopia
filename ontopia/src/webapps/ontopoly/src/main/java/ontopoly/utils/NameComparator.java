package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.StringUtils;

public class NameComparator implements Comparator, Serializable {

  public static final NameComparator INSTANCE = new NameComparator();
  
  public int compare(Object o1, Object o2) {
    TopicNameIF n1 = (TopicNameIF)o1;
    TopicNameIF n2 = (TopicNameIF)o2;    
    return StringUtils.compareToIgnoreCase(n1.getValue(), n2.getValue());
  }

}
