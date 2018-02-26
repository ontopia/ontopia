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
import java.util.Iterator;
import java.util.ArrayList;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * INTERNAL: Represents a constraint on the allowed association roles of
 * the instances of a topic class.
 */
public class TopicRoleConstraint
                             extends AbstractScopedTypedCardinalityConstraint {
  protected TopicConstraintCollection parent;
  protected Collection assoctypes;
  
  /**
   * PUBLIC: Creates a new topic role constraint belonging to the given
   * collection.
   */
  public TopicRoleConstraint(TopicConstraintCollection parent) {
    this.parent = parent;
    this.assoctypes = new ArrayList();
  }

  /**
   * PUBLIC: Returns the set of allowed types of associations in
   * which this instances of this role may participate.
   * @return A collection of TypeSpecification objects.
   */
  public Collection getAssociationTypes() {
    return assoctypes;
  }

  /**
   * PUBLIC: Removes an allowed association type.
   */
  public void removeAssociationType(TypeSpecification typespec) {
    assoctypes.remove(typespec);
  }

  /**
   * PUBLIC: Adds an allowed association type.
   */
  public void addAssociationType(TypeSpecification typespec) {
    assoctypes.add(typespec);
  }

  // --- ConstraintIF methods

  @Override
  public boolean matches(TMObjectIF object) {
    if (object instanceof AssociationRoleIF) {
      AssociationRoleIF role = (AssociationRoleIF) object;
      AssociationIF assoc = role.getAssociation();
      if (!typespec.matches(role))
        return false;

      Iterator it = assoctypes.iterator();
      while (it.hasNext()) {
        TypeSpecification atype = (TypeSpecification) it.next();
        if (atype.matches(assoc))
          return true;
      }
    }
    return false;
  }  
  
  // --- Object methods
  
//    public String toString() {
//      return "<TopicRoleTemplate, matching " + matcher  + ">";
//    }
  
}






