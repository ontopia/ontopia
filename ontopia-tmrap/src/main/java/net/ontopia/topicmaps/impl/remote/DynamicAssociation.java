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
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.Association;
import net.ontopia.topicmaps.impl.basic.TopicMap;

/**
 * INTERNAL: PRIVATE: EXPERIMENTAL: Description: Dynamic proxy for associations
 */

public class DynamicAssociation extends Association implements AssociationIF {

  // This should be AssociationIF when this class becomes a proper proxy
  private Association target;

  /**
   * @param tm
   */
  DynamicAssociation(TopicMap tm) {

    super(tm);

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.AssociationIF#getRoleTypes()
   */
  @Override
  public Collection<TopicIF> getRoleTypes() {

    if (target == null) {
      return super.getRoleTypes();
    }
    return target.getRoleTypes();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.AssociationIF#getRolesByType(net.ontopia.topicmaps.core.TopicIF)
   */
  @Override
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {

    if (target == null) {
      return super.getRolesByType(roletype);
    }
    return target.getRolesByType(roletype);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.AssociationIF#getRoles()
   */
  @Override
  public Collection<AssociationRoleIF> getRoles() {

    if (target == null) {
      return super.getRoles();
    }
    return target.getRoles();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.ScopedIF#getScope()
   */
  @Override
  public Collection<TopicIF> getScope() {

    if (target == null) {
      return super.getScope();
    }
    return target.getScope();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.ScopedIF#addTheme(net.ontopia.topicmaps.core.TopicIF)
   */
  @Override
  public void addTheme(TopicIF theme) {

    if (target == null) {
      super.addTheme(theme);
    } else {
      target.addTheme(theme);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.ScopedIF#removeTheme(net.ontopia.topicmaps.core.TopicIF)
   */
  @Override
  public void removeTheme(TopicIF theme) {

    if (target == null) {
      super.removeTheme(theme);
    } else {
      target.removeTheme(theme);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TypedIF#getType()
   */
  @Override
  public TopicIF getType() {

    if (target == null) {
      return super.getType();
    }
    return target.getType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TypedIF#setType(net.ontopia.topicmaps.core.TopicIF)
   */
  @Override
  public void setType(TopicIF type) {

    if (target == null) {
      super.setType(type);
    } else {
      target.setType(type);
    }
  }

  @Override
  public String toString() {

    if (target == null) {
      return super.toString();
    }
    return "{" + target.toString() + "}";

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#getObjectId()
   */
  @Override
  public String getObjectId() {

    if (target == null) {
      return super.getObjectId();
    }
    return target.getObjectId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#isReadOnly()
   */
  @Override
  public boolean isReadOnly() {

    if (target == null) {
      return super.isReadOnly();
    }
    return target.isReadOnly();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#getTopicMap()
   */
  @Override
  public TopicMapIF getTopicMap() {

    if (target == null) {
      return super.getTopicMap();
    }
    return target.getTopicMap();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#getItemIdentifiers()
   */
  @Override
  public Collection<LocatorIF> getItemIdentifiers() {

    if (target == null) {
      return super.getItemIdentifiers();
    }
    return target.getItemIdentifiers();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#addItemIdentifier(net.ontopia.infoset.core.LocatorIF)
   */
  @Override
  public void addItemIdentifier(LocatorIF source_locator)
      throws ConstraintViolationException {

    if (target == null) {
      super.addItemIdentifier(source_locator);
    } else {
      target.addItemIdentifier(source_locator);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#removeItemIdentifier(net.ontopia.infoset.core.LocatorIF)
   */
  @Override
  public void removeItemIdentifier(LocatorIF source_locator) {

    if (target == null) {
      super.removeItemIdentifier(source_locator);
    } else {
      target.removeItemIdentifier(source_locator);
    }
  }

  @Override
  public void remove() {
    if (target == null) {
      super.remove();
    }
    target.remove();
  }
  
  @Override
  public boolean equals(Object obj) {

    if (obj instanceof DynamicAssociation) {
      if (target == null) {
        return ((DynamicAssociation) obj).equals(this);
      }
      return obj.equals(target);
    }

    if (target == null) {
      return super.equals(obj);
    }
    return target.equals(obj);

  }

  public boolean equals(DynamicAssociation obj) {

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

  public void setTarget(AssociationIF newTarget) {

    // This should be an interface, but at present that is not possable.
    // So I must cast !!!
    
    this.target = (Association) newTarget;
  }

  @Override
  protected boolean isConnected() {

    if (target == null) {
      return super.isConnected();
    }
    
    // IDM this cast to DynamicAssociation should not be
    // necessary and is not strictly correct, but #isConnected() 
    // is defined as protected on TMObject
    return ((DynamicAssociation)target).isConnected();
  }
}
