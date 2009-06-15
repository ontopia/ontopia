// $Id: RolePlayerGrabber.java,v 1.7 2004/11/29 19:01:50 grove Exp $

package net.ontopia.topicmaps.utils;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Grabber that grabs the topic that plays the role in the
 * association role.
 */

public class RolePlayerGrabber implements GrabberIF {
  
  /**
   * INTERNAL: Grabs the topic playing the role in the given association role
   *
   * @param object the given object; internally typecast to AssociationRoleIF
   * @return object which is the role player; an object implementing TopicIF
   */ 


  public Object grab(Object object) {
    return ((AssociationRoleIF)object).getPlayer();
  }

}





