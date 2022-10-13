/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.NameGrabber;
import net.ontopia.utils.GrabberStringifier;

/**
 * INTERNAL: Helper class for storing one triple consisting of
 * (Association Type, AssociationRoleType, Associations).
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.TMvalue.AssociationTypeLoopTag
 */
public class AssocInfoStorage {

  // constant
  private static final Function<Object, String> DEF_NAME_STRINGIFIER = new CustomNameStringifier();

  // members
  private Collection associations;
  private TopicIF type;
  private TopicIF roleType;
  private String sortName;
  
  public AssocInfoStorage(TopicIF type, TopicIF roleType) {
    this.type = type;
    this.roleType = roleType;
    this.associations = null;
    this.sortName = stringify(type, roleType);
  }

  private String stringify(TopicIF type, TopicIF roleType) {
    Collection<TopicIF> scope = Collections.singleton(roleType);
    Function<TopicIF, String> grabber = new GrabberStringifier<>(new NameGrabber(scope),
                                                   DEF_NAME_STRINGIFIER);
    return grabber.apply(type);
  }
  
  public void setType(TopicIF type) {
    this.type = type;
  }
  
  public TopicIF getType() {
    return type;
  }
  
  public void setRoleType(TopicIF roleType) {
    this.roleType = roleType;
  }
  
  public TopicIF getRoleType() {
    return roleType;
  }

  public void setAssociations(Collection associations) {
    this.associations = associations;
  }

  /**
   * get collection of AssociationIF objects.
   */
  public Collection getAssociations() {
    return associations;
  }

  public String getSortName() {
    return sortName;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof AssocInfoStorage))
      return false;
    AssocInfoStorage cmp = (AssocInfoStorage) object;
    return compare(cmp.getRoleType(), roleType) &&
      compare(cmp.getType(), type);
  }

  @Override
  public int hashCode() {
    return (roleType != null ? roleType.hashCode() : 0) +
      (type != null ? type.hashCode() : 0);
  }
  
  @Override
  public String toString() {
    return "[AssocInfoStorage, AssocRoleType: " + getRoleType() +
      ", AssocType: " + getType() + "]";
  }

  // --- Internal methods

  private static final boolean compare(Object o1, Object o2) {
    return o1 == o2 ||
      (o1 != null && o1.equals(o2));
  }
  
}
