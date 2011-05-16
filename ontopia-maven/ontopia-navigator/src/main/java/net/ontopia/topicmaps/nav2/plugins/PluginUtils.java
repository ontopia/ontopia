// $Id: PluginUtils.java,v 1.5 2004/11/12 11:24:52 grove Exp $

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
  public final static boolean inPluginGroups(String groupId, List groups) {
    Iterator it = groups.iterator();
    while (it.hasNext()) {
      String curGroupId = (String) it.next();
      if (curGroupId.equals(groupId))
        return true;
    }
    return false;
  }

}





