
// $Id: ScopedObjectsIndex.java,v 1.3 2008/06/12 14:37:14 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi.index;

import java.util.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.impl.oks2tmapi.TopicMap;
import net.ontopia.topicmaps.impl.oks2tmapi.LazySet;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class ScopedObjectsIndex 
  extends AbstractTMAPIIndex 
  implements org.tmapi.index.core.ScopedObjectsIndex {

  protected TopicMap tm;
  protected ScopeIndexIF si;

  public ScopedObjectsIndex(TopicMap tm, ScopeIndexIF si) {
    this.tm = tm;
    this.si = si;
  }

  protected IndexIF getOther() {
    return si;
  }

  public void configure(org.tmapi.core.TopicMap tm) {
    // no-op
  }

  public Collection getScopingTopics() {
    Collection themes = new HashSet();
    themes.addAll(si.getAssociationThemes());
    themes.addAll(si.getTopicNameThemes());
    themes.addAll(si.getOccurrenceThemes());
    //! themes.addAll(si.getTopicMapThemes()); // ISSUE: What about this one?
    //! themes.addAll(si.getTopicThemes()); // ISSUE: What about this one?
    themes.addAll(si.getVariantThemes());
    return (themes.isEmpty() ? themes : new LazySet(tm, themes));
  }

  public Collection getScopedObjectsByScopingTopic(org.tmapi.core.Topic scopingTopic) {
    Collection scoped = getScoped(tm.unwrapTopic(scopingTopic));
    return (scoped.isEmpty() ? scoped : new LazySet(tm, scoped));
  }

  protected Collection getScoped(TopicIF scopingTopic) {
    Collection scoped = new HashSet();
    scoped.addAll(si.getAssociations(scopingTopic));
    scoped.addAll(si.getTopicNames(scopingTopic));
    scoped.addAll(si.getOccurrences(scopingTopic));
    //! scoped.addAll(si.getTopics(tm.unwrapTopic(scopingTopic))); // ISSUE: What about this one?
    scoped.addAll(si.getVariants(scopingTopic));
    return scoped;
  }

  public Collection getScopedObjectsByScopingTopics(org.tmapi.core.Topic[] scopingTopics, boolean matchAll) {
    if (matchAll) {
      if (scopingTopics == null || scopingTopics.length == 0)
	return Collections.EMPTY_SET;

      Collection scoped = getScoped(tm.unwrapTopic(scopingTopics[0]));
      for (int i=1; i < scopingTopics.length; i++) {
	scoped.retainAll(getScoped(tm.unwrapTopic(scopingTopics[i])));
      }
      return (scoped.isEmpty() ? scoped : new LazySet(tm, scoped));
    } else {
      Collection scoped = new HashSet();
      for (int i=0; i < scopingTopics.length; i++) {
	scoped.addAll(si.getAssociations(tm.unwrapTopic(scopingTopics[i])));
	scoped.addAll(si.getTopicNames(tm.unwrapTopic(scopingTopics[i])));
	scoped.addAll(si.getOccurrences(tm.unwrapTopic(scopingTopics[i])));
	//! scoped.addAll(si.getTopics(tm.unwrapTopic(scopingTopics[i]))); // ISSUE: What about this one?
	scoped.addAll(si.getVariants(tm.unwrapTopic(scopingTopics[i])));
      }
      return (scoped.isEmpty() ? scoped : new LazySet(tm, scoped));
    }
  }
  
}
