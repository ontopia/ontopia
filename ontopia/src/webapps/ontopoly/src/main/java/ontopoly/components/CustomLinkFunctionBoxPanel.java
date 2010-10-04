package ontopoly.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public abstract class CustomLinkFunctionBoxPanel extends LinkFunctionBoxPanel {

  public CustomLinkFunctionBoxPanel(String id) {
    super(id);
  }

  protected abstract IModel<String> getFirstResourceModel();

  protected abstract IModel<String> getSecondResourceModel();
  
  @Override
  protected Label getLabel(String id) {
    return new Label(id) {
      @Override
      protected void onComponentTagBody(MarkupStream markupStream,
          ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, 
              getFirstResourceModel().getObject()
            + "<span class='emphasis'>"
            + getSecondResourceModel().getObject()
            + "</span>");
      }
    };
  }
}
