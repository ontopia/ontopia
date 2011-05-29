
package net.ontopia.topicmaps.utils;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Grabber that grabs the association roles of an association.
 */

public class RolesGrabber implements GrabberIF {
  
  /**
   * INTERNAL: Grabs the association roles of the given association
   *
   * @param object the given object; internally typecast to AssociationIF
   * @return object which is a collection of AssociationRoleIF objects
   */ 

  public Object grab(Object object) {
    return ((AssociationIF)object).getRoles();
  }

}





