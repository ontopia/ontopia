
package net.ontopia.topicmaps.webed.impl.actions.topic;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.actions.topicmap.AbstractTopicMapAction;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;


/**
 * PUBLIC: Action for removing a type from a topic.
 */
public class RemoveType extends AbstractTopicMapAction {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("t t");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    TopicIF type = (TopicIF) params.get(1);
    topic.removeType(type);
    
  }
  
}
