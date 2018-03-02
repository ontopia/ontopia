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
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.topicmaps.impl.utils.PhantomAssociation;

/**
 * INTERNAL: The read-only rdbms association role implementation.
 */

public class ReadOnlyAssociationRole extends ReadOnlyTMObject
  implements AssociationRoleIF {
  
  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public int _p_getFieldCount() {
    return AssociationRole.fields.length;
  }

  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public String getClassIndicator() {
    return AssociationRole.CLASS_INDICATOR;
  }

  @Override
  public String getObjectId() {
    return (id == null ? null : AssociationRole.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // AssociationRoleIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public AssociationIF getAssociation() {
    try {
      return this.<AssociationIF>loadFieldNoCheck(AssociationRole.LF_association);
    } catch (IdentityNotFoundException e) {
      // role or association has been deleted by somebody else, so
      // return a phantom association
      return new PhantomAssociation();
    }
  }

  @Override
  public TopicIF getPlayer() {
    try {
      return this.<TopicIF>loadField(AssociationRole.LF_player);
    } catch (IdentityNotFoundException e) {
      // role has been deleted by somebody else, so return null
      return null;
    }
  }
  
  @Override
  public void setPlayer(TopicIF player) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getType() {
    try {
      return this.<TopicIF>loadField(AssociationRole.LF_type);
    } catch (IdentityNotFoundException e) {
      // role has been deleted by somebody else, so return null
      return null;
    }
  }

  @Override
  public void setType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getReifier() {
    try {
      return this.<TopicIF>loadField(AssociationRole.LF_reifier);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
	}
  
  @Override
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
	}

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  @Override
  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnly.AssociationRole", (AssociationRoleIF)this);
  }

}
