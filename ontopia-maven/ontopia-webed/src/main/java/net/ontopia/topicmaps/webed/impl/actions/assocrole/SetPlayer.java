
// $Id: SetPlayer.java,v 1.9 2008/05/28 10:30:50 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.assocrole;

import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for adding role player to an association role.
 */
public class SetPlayer implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
     //test params
    ActionSignature paramsType = ActionSignature.getSignature("r t?");
    paramsType.validateArguments(params, this);

    AssociationRoleIF role = (AssociationRoleIF) params.get(0);
    if (role == null)
      throw new ActionRuntimeException("No role given to SetPlayer action");
    
    TopicIF player = (TopicIF) params.get(1);
    if (player == null) {
      player = (TopicIF) params.getTMObjectValue();
      if (player == null)
        throw new ActionRuntimeException("No player to be set given to SetPlayer action");
    }

    role.setPlayer(player);
  }
  
}
