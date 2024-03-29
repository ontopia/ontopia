/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.plugins;

import java.util.List;
import java.util.Iterator;

/**
 * INTERNAL: Helper class for providing convenience methods to access
 * plugin and plugin group members.
 */
public final class PluginUtils {

  /**
   * INTERNAL: returns true if <code>groupId</code> occurs in list of
   * <code>groups</code>, otherwise false.
   */
  public static boolean inPluginGroups(String groupId, List groups) {
    Iterator it = groups.iterator();
    while (it.hasNext()) {
      String curGroupId = (String) it.next();
      if (curGroupId.equals(groupId)) {
        return true;
      }
    }
    return false;
  }

}





