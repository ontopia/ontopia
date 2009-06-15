
// $Id: TupleReaderIF.java,v 1.2 2006/03/17 07:51:07 grove Exp $

package net.ontopia.topicmaps.db2tm;


import net.ontopia.utils.*;

/**
 * INTERNAL: A tuple reader is an iterator-like interface for looping
 * through the tuples from a given relation.
 */
public interface TupleReaderIF {

  /**
   * INTERNAL: Returns the next tuple. Method will return null when
   * there are no more tuples.
   */
  public String[] readNext();

  // NOTE: next method intended for further performance improvements
  //! public boolean readNext(String[] tuple, int offset, int length);

  /**
   * INTERNAL: Releases all resources held by the tuple reader. This
   * method should be called when done with the tuple reader.
   */
  public void close();

}
