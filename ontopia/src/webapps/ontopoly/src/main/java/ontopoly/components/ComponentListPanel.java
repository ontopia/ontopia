package ontopoly.components;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class ComponentListPanel extends Panel {

  public ComponentListPanel(String id, List components) {
    super(id);

    ListView componentList = new ListView("componentList", components) {
      protected void populateItem(ListItem item) {
        item.add((Component) item.getModelObject());
      }
    };
    add(componentList);
  }
}
