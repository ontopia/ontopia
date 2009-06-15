package ontopoly.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public abstract class CustomLinkFunctionBoxPanel extends LinkFunctionBoxPanel {

  public CustomLinkFunctionBoxPanel(String id) {
    super(id);
  }

  protected abstract IModel getFirstResourceModel();

  protected abstract IModel getSecondResourceModel();
  
  @Override
  protected Label getLabel(String id) {
    return new Label(id) {
      @Override
      protected void onComponentTagBody(MarkupStream markupStream,
          ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, 
              getFirstResourceModel().getObject().toString()
            + "<span class='emphasis'>"
            + getSecondResourceModel().getObject().toString()
            + "</span>");
      }
    };
  }
}
