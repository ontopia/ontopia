
// $Id: SetType.java,v 1.19 2005/07/26 07:30:54 ian Exp $

package net.ontopia.topicmaps.webed.impl.actions.topic;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.actions.topicmap.AbstractTopicMapAction;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the type of a topic.
 */
public class SetType extends AbstractTopicMapAction {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("t");
    paramsType.validateArguments(params, this);
    
    TopicIF topic = (TopicIF) params.get(0);
    TopicIF type = (TopicIF) params.getTMObjectValue();

    // first remove all existing typing topics
    Collection typing_topics = topic.getTypes();
    if (!typing_topics.isEmpty()) {
      Object[] types = typing_topics.toArray();
      for (int i=0; i < types.length; i++) {
	topic.removeType((TopicIF)types[i]);
      }
    }
    // now set the type of this topic (if the user chose one)
    if (type != null)
      topic.addType(type);
  }
  
}
