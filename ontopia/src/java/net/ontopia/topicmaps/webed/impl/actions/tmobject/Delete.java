
// $Id: Delete.java,v 1.11 2008/01/14 11:37:15 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for deleting a topic map object.
 */
public class Delete implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("x& x?");
    paramsType.validateArguments(params, this);
    
    Collection objects = params.getCollection(0);
    if (objects == null) {
      TMObjectIF object = params.getTMObjectValue();
      if (object != null)
        objects = Collections.singleton(object);
      else
        objects = Collections.EMPTY_SET;
    }

    Iterator it = objects.iterator();
    while (it.hasNext())
      ((TMObjectIF) it.next()).remove();
    
    TMObjectIF next = (TMObjectIF) params.get(1);
    if (next != null)
      response.addParameter(Constants.RP_TOPIC_ID, next.getObjectId());        
  }
  
}
