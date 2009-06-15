// $Id: AssociationRoleConstraint.java,v 1.6 2004/11/29 18:44:27 grove Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.util.Collection;
import java.util.ArrayList;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;

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





