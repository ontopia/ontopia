
package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;


/**
 * PUBLIC: Action for setting the type of a typed object.
 */
public class SetType implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("x");
    paramsType.validateArguments(params, this);

    TypedIF typed = (TypedIF) params.get(0);
    TopicIF type = (TopicIF) params.getTMObjectValue();
    
    typed.setType(type);

  }
  
}
