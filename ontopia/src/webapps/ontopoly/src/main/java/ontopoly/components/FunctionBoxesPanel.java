package ontopoly.components;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class FunctionBoxesPanel extends Panel {

  public FunctionBoxesPanel(String id) {
    super(id);
    
    Form form = new Form("functionBoxesForm");
    add(form);
    
    List<Component> list = getFunctionBoxesList("functionBox"); 
    ListView<Component> functionBoxes = new ListView<Component>("functionBoxesList", list) {
      protected void populateItem(ListItem item) {
        item.add((Component)item.getDefaultModelObject());
      }
    };
    functionBoxes.setVisible(!list.isEmpty());
    form.add(functionBoxes);
  }

  protected abstract List<Component> getFunctionBoxesList(String id);
}
