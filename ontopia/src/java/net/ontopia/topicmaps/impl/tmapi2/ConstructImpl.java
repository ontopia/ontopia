// $Id:$

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.Set;

import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TMObjectIF;

import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

abstract class ConstructImpl implements Construct {

  protected TopicMapImpl topicMap;

  public ConstructImpl(TopicMapImpl topicMap) {
    this.topicMap = topicMap;
  }

  abstract TMObjectIF getWrapped();

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Construct#getId()
   */
  
  public String getId() {
    return getWrapped().getObjectId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Construct#getItemIdentifiers()
   */
  
  public Set<Locator> getItemIdentifiers() {
    return topicMap.wrapSet(getWrapped().getItemIdentifiers());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Construct#addItemIdentifier(org.tmapi.core.Locator)
   */
  
  public void addItemIdentifier(Locator iid) {
    Check.itemIdentifierNotNull(this, iid);
    try {
      getWrapped().addItemIdentifier(topicMap.unwrapLocator(iid));
    } catch (ConstraintViolationException ex) {
      throw new IdentityConstraintException(this, topicMap.getConstructByItemIdentifier(iid), iid,
          "A construct with the same item identifier exists already");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Construct#removeItemIdentifier(org.tmapi.core.Locator)
   */
  
  public void removeItemIdentifier(Locator iid) {
    getWrapped().removeItemIdentifier(topicMap.unwrapLocator(iid));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Construct#getTopicMap()
   */
  
  public TopicMapImpl getTopicMap() {
    return topicMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Construct#remove()
   */
  
  public void remove() {
    getWrapped().remove();
  }

  
  public boolean equals(Object obj) {
    if ((obj!=null) && (obj instanceof ConstructImpl)) {
    	return getWrapped() == ((ConstructImpl) obj).getWrapped();
    }
    return false;
  }
  
  public int hashCode() {
    return getWrapped().hashCode();
  }

}
