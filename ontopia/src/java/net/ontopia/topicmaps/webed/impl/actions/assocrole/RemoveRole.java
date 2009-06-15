
// $Id: RemoveRole.java,v 1.1 2008/06/12 14:37:25 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.assocrole;

import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;

/**
 * PUBLIC: Action for removing an association role.
 * @since 4.0
 */
public class RemoveRole implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {

    AssociationRoleIF role = (AssociationRoleIF) params.get(0);
    role.remove();
  }
  
}
