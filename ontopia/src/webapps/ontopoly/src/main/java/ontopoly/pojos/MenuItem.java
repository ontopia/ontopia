package ontopoly.pojos;

import java.io.Serializable;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

/** Class for representing a menu item for our application. */
public class MenuItem implements Serializable {

  /** the caption of the menu item */
  private Label caption;

  /** the (bookmarkable) page the menu item links to */
  private Class<? extends Page> pageClass;

  /** the (bookmarkable) page the menu item links to */
  private PageParameters pageParameters;

  private final String id;

  public MenuItem(String id, Label caption, Class<? extends Page> destination,
      PageParameters pageParameters) {
    this.id = id;
    this.caption = caption;
    this.pageClass = destination;
    this.pageParameters = pageParameters;
  }

  public String getId() {
    return id;
  }
  
  public Label getCaption() {
    return caption;
  }

  public Class<? extends Page> getPageClass() {
    return pageClass;
  }

  public PageParameters getPageParameters() {
    return pageParameters;
  }
}
