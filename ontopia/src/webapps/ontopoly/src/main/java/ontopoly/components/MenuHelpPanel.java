package ontopoly.components;

import java.util.List;

import org.apache.wicket.model.IModel;

public class MenuHelpPanel extends MenuPanel {

  public MenuHelpPanel(String id, List menuItemList, IModel helpLinkModel) {
    super(id, menuItemList);
    add(new HelpPanel("help", helpLinkModel));
  }

  public MenuHelpPanel(String id, List menuItemList, int selectedMenuItemIndex, IModel helpLinkModel) {
    super(id, menuItemList, selectedMenuItemIndex);
    add(new HelpPanel("help", helpLinkModel));
  }
}
