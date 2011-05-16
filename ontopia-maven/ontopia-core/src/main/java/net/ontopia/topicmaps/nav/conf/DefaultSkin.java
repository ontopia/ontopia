// $Id: DefaultSkin.java,v 1.3 2002/05/29 13:38:40 hca Exp $

package net.ontopia.topicmaps.nav.conf;

/**
 * PUBLIC: The default data carrier for skin.
 */
public class DefaultSkin implements SkinIF {
  protected String title;
  protected String id;

  public DefaultSkin(String id) {
    this.id = id;
    this.title = id.substring(0, 1).toUpperCase() +
                 id.substring(1);
  }
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
}





