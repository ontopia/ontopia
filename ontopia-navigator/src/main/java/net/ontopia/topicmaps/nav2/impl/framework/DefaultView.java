
package net.ontopia.topicmaps.nav2.impl.framework;

import net.ontopia.topicmaps.nav2.core.ViewIF;

/**
 * INTERNAL: The default data carrier for views.
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





