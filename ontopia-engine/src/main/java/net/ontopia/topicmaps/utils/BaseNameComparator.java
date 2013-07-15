
package net.ontopia.topicmaps.utils;

import java.util.Collection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * DEPRECATED: Comparator that first sorts by type then by scope,
 * where untyped base names are ordered before typed ones.
 *
 * @since 3.0
 * @deprecated Use TopicNameComparator instead.
 */
public class BaseNameComparator extends ScopedIFComparator<TopicNameIF> {
  
  BaseNameComparator(Collection<TopicIF> scope) {
    super(scope);
  }
  
  public int compare(TopicNameIF o1, TopicNameIF o2) {
    TopicIF t1 = o1.getType();
    TopicIF t2 = o2.getType();
    
    // untyped should sort before typed
    if (t1 == null) {
      if (t2 != null) return -1;
    } else {
      if (t2 == null) return 1;
    }
    
    return super.compare(o1, o2);
  }

}
