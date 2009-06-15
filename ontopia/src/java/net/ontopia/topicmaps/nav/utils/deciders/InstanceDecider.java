// $Id: InstanceDecider.java,v 1.10 2008/01/11 13:50:21 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.deciders;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.utils.DeciderIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.ClassInstanceUtils;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Decider that takes either TypedIF or TopicIF and
 * returns ok if it is an instance of one of the types it was
 * constructed with.
 */
public class InstanceDecider implements DeciderIF {

  // Define a logging category.
  protected static Logger log = Logger
    .getLogger(InstanceDecider.class.getName());

  protected Collection types;

  /**
   * Constructor which takes a Collection of types.
   */
  public InstanceDecider(Collection types) {
    this.types = types;
  } 

  /**
   * Returns true if the type of a TypedIF object matches and returns
   * true if one of the types of a MultiTyped match .
   */
  public boolean ok(Object object) {
    if (object instanceof TypedIF) {
      // TypedIF can only have one type
      // Used for Associations, Roles, Occurrences.
      TypedIF typed = (TypedIF) object;
      if (types!=null && !types.isEmpty()) {
        Iterator it=types.iterator();
        while(it.hasNext()){
          TopicIF thisTopic = (TopicIF) it.next();
          if (ClassInstanceUtils.isInstanceOf(typed, thisTopic)) {
            return true;
          } 
        }
      }   
      return false;
    } else if (object instanceof TopicIF) {
      TopicIF topic = (TopicIF) object;
      if (!types.isEmpty()) {
        Iterator it = types.iterator();
        while(it.hasNext()) {
          TopicIF thisTopic = (TopicIF) it.next();
          if (ClassInstanceUtils.isInstanceOf(topic, thisTopic)) {
            return true;
          } 
        }   
      } 
      return false;      
    } else {
      log.warn("Object not suitable type: " + object.getClass().getName());
      return false;
    }
  }
  
}





