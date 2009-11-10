package ontopoly.components;

import java.util.List;

import ontopoly.pojos.MenuItem;

import org.apache.wicket.model.IModel;

public class MenuHelpPanel extends MenuPanel {

  public MenuHelpPanel(String id, List<MenuItem> menuItemList, IModel<String> helpLinkModel) {
    super(id, menuItemList);
    add(new HelpPanel("help", helpLinkModel));
  }

  public MenuHelpPanel(String id, List<MenuItem> menuItemList, int selectedMenuItemIndex, IModel<String> helpLinkModel) {
    super(id, menuItemList, selectedMenuItemIndex);
    add(new HelpPanel("help", helpLinkModel));
  }
}
