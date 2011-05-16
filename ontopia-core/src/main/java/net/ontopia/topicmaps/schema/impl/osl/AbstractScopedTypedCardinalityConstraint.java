// $Id: AbstractScopedTypedCardinalityConstraint.java,v 1.5 2002/05/29 13:38:43 hca Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * INTERNAL: Common base class for constraints which have cardinality,
 * type, and scope facets.
 */
public abstract class AbstractScopedTypedCardinalityConstraint
                                  extends AbstractScopedCardinalityConstraint
                                  implements TypedConstraintIF {
  protected TypeSpecification typespec;

  public void setTypeSpecification(TypeSpecification typespec) {
    this.typespec = typespec;
  }

  public TypeSpecification getTypeSpecification() {
    return typespec;
  }

  // --- ConstraintIF methods
  
  public boolean matches(TMObjectIF object) {
    return typespec.matches(object);
  }
  
}






