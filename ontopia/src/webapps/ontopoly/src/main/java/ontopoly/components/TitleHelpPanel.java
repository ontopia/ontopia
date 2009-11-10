package ontopoly.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class TitleHelpPanel extends Panel {

  public TitleHelpPanel(String id, IModel<String> titleModel, IModel<String> helpLinkModel) {
    super(id);
    add(new Label("title", titleModel));
    add(new HelpPanel("help", helpLinkModel));
  }

}
