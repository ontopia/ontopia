// $Id:$

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.Set;

import net.ontopia.topicmaps.core.ScopedIF;

import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public abstract class ScopedImpl extends ReifiableImpl implements Scoped {

  public ScopedImpl(TopicMapImpl topicMap) {
    super(topicMap);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Scoped#getScope()
   */
  
  public Set<Topic> getScope() {
    return topicMap.wrapSet(((ScopedIF) getWrapped()).getScope());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Scoped#addTheme(org.tmapi.core.Topic)
   */
  
  public void addTheme(Topic theme) {
    Check.themeNotNull(this, theme);
    ((ScopedIF) getWrapped()).addTheme(topicMap.unwrapTopic(theme));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Scoped#removeTheme(org.tmapi.core.Topic)
   */
  
  public void removeTheme(Topic theme) {
    Check.themeNotNull(this, theme);
    ((ScopedIF) getWrapped()).removeTheme(topicMap.unwrapTopic(theme));
  }

}
