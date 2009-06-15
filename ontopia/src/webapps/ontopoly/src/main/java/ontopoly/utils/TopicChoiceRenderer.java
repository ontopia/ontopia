package ontopoly.utils;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.utils.TopicStringifiers;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class TopicChoiceRenderer implements IChoiceRenderer {

  public static final TopicChoiceRenderer INSTANCE = new TopicChoiceRenderer();
  
  protected Topic getTopic(Object object) {
    // model objects are supported
    return (Topic)(object instanceof IModel ? ((IModel)object).getObject() : object);    
  }
  
  public Object getDisplayValue(Object object) {
    Topic topic = getTopic(object);
    if (topic == null)
      return "[No name]";
    else
    return TopicStringifiers.toString(topic.getTopicIF());    
  }

  public String getIdValue(Object object, int index) {
    Topic topic = getTopic(object);
    return topic.getId();
  }

}
