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

package net.ontopia.topicmaps.impl.utils;

import java.util.Collection;
import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Class that represents the association object which a
 * deleted role might have belonged to. Note that this class exists
 * just to make the API behave gracefully in the cases where
 * association roles are deleted, so that applications do not fail
 * that easily.
 */
public class PhantomAssociation implements AssociationIF {
  private static final String MSG_CANNOT_ACCESS = "Cannot access phantom association";
  private static final String MSG_CANNOT_MODIFY = "Cannot modify phantom association";

  // -- AssociationIF

  @Override
  public Collection<TopicIF> getRoleTypes() {
    throw new PhantomAccessException(MSG_CANNOT_ACCESS);
  }

  @Override
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    throw new PhantomAccessException(MSG_CANNOT_ACCESS);
  }

  @Override
  public Collection<AssociationRoleIF> getRoles() {
    return Collections.emptySet();
  }

  // -- ScopedIF

  @Override
  public Collection<TopicIF> getScope() {
    throw new PhantomAccessException(MSG_CANNOT_ACCESS);
  }

  @Override
  public void addTheme(TopicIF theme) {
    throw new PhantomAccessException(MSG_CANNOT_MODIFY);
  }

  @Override
  public void removeTheme(TopicIF theme) {
    throw new PhantomAccessException(MSG_CANNOT_MODIFY);
  }

  // -- TypedIF

  @Override
  public TopicIF getType() {
    return null;
  }

  @Override
  public void setType(TopicIF type) {
    throw new PhantomAccessException(MSG_CANNOT_MODIFY);
  }
  
  @Override
  public String getObjectId() {
    throw new PhantomAccessException(MSG_CANNOT_ACCESS);
  }

  @Override
  public boolean isReadOnly() {
    throw new PhantomAccessException(MSG_CANNOT_ACCESS);
  }

  @Override
  public TopicMapIF getTopicMap() {
    throw new PhantomAccessException(MSG_CANNOT_ACCESS);
  }

  @Override
  public Collection<LocatorIF> getItemIdentifiers() {
    throw new PhantomAccessException(MSG_CANNOT_ACCESS);
  }

  @Override
  public void addItemIdentifier(LocatorIF srcloc) {
    throw new PhantomAccessException(MSG_CANNOT_MODIFY);
  }

  @Override
  public void removeItemIdentifier(LocatorIF srcloc) {
    throw new PhantomAccessException(MSG_CANNOT_MODIFY);
  }
  
  @Override
  public void remove() {
    throw new PhantomAccessException(MSG_CANNOT_MODIFY);
  }

  @Override
  public TopicIF getReifier() {
    throw new PhantomAccessException(MSG_CANNOT_ACCESS);
	}
  
  @Override
  public void setReifier(TopicIF reifier) {
    throw new PhantomAccessException(MSG_CANNOT_MODIFY);
	}

}
