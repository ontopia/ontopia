package ontopoly.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;


public abstract class LinkPanel extends Panel {

  public LinkPanel(String id) {
    super(id);

    // add link with label
    Link<Page> link = newLink("link");
    link.add(newLabel("label"));
    add(link);
  }

  protected abstract Link<Page> newLink(String id);

  protected abstract Label newLabel(String id);
  
}
