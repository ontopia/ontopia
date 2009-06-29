//$Id: LoggerRepository.java,v 1.2 2005/03/18 09:26:41 ian Exp $

package org.apache.log4j.spi;

import org.apache.log4j.Level;


/**
 * INTERNAL: Dummy Interface.
 */

public interface LoggerRepository {

  public void setThreshold(Level level);
  
}
