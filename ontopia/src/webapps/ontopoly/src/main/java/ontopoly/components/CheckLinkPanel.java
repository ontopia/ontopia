package ontopoly.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;


public abstract class CheckLinkPanel extends Panel {

  public CheckLinkPanel(String id) {
    super(id);

    // add check box or radio button
    Component check = newCheck("check");
    add(check);
    
    // add link with label
    Link link = newLink("link");
    link.add(newLabel("label"));
    add(link);
  }
  
  protected abstract Component newCheck(String id);

  protected abstract Link newLink(String id);

  protected abstract Label newLabel(String id);
  
}
