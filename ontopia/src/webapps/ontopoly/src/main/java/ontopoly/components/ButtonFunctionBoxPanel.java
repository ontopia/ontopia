package ontopoly.components;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public abstract class ButtonFunctionBoxPanel extends FunctionBoxPanel {

  public ButtonFunctionBoxPanel(String id) {
    this(id, null);
  }
  
  public ButtonFunctionBoxPanel(String id, IModel model) {
    super(id, model);
  }

  @Override
  protected List getFunctionBoxComponentList(String id) {
    return Arrays.asList(new List[] {
        Arrays.asList(new Component[] { new Label(id, getText()) }),
        Arrays.asList(new Component[] { getButton(id) }) });
  }

  protected Component getButton(String id) {

    AjaxLink button = new AjaxLink(id) {

      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.setName("input");
        tag.put("type", "button");
        tag.put("value", getButtonLabel().getObject().toString());
        super.onComponentTag(tag);
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
        ButtonFunctionBoxPanel.this.onClick(target);
      }
    };

    List behaviors = getButtonBehaviors();
    if (behaviors != null) {
      Iterator it = behaviors.iterator();
      while (it.hasNext()) {
        button.add((IBehavior) it.next());
      }
    }
    return button;
  }

  protected abstract void onClick(AjaxRequestTarget target);

  protected abstract IModel getText();

  protected abstract IModel getButtonLabel();

  protected List getButtonBehaviors() {
    return null;
  }
}
