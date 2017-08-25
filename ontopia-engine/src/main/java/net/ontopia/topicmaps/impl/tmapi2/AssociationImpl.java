/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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

  
  @Override
  public AssociationIF getWrapped() {
    return wrapped;
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#createRole(org.tmapi.core.Topic, org.tmapi.core.Topic)
   */
  
  @Override
  public RoleImpl createRole(Topic type, Topic player) {
    Check.typeNotNull(this, type);
    Check.playerNotNull(this, player);
    Check.typeInTopicMap(getTopicMap(), type);
    Check.playerInTopicMap(getTopicMap(), player);
    AssociationRoleIF role = topicMap.getWrapped().getBuilder().makeAssociationRole(wrapped, topicMap.unwrapTopic(type), topicMap.unwrapTopic(player));
    return topicMap.wrapRole(role);
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#getParent()
   */
  
  @Override
  public TopicMapImpl getParent() {
    return getTopicMap();
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#getRoleTypes()
   */
  
  @Override
  public Set<Topic> getRoleTypes() {
    return topicMap.wrapSet(wrapped.getRoleTypes());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#getRoles()
   */
  
  @Override
  public Set<Role> getRoles() {
    return topicMap.wrapSet(wrapped.getRoles());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Association#getRoles(org.tmapi.core.Topic)
   */
  
  @Override
  public Set<Role> getRoles(Topic type) {
    Check.typeNotNull(type);
    return topicMap.wrapSet(wrapped.getRolesByType(topicMap.unwrapTopic(type)));
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Typed#getType()
   */
  
  @Override
  public Topic getType() {
    return topicMap.wrapTopic(wrapped.getType());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Typed#setType(org.tmapi.core.Topic)
   */
  @Override
  public void setType(Topic type) {
    Check.typeNotNull(this, type);
    Check.typeInTopicMap(getTopicMap(), type);
    wrapped.setType(topicMap.unwrapTopic(type));
  }

}
