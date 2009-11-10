package ontopoly.components;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class FunctionBoxPanel extends Panel {

  public FunctionBoxPanel(String id) {
    this(id, null);
  }
  
  public FunctionBoxPanel(String id, IModel model) {
    super(id, model);
    ListView nestedComponentList = new ListView<List<Component>>("outerList",
        getFunctionBoxComponentList("content")) {
      protected void populateItem(ListItem<List<Component>> item) {
        List<Component> componentGroups = item.getModelObject();

        item.add(new ListView<Component>("innerList", componentGroups) {
          protected void populateItem(ListItem<Component> item) {
            item.add(item.getModelObject());
          }
        });
      }
    };
    add(nestedComponentList);
  }

  protected abstract List<List<Component>> getFunctionBoxComponentList(String id);
}
