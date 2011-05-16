
// $Id: RemoveTheme.java,v 1.7 2005/09/13 14:54:02 grove Exp $

package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for removing a theme from a scoped object.
 */
public class RemoveTheme implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
     //test params
    ActionSignature paramsType = ActionSignature.getSignature("baov");
    paramsType.validateArguments(params, this);

    ScopedIF scoped = (ScopedIF) params.get(0);
    TopicIF theme = (TopicIF) params.getTMObjectValue();
    if (theme == null)
      throw new ActionRuntimeException("No topic ID given to RemoveTheme action");
    
    scoped.removeTheme(theme);

  }
}
