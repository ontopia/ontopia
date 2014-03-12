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

/**
 * INTERNAL: Represents a constraint on the association roles of an
 * association.
 */
public class AssociationRoleConstraint
                                   extends AbstractTypedCardinalityConstraint {
  protected AssociationClass parent;
  protected Collection players;

  /**
   * INTERNAL: Creates an association role constraint belonging to an
   * association class.
   */
  public AssociationRoleConstraint(AssociationClass parent) {
    this.parent = parent;
    this.players = new ArrayList();
  }

  /**
   * INTERNAL: Returns the set of allowed types of topics playing this role.
   * @return A collection of TypeSpecification objects.
   */
  public Collection getPlayerTypes() {
    return players;
  }

  /**
   * INTERNAL: Removes an topic player type from the set of allowed
   * player types.
   */
  public void removePlayerType(TypeSpecification typespec) {
    players.remove(typespec);
  }

  /**
   * INTERNAL: Add a new type to the allowed types of role players.
   */
  public void addPlayerType(TypeSpecification typespec) {
    players.add(typespec);
  }
  
}





