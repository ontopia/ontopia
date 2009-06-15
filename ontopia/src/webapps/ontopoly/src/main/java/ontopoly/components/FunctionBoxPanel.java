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
    ListView nestedComponentList = new ListView("outerList",
        getFunctionBoxComponentList("content")) {
      protected void populateItem(ListItem item) {
        List componentGroups = (List) item.getModelObject();

        item.add(new ListView("innerList", componentGroups) {
          protected void populateItem(ListItem item) {
            item.add((Component) item.getModelObject());
          }
        });
      }
    };
    add(nestedComponentList);
  }

  protected abstract List getFunctionBoxComponentList(String id);
}
