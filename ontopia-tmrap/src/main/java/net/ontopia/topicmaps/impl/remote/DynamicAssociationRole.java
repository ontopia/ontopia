/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.impl.remote;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.AssociationRole;
import net.ontopia.topicmaps.impl.basic.TopicMap;


/**
 * INTERNAL:
 * PRIVATE:
 */

public class DynamicAssociationRole extends AssociationRole {

  private AssociationRole target;
  
  /**
   * @param tm
   */
  public DynamicAssociationRole(TopicMap tm) {

    super(tm);
  }

  @Override
  public AssociationIF getAssociation() {

    if (target == null) {
      return super.getAssociation();
    }
    return target.getAssociation();
  }
  @Override
  public TopicIF getPlayer() {

    if (target == null) {
      return super.getPlayer();
    }
    return target.getPlayer();
  }
  @Override
  public TopicIF getType() {

    if (target == null) {
      return super.getType();
    }
    return target.getType();
  }
  @Override
  public boolean isConnected() {

    if (target == null) {
      return super.isConnected();
    }
    return target.isConnected();
  }

  @Override
  public void setPlayer(TopicIF player) {

    if (target == null) {
      super.setPlayer(player);
    } else {
      target.setPlayer(player);
    }
  }
  @Override
  public void setType(TopicIF type) {

    if (target == null) {
      super.setType(type);
    } else {
      target.setType(type);
    }
  }

  public void setTarget(AssociationRole aRole) {
  
    target = aRole;
  }
  @Override
  public String toString() {

    if (target == null) {
      return super.toString();
    }
    return "{" + super.toString() + "}";
  }
  @Override
  public void addItemIdentifier(LocatorIF source_locator)
      throws ConstraintViolationException {

    if (target == null) {
      super.addItemIdentifier(source_locator);
    } else {
      target.addItemIdentifier(source_locator);
    }
  }
  @Override
  public String getObjectId() {

    if (target == null) {
      return super.getObjectId();
    }
    return target.getObjectId();
  }
  @Override
  public Collection<LocatorIF> getItemIdentifiers() {

    if (target == null) {
      return super.getItemIdentifiers();
    }
    return target.getItemIdentifiers();
  }
  @Override
  public TopicMapIF getTopicMap() {

    if (target == null) {
      return super.getTopicMap();
    }
    return target.getTopicMap();
  }
  @Override
  public boolean isReadOnly() {

    if (target == null) {
      return super.isReadOnly();
    }
    return target.isReadOnly();
  }
  @Override
  public void removeItemIdentifier(LocatorIF source_locator) {

    if (target == null) {
      super.removeItemIdentifier(source_locator);
    } else {
      target.removeItemIdentifier(source_locator);
    }
  }
  @Override
  public boolean equals(Object obj) {

    if (obj instanceof DynamicAssociationRole) {
      if (target == null) {
        return ((DynamicAssociationRole) obj).equals(this);
      }
      return obj.equals(target);
    }

    if (target == null) {
      return super.equals(obj);
    }
    return target.equals(obj);

  }

  public boolean equals(DynamicAssociationRole obj) {
  
    if (target == null) {
      return super.equals(obj);
    }
    return target.equals(obj);
  
  }
  @Override
  public int hashCode() {

    if (target == null) {
      return super.hashCode();
    }
    return target.hashCode();
  }
}
