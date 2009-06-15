package ontopoly.components;

import ontopoly.utils.TopicChoiceRenderer;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class TopicDropDownChoice extends DropDownChoice {

  public TopicDropDownChoice(String id, IModel model, IModel choices) {
    super(id, model, choices, TopicChoiceRenderer.INSTANCE);
  }

  public TopicDropDownChoice(String id, IModel model, IModel choices, IChoiceRenderer renderer) {
    super(id, model, choices, renderer);
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("select");
    super.onComponentTag(tag);
  }

}
