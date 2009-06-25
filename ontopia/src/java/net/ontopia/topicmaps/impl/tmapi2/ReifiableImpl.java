// $Id:$

package net.ontopia.topicmaps.impl.tmapi2;

import net.ontopia.topicmaps.core.ReifiableIF;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public abstract class ReifiableImpl extends ConstructImpl implements
    Reifiable {

  public ReifiableImpl(TopicMapImpl topicMap) {
    super(topicMap);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Reifiable#getReifier()
   */
  
  public TopicImpl getReifier() {
    return topicMap.wrapTopic(((ReifiableIF) getWrapped()).getReifier());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Reifiable#setReifier(org.tmapi.core.Topic)
   */
  
  public void setReifier(Topic reifier)
      throws ModelConstraintException {
    if (reifier != null && reifier.getReified() != null && !reifier.getReified().equals(this)) {
      throw new ModelConstraintException(this, "The reifier reifies another construct");
    }
    ((ReifiableIF) getWrapped()).setReifier(topicMap.unwrapTopic(reifier));
  }

}
