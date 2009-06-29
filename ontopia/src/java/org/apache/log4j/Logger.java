//$Id: Logger.java,v 1.4 2005/03/21 14:12:08 grove Exp $

package org.apache.log4j;


/**
 * INTERNAL.
 * 
 * Purpose: Dummy logger class to reduce the size of the jar files
 */

public class Logger {
  
  protected Logger() {}

  public static Logger getLogger(String name) {
  
    return new Logger();
  }

  public static Logger getLogger(Class aClass) {

    return new Logger();
  }

  public void info(Object object) {
  
    // Do nothing
    
  }

  public void debug(Object object) {

    // Do nothing
    
  }

  public void error(Object object) {

    // Do nothing
    
  }
  
  public void fatal(Object object) {

    // Do nothing
    
  }
  
  public void error(Object object, Throwable t) {
  
    // Do nothing
    
  }

  public void info(Object object, Throwable t) {

    // Do nothing
    
  }
  
public boolean isDebugEnabled() {
  
    return false;
  }

  public boolean isInfoEnabled() {

    return false;
  }
  

  public void warn(Object object) {
  
    // Do nothing
    
  }

  public void warn(Object object, Throwable t) {
  
    // Do nothing
    
  }

  public void removeAllAppenders() {}

  public void addAppender(Appender anAppender) {}

  public Level getLevel() {
    return null;
  }

  public void setLevel(Level level) {
  }

}
