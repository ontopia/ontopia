// $Id:$

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.Set;

import net.ontopia.topicmaps.core.ScopedIF;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public abstract class ScopedImpl extends ReifiableImpl implements org.tmapi.core.Scoped {

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
    if (theme == null) {
      throw new ModelConstraintException(this, "The theme must not be null");
    }
    ((ScopedIF) getWrapped()).addTheme(topicMap.unwrapTopic(theme));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Scoped#removeTheme(org.tmapi.core.Topic)
   */
  
  public void removeTheme(Topic theme) {
    if (theme == null) {
      throw new ModelConstraintException(this, "The theme must not be null");
    }
    ((ScopedIF) getWrapped()).removeTheme(topicMap.unwrapTopic(theme));
  }

}
