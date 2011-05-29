
package net.ontopia.topicmaps.webed.impl.actions;

import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;

/**
 * PUBLIC: This action does nothing. It is generally used when a form
 * control requires an action, but the necessary modifications are
 * performed by actions attached to other controls.
 *
 * @since 2.0
 */
public class DummyAction implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
  }
  
}
