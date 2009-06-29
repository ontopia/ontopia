//$Id: DummyLoggerRepository.java,v 1.1 2005/03/18 09:26:41 ian Exp $

package org.apache.log4j.spi;

import org.apache.log4j.Level;


/**
 * INTERNAL.
 * 
 * Description: Dummy Class
 */

public class DummyLoggerRepository implements LoggerRepository {

  private static DummyLoggerRepository instance =  new DummyLoggerRepository();
  public static DummyLoggerRepository getDefault() {
    
    return instance;
  }
  
  public void setThreshold(Level level) {}
  
}
