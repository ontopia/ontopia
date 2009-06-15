
// $Id: ActionInGroup.java,v 1.1 2003/12/22 19:14:36 larsga Exp $

package net.ontopia.topicmaps.webed.impl.basic;

import net.ontopia.topicmaps.webed.core.ActionIF;

/**
 * INTERNAL: Represents an action given a name inside an action group.
 */
public class ActionInGroup {
  private String name;
  private ActionIF action;
  private boolean exclusive;

  public ActionInGroup(ActionIF action, String name, boolean exclusive) {
    this.action = action;
    this.name = name;
    this.exclusive = exclusive;
  }

  public ActionIF getAction() {
    return action;
  }

  public String getName() {
    return name;
  }

  public boolean isExclusive() {
    return exclusive;
  }

  public String toString() {
    return "<ActionInGroup " + name + " " + action + ">";
  }
  
}
