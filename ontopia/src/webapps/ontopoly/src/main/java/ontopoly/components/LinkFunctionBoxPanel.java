package ontopoly.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

public abstract class LinkFunctionBoxPanel extends FunctionBoxPanel {

  public LinkFunctionBoxPanel(String id) {
    super(id);
  }

  @Override
  protected List<List<Component>> getFunctionBoxComponentList(String id) {
    List<Component> heading = Arrays.asList(new Component[] { getLabel(id) }); 
    List<Component> box = Arrays.asList(new Component[] {
        new Label(id, new ResourceModel("arrow.right")), getLink(id) });
    
    List<List<Component>> result = new ArrayList<List<Component>>(2);
    result.add(heading);
    result.add(box);
    return result;
  }

  protected abstract Component getLabel(String id);

  protected abstract Component getLink(String id);
  
}
