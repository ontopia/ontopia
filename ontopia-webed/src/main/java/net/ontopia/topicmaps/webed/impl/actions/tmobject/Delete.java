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
