package ontopoly.components;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class FunctionBoxesPanel extends Panel {

  public FunctionBoxesPanel(String id) {
    super(id);
    List list = getFunctionBoxesList("functionBox"); 
    ListView functionBoxes = new ListView("functionBoxesList", list) {
      protected void populateItem(ListItem item) {
        item.add((Component) item.getModelObject());
      }
    };
    functionBoxes.setVisible(!list.isEmpty());
    add(functionBoxes);
  }

  protected abstract List getFunctionBoxesList(String id);
}
