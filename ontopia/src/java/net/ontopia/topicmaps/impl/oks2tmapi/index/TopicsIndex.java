
// $Id: TopicsIndex.java,v 1.4 2005/07/11 09:58:45 grove Exp $

package net.ontopia.topicmaps.impl.oks2tmapi.index;

import java.util.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.oks2tmapi.TopicMap;
import net.ontopia.topicmaps.impl.oks2tmapi.LazySet;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class TopicsIndex 
  extends AbstractTMAPIIndex 
  implements org.tmapi.index.core.TopicsIndex {

  protected TopicMap tm;
  protected ClassInstanceIndexIF ci;

  public TopicsIndex(TopicMap tm, ClassInstanceIndexIF ci) {
    this.tm = tm;
    this.ci = ci;
  }

  protected IndexIF getOther() {
    return ci;
  }

  public void configure(org.tmapi.core.TopicMap tm) {
    // no-op
  }

  public Collection getTopicTypes() {
    return tm.wrapSet(ci.getTopicTypes());
  }

  public Collection getTopicsByType(org.tmapi.core.Topic type) {
    return tm.wrapSet(ci.getTopics(tm.unwrapTopic(type)));
  }

  public Collection getTopicsByTypes(org.tmapi.core.Topic[] types, boolean matchAll) {
    if (matchAll) {
      if (types == null || types.length == 0)
	return Collections.EMPTY_SET;

      Collection typed = ci.getTopics(tm.unwrapTopic(types[0]));
      for (int i=1; i < types.length; i++) {
	typed.retainAll(ci.getTopics(tm.unwrapTopic(types[i])));
      }
      return (typed.isEmpty() ? typed : new LazySet(tm, typed));
    } else {
      Collection typed = new HashSet();
      for (int i=0; i < types.length; i++) {
	typed.addAll(ci.getTopics(tm.unwrapTopic(types[i])));
      }
      return (typed.isEmpty() ? typed : new LazySet(tm, typed));
    }
  }

  public org.tmapi.core.Topic getTopicBySubjectLocator(org.tmapi.core.Locator subjectLocator) {
    return tm._getTopicBySubjectLocator(subjectLocator);
  }

  public org.tmapi.core.Topic getTopicBySubjectIdentifier(org.tmapi.core.Locator subjectIdentifier) {
    return tm._getTopicBySubjectIdentifier(subjectIdentifier);
  }
  
}
