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

package net.ontopia.topicmaps.impl.basic;

import java.util.Objects;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;

/**
 * INTERNAL: The basic association role implementation.
 */

public class AssociationRole extends TMObject implements AssociationRoleIF {

  private static final long serialVersionUID = 8387889553134058046L;

  protected TopicIF reifier;
  protected TopicIF type;
  protected TopicIF player;

  protected AssociationRole(TopicMap tm) {
    super(tm);
  }

  // -----------------------------------------------------------------------------
  // AssociationRoleIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public AssociationIF getAssociation() {
    return (AssociationIF) parent;
  }

  /**
   * INTERNAL: Sets the association that the association role belongs to. [parent]
   */
  protected void setAssociation(Association parent) {
    // Validate topic map
    if (parent != null && parent.topicmap != this.topicmap)
        throw new ConstraintViolationException(
            "Cannot move objects across topic maps: " + this.topicmap + " and "
                + parent.topicmap);
    // Set parent
    this.parent = parent;
  }

  @Override
  public TopicIF getPlayer() {
    return player;
  }

  @Override
  public void setPlayer(TopicIF player) {
    Objects.requireNonNull(player, "Association role player must not be null.");
    CrossTopicMapException.check(player, this);
    // Notify listeners
    fireEvent(AssociationRoleIF.EVENT_SET_PLAYER, player, this.player);
    // Unregister association role with topic
    if (this.player != null && parent != null && parent.parent != null)
        ((Topic) this.player).removeRole(this);
    // Set property
    this.player = player;
    // Register association role with topic
    if (player != null && parent != null && parent.parent != null)
        ((Topic) this.player).addRole(this);
  }

  @Override
  public void remove() {
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      ((Association)parent).removeRole(this);
    }
  }

  // -----------------------------------------------------------------------------
  // TypedIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public TopicIF getType() {
    return type;
  }

  @Override
  public void setType(TopicIF type) {
    Objects.requireNonNull(type, "Association role type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent(AssociationRoleIF.EVENT_SET_TYPE, type, getType());
    this.type = type;
  }
  
  // -----------------------------------------------------------------------------
  // ReifiableIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public TopicIF getReifier() {
    return reifier;
  }
  
  @Override
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null) CrossTopicMapException.check(_reifier, this);
    if (DuplicateReificationException.check(this, _reifier)) { return; }
    // Notify listeners
    Topic reifier = (Topic)_reifier;
    Topic oldReifier = (Topic)getReifier();
    fireEvent(ReifiableIF.EVENT_SET_REIFIER, reifier, oldReifier);
    this.reifier = reifier;
    if (oldReifier != null) oldReifier.setReified(null);
    if (reifier != null) reifier.setReified(this);
  }

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  @Override
  protected void fireEvent(String event, Object new_value, Object old_value) {
    if (parent == null || parent.parent == null)
      return;
    else
      topicmap.processEvent(this, event, new_value, old_value);
  }

  @Override
  public boolean isConnected() {
    return (parent != null && parent.isConnected());
  }

  @Override
  public String toString() {
    return ObjectStrings.toString("basic.AssociationRole",
        (AssociationRoleIF) this);
  }

}
