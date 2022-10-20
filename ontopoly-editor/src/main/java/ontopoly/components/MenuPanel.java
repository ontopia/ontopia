/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
package ontopoly.components;

import java.util.List;

import ontopoly.pojos.MenuItem;

import org.apache.wicket.Page;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;


public class MenuPanel extends Panel {

  public MenuPanel(String id, List<MenuItem> menuItemList, final int selectedMenuItemIndex) {
    super(id);

    ListView<MenuItem> menuItems = new ListView<MenuItem>("menuItems", menuItemList) {
      int counter = 0;

      @Override
      protected void populateItem(ListItem<MenuItem> item) {
        MenuItem menuItem = item.getModelObject();
        BookmarkablePageLink<Page> link = new BookmarkablePageLink<Page>("menuItemLink",
            menuItem.getPageClass(), menuItem.getPageParameters());
        link.add(menuItem.getCaption());
        item.add(link);

        if (counter == selectedMenuItemIndex) {
          if (counter > 0) {          
            item.add(new SimpleAttributeModifier("class", "delimiter selected"));
          } else {
            item.add(new SimpleAttributeModifier("class", "selected"));
          }
        } else {
          if (counter > 0) {
            item.add(new SimpleAttributeModifier("class", "delimiter"));
          }          
        }
        counter++;
      }
    };
    add(menuItems);
  }
}
