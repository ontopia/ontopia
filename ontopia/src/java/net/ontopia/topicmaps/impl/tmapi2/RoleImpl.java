// $Id:$

package net.ontopia.topicmaps.impl.tmapi2;

import net.ontopia.topicmaps.core.AssociationRoleIF;

import org.tmapi.core.Association;
import org.tmapi.core.Topic;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class RoleImpl extends ReifiableImpl implements org.tmapi.core.Role {

  private AssociationRoleIF wrapped;

  public RoleImpl(TopicMapImpl topicMap, AssociationRoleIF role) {
    super(topicMap);
    wrapped = role;
  }

  /* (non-Javadoc)
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */
  
  public AssociationRoleIF getWrapped() {
    return wrapped;
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Role#getParent()
   */
  
  public Association getParent() {
    return topicMap.wrapAssociation(wrapped.getAssociation());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Role#getPlayer()
   */
  
  public Topic getPlayer() {
    return topicMap.wrapTopic(wrapped.getPlayer());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Role#setPlayer(org.tmapi.core.Topic)
   */
  
  public void setPlayer(Topic player) {
    Check.playerNotNull(this, player);
    wrapped.setPlayer(topicMap.unwrapTopic(player));
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Typed#getType()
   */
  
  public Topic getType() {
    return topicMap.wrapTopic(wrapped.getType());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Typed#setType(org.tmapi.core.Topic)
   */
  
  public void setType(Topic type) {
    Check.typeNotNull(this, type);
    wrapped.setType(topicMap.unwrapTopic(type));
  }

}
