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

package net.ontopia.topicmaps.webed.impl.actions.topicmap;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;


/**
 * PUBLIC: Action for creating a new association. It is possible to
 * specify optionally the role player and the association type.
 */
public class CreateAssoc extends AbstractTopicMapAction {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    // create new association
    
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("m t? t?");
    paramsType.validateArguments(params, this);
    
    TopicMapIF topicmap = (TopicMapIF) params.get(0);

    TopicMapBuilderIF builder = topicmap.getBuilder();
    
    TopicIF assocType = (TopicIF) params.get(1);    
		if (assocType == null) assocType = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(assocType);

    Collection scope = params.getCollection(2);
    if (scope != null) {
      Iterator it = scope.iterator();
      while (it.hasNext()) {
        TopicIF theme = (TopicIF) it.next();
        assoc.addTheme(theme);
      }
    }
    
    response.addParameter(Constants.RP_ASSOC_ID, assoc.getObjectId());
  }
  
}
