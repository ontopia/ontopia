//$Id: LogManager.java,v 1.3 2005/03/18 09:26:41 ian Exp $

package org.apache.log4j;

import org.apache.log4j.spi.DummyLoggerRepository;
import org.apache.log4j.spi.LoggerRepository;


/**
 * INTERNAL.
 * 
 * Description: Dummy Class
 */

public class LogManager {

  public static LoggerRepository getLoggerRepository() {
    
    return DummyLoggerRepository.getDefault();    
  }
  
}
