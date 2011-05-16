// $Id: TopicRoleConstraint.java,v 1.7 2007/08/20 08:02:12 geir.gronmo Exp $

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






