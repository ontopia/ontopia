// $Id: JDOAggregateIF.java,v 1.3 2002/05/29 13:38:37 hca Exp $

package net.ontopia.persistence.query.jdo;

/**
 * INTERNAL: Represents an aggregate function in a JDO query. An
 * aggregate function can be used as part of the select and ordering
 * components of a JDO query.
 */

public interface JDOAggregateIF {

  /**
   * INTERNAL: Constant referring to the COUNT aggregate function.
   */
  public static final int COUNT = 1;

  /**
   * INTERNAL: Returns the aggregate function type indicated by one of
   * the constants in the {@link JDOAggregateIF} interface.
   */
  public int getType();

  /**
   * INTERNAL: Returns the JDOValueIF that the aggregate function is
   * to be evaluated against.
   */
  public JDOValueIF getValue();
  
}





