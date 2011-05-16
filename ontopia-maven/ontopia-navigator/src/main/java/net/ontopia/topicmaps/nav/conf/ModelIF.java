// $Id: ModelIF.java,v 1.3 2002/05/29 13:38:40 hca Exp $

package net.ontopia.topicmaps.nav.conf;

/**
 * PUBLIC: An interface representing a model.
 */
public interface ModelIF {

  /**
   * PUBLIC: Returns the display title of the model.
   */
  public String getTitle();

  /**
   * PUBLIC: Sets the display title of the model.
   */
  public void setTitle(String title);

  /**
   * PUBLIC: Returns the ID of the model.
   */
  public String getId();

  /**
   * PUBLIC: Sets the ID of the model.
   */
  public void setId(String id);
  
}





