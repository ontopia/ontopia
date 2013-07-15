
package net.ontopia.topicmaps.utils;

import java.util.Collection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * INTERNAL: Comparator that first sorts by type then by scope,
 * where untyped base names are ordered before typed ones.
 *
 * @since 3.0
 */
public class TopicNameComparator extends ScopedIFComparator<TopicNameIF> {
  
  TopicNameComparator(Collection<TopicIF> scope) {
    super(scope);
  }
  
  public int compare(TopicNameIF o1, TopicNameIF o2) {
    TopicMapIF tm = o1.getTopicMap();

    TopicIF t1 = o1.getType();
    TopicIF t2 = o2.getType();
    
    // check for default type
    TopicIF untypedname = tm.getTopicBySubjectIdentifier(PSI.getSAMNameType());
    if (untypedname == null) {
      t1 = null;
      t2 = null;
    } else {
      if (untypedname.equals(t1)) t1 = null;
      if (untypedname.equals(t2)) t2 = null;
    }

    // untyped should sort before typed
    if (t1 == null) {
      if (t2 != null) return -1;
    } else {
      if (t2 == null) return 1;
    }
    
    return super.compare(o1, o2);
  }

}
