
// $Id: ActionUtils.java,v 1.4 2006/05/15 08:38:15 larsga Exp $

package net.ontopia.topicmaps.webed.impl.utils;

import net.ontopia.topicmaps.webed.impl.basic.ActionGroupIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionInGroup;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Utility methods to ease getting access to action related
 * objects.
 */
public class ActionUtils {
  
  /**
   * Gets the action object specified by the action group and the name
   * of the action.
   */
  public static ActionInGroup getAction(ActionRegistryIF registry,
                                        String group_name, String action_name) {
    if (registry == null)
      throw new OntopiaRuntimeException("No action registry found; please " +
                                        "check your actions.xml file");
    ActionGroupIF group = registry.getActionGroup(group_name);
    if (group == null)
      throw new OntopiaRuntimeException("Group '" + group_name + "' not found.");
    return group.getAction(action_name);
  }
 
}
