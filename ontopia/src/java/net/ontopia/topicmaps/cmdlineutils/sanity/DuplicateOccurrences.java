//      $Id: DuplicateOccurrences.java,v 1.6 2002/05/29 13:38:38 hca Exp $      
package net.ontopia.topicmaps.cmdlineutils.sanity;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.utils.*;

/**
 * Used to report all duplicate occurrences(same locator, same occurrence roletype)
 * on a single topic.
 */

public class DuplicateOccurrences {

  private TopicMapIF tm;

  public DuplicateOccurrences(TopicMapIF tm) {
    this.tm = tm;
  }

  /**
   * Returns a Collection of all the topics containing duplicate occurrences.
   */
  public Collection getDuplicateOccurrences() {
    Collection retur = new ArrayList();
    Collection topics = tm.getTopics();
    Iterator ittop = topics.iterator();
    while (ittop.hasNext()) {
      TopicIF t = (TopicIF)ittop.next();
      HashMap templocators = new HashMap();
      HashMap temproles    = new HashMap();
      
      Collection occurences = t.getOccurrences();
      Iterator itoccur = occurences.iterator();
      while (itoccur.hasNext()) {
        OccurrenceIF o = (OccurrenceIF)itoccur.next();
        LocatorIF l = o.getLocator();
        if (l != null) {
          if (temproles.containsKey(l.getAddress())) {
            retur.add(t);
          } else {
            temproles.put(l.getAddress(), null);
            templocators.put(o.getTopic(), null);
          }
        }
      }
    }
    return retur;
  }
}





