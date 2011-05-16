package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;

import net.ontopia.utils.ObjectUtils;
import ontopoly.model.Topic;

public class TopicIdComparator implements Comparator<Topic>, Serializable {

  public static final TopicIdComparator INSTANCE = new TopicIdComparator();
  
  private TopicIdComparator() {
    // don't call me
  }
  
  public int compare(Topic t1, Topic t2) {
    return ObjectUtils.compare(t1.getId(), t2.getId());
  }

}
