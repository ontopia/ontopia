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

import net.ontopia.topicmaps.core.AssociationRoleIF;

import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class RoleImpl extends ReifiableImpl implements Role {

  private AssociationRoleIF wrapped;

  public RoleImpl(TopicMapImpl topicMap, AssociationRoleIF role) {
    super(topicMap);
    wrapped = role;
  }

  /* (non-Javadoc)
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */
  
  @Override
  public AssociationRoleIF getWrapped() {
    return wrapped;
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Role#getParent()
   */
  
  @Override
  public Association getParent() {
    return topicMap.wrapAssociation(wrapped.getAssociation());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Role#getPlayer()
   */
  
  @Override
  public Topic getPlayer() {
    return topicMap.wrapTopic(wrapped.getPlayer());
  }

  /* (non-Javadoc)
   * @see org.tmapi.core.Role#setPlayer(org.tmapi.core.Topic)
   */
  
  @Override
  public void setPlayer(Topic player) {
    Check.playerNotNull(this, player);
    Check.playerInTopicMap(getTopicMap(), player);
    wrapped.setPlayer(topicMap.unwrapTopic(player));
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
