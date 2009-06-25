// $Id:$

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;

import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class AssociationImpl extends ScopedImpl implements Association {

  private AssociationIF wrapped;

  /* (non-Javadoc)
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */
  public AssociationImpl(TopicMapImpl topicMap, AssociationIF assoc) {
    super(topicMap);
    wrapped = assoc;
  }

  
  public AssociationIF getWrapped() {
    return wrapped;
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#createRole(org.tmapi.core.Topic, org.tmapi.core.Topic)
   */
  
  public RoleImpl createRole(Topic type, Topic player) {
    Check.typeNotNull(this, type);
    Check.playerNotNull(this, player);
    AssociationRoleIF role = topicMap.getWrapped().getBuilder().makeAssociationRole(wrapped, topicMap.unwrapTopic(type), topicMap.unwrapTopic(player));
    return topicMap.wrapRole(role);
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#getParent()
   */
  
  public TopicMapImpl getParent() {
    return getTopicMap();
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#getRoleTypes()
   */
  
  public Set<Topic> getRoleTypes() {
    return topicMap.wrapSet(wrapped.getRoleTypes());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#getRoles()
   */
  
  public Set<Role> getRoles() {
    return topicMap.wrapSet(wrapped.getRoles());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#getRoles(org.tmapi.core.Topic)
   */
  
  public Set<Role> getRoles(Topic type) {
    Check.typeNotNull(type);
    return topicMap.wrapSet(wrapped.getRolesByType(topicMap.unwrapTopic(type)));
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
