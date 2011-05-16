
// $Id: ClosureIF.java,v 1.4 2002/06/03 13:32:47 larsga Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Interface to represent some closure, a block of code
 * which is executed from inside some block, function or iteration
 * which operates on an input object.<p>
 *
 * @since 1.3.2
 */
public interface ClosureIF {

  /**
   * Performs some operation on the input object.
   */
  public void execute(Object object);

}
