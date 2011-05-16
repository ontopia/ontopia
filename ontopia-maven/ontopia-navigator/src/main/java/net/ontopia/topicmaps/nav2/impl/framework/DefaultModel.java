// $Id: DefaultModel.java,v 1.4 2002/08/26 15:02:59 grove Exp $

package net.ontopia.topicmaps.nav2.impl.framework;

import net.ontopia.topicmaps.nav2.core.ModelIF;

/**
 * INTERNAL: The default data carrier for models.
 */
public class DefaultModel implements ModelIF {
  protected String title;
  protected String id;

  public DefaultModel(String id, String title) {
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





