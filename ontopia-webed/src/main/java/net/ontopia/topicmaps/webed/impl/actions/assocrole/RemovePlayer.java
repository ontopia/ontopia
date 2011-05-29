
package net.ontopia.topicmaps.webed.impl.actions.assocrole;

import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;

/**
 * PUBLIC: Action for removing the role player from an association role.
 */
public class RemovePlayer implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {

    AssociationRoleIF role = (AssociationRoleIF) params.get(0);
    //! TopicIF player = (TopicIF) params.get(1);
    
    role.setPlayer(null); // don't really need the topic, actually
  }
  
}
