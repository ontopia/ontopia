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

  public MenuItem(Label caption, Class<? extends Page> pageClass) {
    this.caption = caption;
    this.pageClass = pageClass;
    this.pageParameters = null;
  }

  public MenuItem(Label caption, Class<? extends Page> destination,
      PageParameters pageParameters) {
    this.caption = caption;
    this.pageClass = destination;
    this.pageParameters = pageParameters;
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
