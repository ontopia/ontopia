//$Id: Level.java,v 1.1 2005/03/07 10:07:39 ian Exp $

package org.apache.log4j;


/**
 * INTERNAL.
 * 
 * Purpose: Dummy Level
 */

public class Level {

  public static final Level WARN = new Level();
  public static final Level ALL = new Level();
  public static final Level DEBUG = new Level();
  public static final Level INFO = new Level();
  public static final Level ERROR = new Level();
  public static final Level FATAL = new Level();
  public static final Level OFF = new Level();

  protected Level() {}
}
