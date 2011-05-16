
// $Id: AlwaysDifferentObject.java,v 1.1 2003/12/22 19:15:08 larsga Exp $

package net.ontopia.topicmaps.webed.impl.utils;

/**
 * INTERNAL: Used as the value of form controls which are not real
 * form controls and whose actions need to always execute.
 *
 * @since 2.0
 */
public class AlwaysDifferentObject {

  public boolean equals(Object other) {
    return false;
  }
  
}
