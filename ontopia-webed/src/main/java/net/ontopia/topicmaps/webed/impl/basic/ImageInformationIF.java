
// $Id: ImageInformationIF.java,v 1.2 2005/03/10 08:26:55 grove Exp $

package net.ontopia.topicmaps.webed.impl.basic;

/**
 * INTERNAL: Container for storing information about an image used to
 * display a graphical button.
 */
public interface ImageInformationIF {

  /**
   * INTERNAL: Gets the name of the image.
   */
  public String getName();

  /**
   * INTERNAL: Gets the relative URL to the image location.
   */
  public String getRelativeURL();

  /**
   * INTERNAL: Gets the width of the image in pixels.
   */
  public String getWidth();

  /**
   * INTERNAL: Gets the height of the image in pixels.
   */
  public String getHeight();

  /**
   * INTERNAL: Gets the width of the border displayed around the image
   * in pixels. Default is to display no border (equivalent to 0
   * pixels).
   */
  public String getBorder();

  /**
   * INTERNAL: Gets the align mode in which the image should be rendered. 
   * Default mode is "middle".
   */
  public String getAlign();
  
}
