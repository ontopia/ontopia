/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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
import java.util.ArrayList;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.schema.core.ConstraintIF;

/**
 * INTERNAL: Represents an association class definition. Contains all
 * the constraints the definition consists of.
 */
public class AssociationClass implements TypedConstraintIF, ConstraintIF,
                                         ScopedConstraintIF {
  protected OSLSchema schema;
  protected Collection roles;
  protected TypeSpecification typespec;
  protected ScopeSpecification scopespec;

  /**
   * INTERNAL: Creates an association class definition belonging to the
   * schema.
   */
  public AssociationClass(OSLSchema schema) {
    this.schema = schema;
    this.roles = new ArrayList();
  }
  
  /**
   * INTERNAL: Returns the scope constraint.
   */
  public ScopeSpecification getScopeSpecification() {
    return scopespec;
  }
  
  /**
   * INTERNAL: Sets the scope constraint.
   */
  public void setScopeSpecification(ScopeSpecification scopespec) {
    this.scopespec = scopespec;
  }
  
  /**
   * INTERNAL: Sets the type constraint.
   */
  public void setTypeSpecification(TypeSpecification typespec) {
    this.typespec = typespec;
  }

  /**
   * INTERNAL: Returns the type constraint.
   */
  public TypeSpecification getTypeSpecification() {
    return typespec;
  }

  /**
   * INTERNAL: Adds a new role constraint. If the role constraint is
   * already present the call is ignored.
   */
  public void addRoleConstraint(AssociationRoleConstraint constraint) {
    roles.add(constraint);
  }

  /**
   * INTERNAL: Returns the collection of role constraints in this class
   * definition.
   */
  public Collection getRoleConstraints() {
    return roles;
  }

  /**
   * INTERNAL: Removes a role constraint from the class. If the constraint
   * is not already in the class the call is ignored.
   */
  public void removeRoleConstraint(AssociationRoleConstraint constraint) {
    roles.remove(constraint);
  }

  // --- ConstraintIF

  public boolean matches(TMObjectIF object) {
    return typespec.matches(object);
  }
  
}





