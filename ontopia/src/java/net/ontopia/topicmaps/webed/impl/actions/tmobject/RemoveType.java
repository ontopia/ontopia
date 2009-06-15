
// $Id: RemoveType.java,v 1.7 2008/06/12 14:37:26 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;
/**
 * PUBLIC: Action for removing the type of an object. The object will
 * afterwards have no type. Note that this will only work on
 * TopicNameIF instances.
 */
public class RemoveType implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("x");
    paramsType.validateArguments(params, this);

    TypedIF typed = (TypedIF) params.get(0);
    typed.setType(null);
  }
  
}
