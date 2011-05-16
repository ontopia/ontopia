// $Id: ViewIF.java,v 1.3 2002/05/29 13:38:40 hca Exp $

package net.ontopia.topicmaps.nav.conf;

/**
 * PUBLIC: An interface representing a view.
 */
public interface ViewIF {

  /**
   * PUBLIC: Returns the display title of the view.
   */
  public String getTitle();

  /**
   * PUBLIC: Sets the display title of the view.
   */
  public void setTitle(String title);

  /**
   * PUBLIC: Returns the ID of the view.
   */
  public String getId();

  /**
   * PUBLIC: Sets the ID of the view.
   */
  public void setId(String id);
  
}





