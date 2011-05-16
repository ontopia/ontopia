// $Id: DefaultView.java,v 1.3 2002/05/29 13:38:40 hca Exp $

package net.ontopia.topicmaps.nav.conf;

/**
 * PUBLIC: The default data carrier for views.
 */
public class DefaultView implements ViewIF {
  protected String title;
  protected String id;

  public DefaultView(String id, String title) {
    this.id = id;
    this.title = title;
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





