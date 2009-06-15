package ontopoly.components;

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
  protected List getFunctionBoxComponentList(String id) {
    return Arrays.asList(new List[] {
        Arrays.asList(new Component[] { getLabel(id) }),
        Arrays.asList(new Component[] {
            new Label(id, new ResourceModel("arrow.right")), getLink(id) }) });
  }

  protected abstract Component getLabel(String id);

  protected abstract Component getLink(String id);
  
}
