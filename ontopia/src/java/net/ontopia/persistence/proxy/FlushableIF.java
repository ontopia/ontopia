// $Id: FlushableIF.java,v 1.5 2002/05/29 13:38:37 hca Exp $

package net.ontopia.persistence.proxy;
  
/**
 * INTERNAL: Interface implemented by data repository accessors that
 * needs to be informed when changes to the repository needs to be
 * performed.<p>
 *
 * This interface can thus be used to implement optimized data
 * repository access.<p>
 */

public interface FlushableIF {

  /**
   * INTERNAL: Tells the object to flush itself.
   */
  public void flush() throws Exception;
  
}






