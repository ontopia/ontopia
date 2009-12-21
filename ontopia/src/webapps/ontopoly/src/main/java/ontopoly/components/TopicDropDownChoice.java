package ontopoly.components;

import java.util.List;

import ontopoly.model.Topic;
import ontopoly.utils.TopicChoiceRenderer;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class TopicDropDownChoice<T extends Topic> extends DropDownChoice<T> {

  public TopicDropDownChoice(String id, IModel<T> model, IModel<List<T>> choices) {
    super(id, model, choices, new TopicChoiceRenderer<T>());
  }

  public TopicDropDownChoice(String id, IModel<T> model, IModel<List<T>> choices, IChoiceRenderer<T> renderer) {
    super(id, model, choices, renderer);
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("select");
    super.onComponentTag(tag);
  }

}
