package ontopoly.components;

import java.util.List;

import ontopoly.pojos.MenuItem;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;


public class MenuPanel extends Panel {
  //TODO remove this constructor. Make the callers call the other method instead with -1 (NONE_SELECTED) as
  // the third argument.
  public MenuPanel(String id, List menuItemList) {
    super(id); 

    ListView menuItems = new ListView("menuItems", menuItemList) {
      int counter = 0;

      protected void populateItem(ListItem item) {
        MenuItem menuItem = (MenuItem) item.getModelObject();
        BookmarkablePageLink link = new BookmarkablePageLink("menuItemLink",
            menuItem.getPageClass(), menuItem.getPageParameters());
        link.add(menuItem.getCaption());
        item.add(link);
        if (counter++ != 0) {
          item.add(new SimpleAttributeModifier("class", "delimiter"));
        }

      }
    };
    add(menuItems);
  }

  public MenuPanel(String id, List menuItemList, final int selectedMenuItemIndex) {
    super(id);

    ListView menuItems = new ListView("menuItems", menuItemList) {
      int counter = 0;

      protected void populateItem(ListItem item) {
        MenuItem menuItem = (MenuItem) item.getModelObject();
        BookmarkablePageLink link = new BookmarkablePageLink("menuItemLink",
            menuItem.getPageClass(), menuItem.getPageParameters());
        link.add(menuItem.getCaption());
        item.add(link);

        if (counter == selectedMenuItemIndex) {
          if (counter > 0)          
            item.add(new SimpleAttributeModifier("class", "delimiter selected"));
          else
            item.add(new SimpleAttributeModifier("class", "selected"));
        } else {
          if (counter > 0)
            item.add(new SimpleAttributeModifier("class", "delimiter"));          
        }
        counter++;
      }
    };
    add(menuItems);
  }
}
