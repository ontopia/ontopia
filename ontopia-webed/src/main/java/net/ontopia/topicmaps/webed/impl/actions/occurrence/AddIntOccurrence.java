
package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for adding an internal occurrence to a topic. The
 * occurrence type and scope may optionally be specified.
 */
public class AddIntOccurrence implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("t t t?&");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    TopicIF occtype = (TopicIF) params.get(1);
    Collection themes = params.getCollection(2);
    String value = params.getStringValue().trim();
    
    TopicMapBuilderIF builder = topic.getTopicMap().getBuilder();
    // do not create occurrence with empty string value
    if (value == null || value.equals(""))
      return;
    
    // create new (internal) occurrence for topic
    OccurrenceIF occurrence = builder.makeOccurrence(topic, occtype, value);

    // set scope, if provided
    if (themes != null) {
      Iterator it = themes.iterator();
      while (it.hasNext()) 
        occurrence.addTheme((TopicIF) it.next());
    }
  }
}
