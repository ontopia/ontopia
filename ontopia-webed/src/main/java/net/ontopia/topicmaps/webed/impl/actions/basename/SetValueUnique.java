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

package net.ontopia.topicmaps.webed.impl.actions.basename;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;


/**
 * PUBLIC: Action for setting the string value of a topic name. Creates
 * a new topic name if none already exists. Also checks whether or not
 * there already is another topic name with the same scope and value,
 * and if there is it logs the fact.
 *
 * @since 2.0
 */
public class SetValueUnique extends SetValue {


  public void perform(ActionParametersIF params, ActionResponseIF response) {
    // test params
    ActionSignature paramsType = ActionSignature.getSignature("b t? t?& s? t?");
    paramsType.validateArguments(params, this);

    // do the job
    setValue(params, response, 4);
  }
  
  
  protected boolean isUnique(ActionParametersIF params, ActionResponseIF response, int typeParamIx) {
    TopicIF topic = (TopicIF) params.get(1);
    Collection scope = params.getCollection(2);
    TopicIF type = (TopicIF)params.get(typeParamIx);
    String value = params.getStringValue();
    
    NameIndexIF nameindex = (NameIndexIF) topic.getTopicMap()
      .getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");

    Iterator it = nameindex.getTopicNames(value).iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();
      if (bn.getTopic().equals(topic))
        continue; // we've found ourselves

      TopicIF bntype = bn.getType();
      if (type == bntype || (bntype != null && bntype.equals(type))) {
	Collection bnscope = bn.getScope();
	if ((scope == null && bnscope.isEmpty()) ||
	    (bnscope.containsAll(scope) && scope.containsAll(bnscope))) {
	  // uh oh. we've found a duplicate
	  params.getRequest().getUser().addLogMessage("The name '" + value  +
						      "' is already in use");
	  
	  String forwardto = (String) params.get(3);
	  if (forwardto != null)
	    response.setForward(forwardto);
	  
	  return false;
	}
      }
    }

    return true;
  }
  
}
