
package net.ontopia.topicmaps.webed.impl.actions;

import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;

/**
 * PUBLIC: This action does nothing.
 */
public class AddLogMessageAction implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    
    String message = (String) params.get(0);
    params.getRequest().getUser().addLogMessage(message);
      
  }
  
}
