package ontopoly.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;


public abstract class LinkPanel extends Panel {

  public LinkPanel(String id) {
    super(id);

    // add link with label
    Link link = newLink("link");
    link.add(newLabel("label"));
    add(link);
  }

  protected abstract Link newLink(String id);

  protected abstract Label newLabel(String id);
  
}
