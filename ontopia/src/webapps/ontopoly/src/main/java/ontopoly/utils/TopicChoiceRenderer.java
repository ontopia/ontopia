package ontopoly.utils;

import net.ontopia.topicmaps.utils.TopicStringifiers;

import ontopoly.model.OntopolyTopicIF;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class TopicChoiceRenderer<T extends OntopolyTopicIF> implements IChoiceRenderer<T> {

  public static final TopicChoiceRenderer INSTANCE = new TopicChoiceRenderer();
  
  protected OntopolyTopicIF getTopic(Object object) {
    // model objects are supported
    return (OntopolyTopicIF)(object instanceof IModel ? ((IModel)object).getObject() : object);    
  }
  
  public Object getDisplayValue(OntopolyTopicIF object) {
    String name = object.getName();
    if (name == null || name.equals(""))
      return "[No name]";
    else
      return name;
  }

  public String getIdValue(OntopolyTopicIF object, int index) {
    OntopolyTopicIF topic = getTopic(object);
    return topic.getId();
  }

}
