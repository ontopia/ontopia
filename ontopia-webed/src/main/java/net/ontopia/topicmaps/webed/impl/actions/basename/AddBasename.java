
package net.ontopia.topicmaps.webed.impl.actions.basename;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;


/**
 * PUBLIC: Action for adding a topic name to a topic. The scope may
 * optionally be specified.
 */
public class AddBasename implements ActionIF {

  public void perform(ActionParametersIF params,
                      ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("t t?& t?");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    Collection themes = params.getCollection(1);
    TopicIF type = (TopicIF) params.get(2);
    String value = params.getStringValue().trim();
    
    // do not create name with empty string value
    if (value == null || value.equals(""))
      return;
    
    // create new topic name for topic
    TopicMapBuilderIF builder =
      topic.getTopicMap().getBuilder();
    TopicNameIF basename = builder.makeTopicName(topic, value);

    // retrieve (optional) basename type from first parameter and set
    if (type != null) 
      basename.setType(type);

    // set scope, if provided
    if (themes != null) {
      Iterator it = themes.iterator();
      while (it.hasNext()) 
        basename.addTheme((TopicIF) it.next());
    }
  }
}
