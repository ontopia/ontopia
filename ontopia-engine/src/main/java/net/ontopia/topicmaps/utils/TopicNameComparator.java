
package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Comparator that first sorts by type then by scope,
 * where untyped base names are ordered before typed ones.
 *
 * @since 3.0
 */
public class TopicNameComparator extends ScopedIFComparator {
  
  TopicNameComparator(Collection scope) {
    super(scope);
  }
  
  public int compare(Object o1, Object o2) {
    TopicMapIF tm = ((TopicNameIF)o1).getTopicMap();

    TopicIF t1 = ((TopicNameIF)o1).getType();
    TopicIF t2 = ((TopicNameIF)o2).getType();
    
    // check for default type
    TopicIF untypedname = tm.getTopicBySubjectIdentifier(PSI.getSAMNameType());
    if (untypedname.equals(t1)) t1 = null;
    if (untypedname.equals(t2)) t2 = null;

    // untyped should sort before typed
    if (t1 == null) {
      if (t2 != null) return -1;
    } else {
      if (t2 == null) return 1;
    }
    
    return super.compare(o1, o2);
  }

}
