
// $Id: DeferredAction.java,v 1.3 2005/09/13 14:54:02 grove Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;

/**
 * PUBLIC: This action sets the 'deferredAction' parameter for the
 * next request to the string given, which should be an action ID.
 */
public class DeferredAction implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {

    String actionid = (String) params.get(0);
    response.addParameter("deferredAction", actionid);
        
  }
  
}
