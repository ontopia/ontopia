/*
 * #!
 * Ontopia Webed
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
