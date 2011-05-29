
package net.ontopia.topicmaps.cmdlineutils.sanity;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.utils.*;

public class DuplicateNames {
  private TopicMapIF tm;
  //  protected StringifierIF ts = TopicStringifiers.getTopicTopicNameStringifier();

  public DuplicateNames(TopicMapIF tm) {
    this.tm = tm;
  }


  /**
   * Returns a collection of all the topics with the same basename and scope.
   */
  public Collection getDuplicatedNames() {

    Collection retur = new ArrayList();
    Collection topics = tm.getTopics();
    Iterator it = topics.iterator();
    //check all the topics.
    while (it.hasNext()) {
      TopicIF topic = (TopicIF)it.next();

      Collection basenames = topic.getTopicNames();
      String basename, scopename;
      Iterator itbasenames = basenames.iterator();
      while (itbasenames.hasNext()) {
        TopicNameIF b = (TopicNameIF)itbasenames.next();
        basename = b.toString();
        Collection scopes = b.getScope();
        Iterator itscope = scopes.iterator();
        while (itscope.hasNext()) {
          TopicIF t = (TopicIF)itscope.next();
          scopename = t.getObjectId();
          if (basename.equals(scopename)) {
            retur.add(topic);
          }
        }
      }
    }
    return retur;
  }
}





