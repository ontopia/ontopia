
// $Id: DefaultAction.java,v 1.16 2005/09/13 14:54:02 grove Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;

/**
 * PUBLIC: This action does nothing.
 * @deprecated
 */
public class DefaultAction implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
  }
  
}
