
package net.ontopia.topicmaps.webed.taglibs;

import net.ontopia.topicmaps.webed.impl.utils.ActionData;

/**
 * INTERNAL: Implemented by tags which accept sub-actions that will be
 * invoked after their main action.
 *
 * @since 2.0
 */
public interface ActionInvokingTagIF {

  /**
   * Accepts the action and adds it to the list of actions to be
   * invoked together with the primary action.
   */
  public void addAction(ActionData action);

}
