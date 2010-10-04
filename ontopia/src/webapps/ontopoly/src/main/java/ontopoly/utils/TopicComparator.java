package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;

import ontopoly.model.OntopolyTopicIF;

import net.ontopia.utils.StringUtils;

public class TopicComparator implements Comparator<OntopolyTopicIF>, Serializable {

  public static final TopicComparator INSTANCE = new TopicComparator();
  
  private TopicComparator() {
    // don't call me
  }
  
  public int compare(OntopolyTopicIF t1, OntopolyTopicIF t2) {
    if (t1 == null && t2 == null) return 0;
    else if (t1 == null)
      return 1;
    else if (t2 == null)
      return -1;
    return StringUtils.compareToIgnoreCase(t1.getName(), t2.getName());
  }

}
