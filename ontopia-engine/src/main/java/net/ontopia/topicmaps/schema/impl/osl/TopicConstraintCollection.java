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

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Abstract superclass for objects that contain topic constraints.
 */
public abstract class TopicConstraintCollection {
  protected OSLSchema schema;
  protected Collection basenames;
  protected Collection occurrences;
  protected Collection roles;
  protected Collection subrules;
  protected String id;

  /**
   * INTERNAL: Creates a new collection of topic constraints.
   * @param schema The parent schema.
   * @param id The ID of the collection. May be null.
   */
  public TopicConstraintCollection(OSLSchema schema, String id) {
    this.schema = schema;
    this.basenames = new ArrayList();
    this.occurrences = new ArrayList();
    this.roles = new ArrayList();
    this.subrules = new ArrayList();
    this.id = id;
  }

  /**
   * INTERNAL: Returns the ID of the collection. May be null.
   */
  public String getId() {
    return id;
  }

  /**
   * INTERNAL: Adds a new base name constraint to the collection.
   */
  public void addTopicNameConstraint(TopicNameConstraint constraint) {
    basenames.add(constraint);
  }

  /**
   * INTERNAL: Adds a new occurrence constraint to the collection.
   */
  public void addOccurrenceConstraint(OccurrenceConstraint constraint) {
    occurrences.add(constraint);
  }

  /**
   * INTERNAL: Adds a new role constraint to the collection.
   */
  public void addRoleConstraint(TopicRoleConstraint constraint) {
    roles.add(constraint);
  }

  /**
   * INTERNAL: Adds a new sub collection to the collection. The constraints
   * contained in the sub collection are not added to this collection,
   * but included by reference.
   */
  public void addSubRule(TopicConstraintCollection subrule) {
    subrules.add(subrule);
  }

  /**
   * INTERNAL: Returns the set of sub collections of this collection.
   * @return A collection of TopicConstraintCollection objects.
   */
  public Collection getSubRules() {
    return subrules;
  }
  
  /**
   * INTERNAL: Returns the set of base name constraints in this collection.
   * @return A collection of TopicNameConstraint objects.
   */
  public Collection getTopicNameConstraints() {
    return basenames;
  }

  /**
   * INTERNAL: Returns the set of occurrence constraints in this collection.
   * @return A collection of OccurrenceConstraint objects.
   */
  public Collection getOccurrenceConstraints() {
    return occurrences;
  }

  /**
   * INTERNAL: Returns the set of role constraints in this collection.
   * @return A collection of RoleConstraint objects.
   */
  public Collection getRoleConstraints() {
    return roles;
  }

  /**
   * INTERNAL: Returns the complete set of base name constraints in this
   * collection, including those of sub collections.
   * @return A collection of TopicNameConstraint objects.
   */
  public Collection getAllTopicNameConstraints() {
    Collection constraints = new ArrayList(basenames);
    Iterator it = subrules.iterator();
    while (it.hasNext())
      constraints.addAll(((TopicConstraintCollection) it.next()).getAllTopicNameConstraints());
    return constraints;
  }

  /**
   * INTERNAL: Returns the complete set of occurrence constraints in this
   * collection, including those of sub collections.
   * @return A collection of OccurrenceConstraint objects.
   */
  public Collection getAllOccurrenceConstraints() {
    Collection constraints = new ArrayList(occurrences);
    Iterator it = subrules.iterator();
    while (it.hasNext())
      constraints.addAll(((TopicConstraintCollection) it.next()).getAllOccurrenceConstraints());
    return constraints;
  }

  /**
   * INTERNAL: Returns the complete set of role constraints in this
   * collection, including those of sub collections.
   * @return A collection of TopicRoleConstraint objects.
   */
  public Collection getAllRoleConstraints() {
    Collection constraints = new ArrayList(roles);
    Iterator it = subrules.iterator();
    while (it.hasNext())
      constraints.addAll(((TopicConstraintCollection) it.next()).getAllRoleConstraints());
    return constraints;
  }
  
}





