package ontopoly.components;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class HelpPanel extends Panel {

  public HelpPanel(String id, IModel<String> helpLinkModel) {
    super(id);
    add(new ExternalLink("helpLink", helpLinkModel));
  }

}
