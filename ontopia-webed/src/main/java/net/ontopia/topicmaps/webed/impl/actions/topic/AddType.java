
package net.ontopia.topicmaps.webed.impl.actions.topic;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for adding a type to a topic.
 */
public class AddType implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("t");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    TopicIF type = (TopicIF) params.getTMObjectValue();
    if (type == null)
      throw new ActionRuntimeException("No valid topic ID in request parameter; " +
                                       "cannot add topic type");
    
    topic.addType(type);
    
  }
  
}
