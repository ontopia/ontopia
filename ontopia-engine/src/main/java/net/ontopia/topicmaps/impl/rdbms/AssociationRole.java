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

package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.persistence.proxy.IdentityNotFoundException;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.topicmaps.impl.utils.PhantomAssociation;

/**
 * INTERNAL: The rdbms association role implementation.
 */

public class AssociationRole extends TMObject implements AssociationRoleIF {
  
  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------

  protected static final int LF_sources = 0;
  protected static final int LF_topicmap = 1;
  protected static final int LF_association = 2;
  protected static final int LF_type = 3;
  protected static final int LF_player = 4;
  protected static final int LF_reifier = 5;
  protected static final String[] fields = {"sources", "topicmap", "assoc", "type", "player", "reifier"};

  public void detach() {
    detachCollectionField(LF_sources);
    detachField(LF_topicmap);
    detachField(LF_association);
    detachField(LF_reifier);
    detachField(LF_type);
    detachField(LF_player);
  }

  public void _p_setTransaction(TransactionIF txn) {
    super._p_setTransaction(txn);
    // make sure that association field is always initialized
    loadField(LF_association);
  }
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public static final String CLASS_INDICATOR = "R";

  public AssociationRole() {
  }

  public AssociationRole(TransactionIF txn) {
    super(txn);
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return fields.length;
  }

  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // AssociationRoleIF implementation
  // ---------------------------------------------------------------------------

  public AssociationIF getAssociation() {
    try {
      return (AssociationIF)loadFieldNoCheck(LF_association);
    } catch (IdentityNotFoundException e) {
      // role or association has been deleted by somebody else, so
      // return a phantom association
      return new PhantomAssociation();
    }
  }

  /**
   * INTERNAL: Sets the association that the association role belongs to. [parent]
   */
  void setAssociation(AssociationIF assoc) {
    // Set parent topic map
    setTopicMap((assoc == null ? null : (TopicMap)assoc.getTopicMap()));
    // Notify transaction
    valueChanged(LF_association, assoc, true);
  }

  void setTopicMap(TopicMap topicmap) {    
    // Notify player
    Topic player = (Topic)getPlayer();
    if (player != null)
      if (topicmap != null)
        player.addRole(this);
      else 
        player.removeRole(this);

    // Note: must unregister with player before deleting itself.
    
    // Notify transaction
    transactionChanged(topicmap);
    valueChanged(LF_topicmap, topicmap, true);    
  }

  public TopicIF getPlayer() {
    try {
      return (TopicIF)loadField(LF_player);
    } catch (IdentityNotFoundException e) {
      // role has been deleted by somebody else, so return null
      return null;
    }
  }
  
  public void setPlayer(TopicIF player) {
    if (player == null)
      throw new NullPointerException("Association role player must not be null.");
    CrossTopicMapException.check(player, this);
    TopicIF oldplayer = getPlayer();
    
    // Notify listeners
    fireEvent(AssociationRoleIF.EVENT_SET_PLAYER, player, oldplayer);
    
    // Notify transaction
    valueChanged(LF_player, player, true);
    
    // Unregister association role with topic
    AssociationIF assoc = getAssociation();
    if (oldplayer != null && assoc != null && assoc.getTopicMap() != null)
      ((Topic)oldplayer).removeRole(this);

    // Register association role with topic
    if (player != null && assoc != null && assoc.getTopicMap() != null)
      ((Topic)player).addRole(this);
  }

  public void remove() {
    Association parent = (Association)getAssociation();
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      parent.removeRole(this);
    }
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getType() {
    try {
      return (TopicIF)loadField(LF_type);
    } catch (IdentityNotFoundException e) {
      // role has been deleted by somebody else, so return null
      return null;
    }
  }

  public void setType(TopicIF type) {
    if (type == null)
      throw new NullPointerException("Association role type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent(AssociationRoleIF.EVENT_SET_TYPE, type, getType());
    // Notify transaction
    valueChanged(LF_type, type, true);
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    try {
      return (TopicIF)loadField(LF_reifier);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
  }
  
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null)
      CrossTopicMapException.check(_reifier, this);
    // Notify listeners
    Topic reifier = (Topic)_reifier;
    Topic oldReifier = (Topic)getReifier();
    fireEvent(ReifiableIF.EVENT_SET_REIFIER, reifier, oldReifier);
    valueChanged(LF_reifier, reifier, true);
    if (oldReifier != null) oldReifier.setReified(null);
    if (reifier != null) reifier.setReified(this);
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("rdbms.AssociationRole", (AssociationRoleIF)this);
  }
}
