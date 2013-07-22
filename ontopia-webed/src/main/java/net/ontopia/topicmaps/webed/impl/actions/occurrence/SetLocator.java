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

package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import java.net.MalformedURLException;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the locator of an external occurrence.
 */
public class SetLocator implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("o t? t?");
    paramsType.validateArguments(params, this);

    OccurrenceIF occurrence = (OccurrenceIF) params.get(0);
    TopicIF topic = (TopicIF) params.get(1);
    String value = params.getStringValue();
    // FIXME: what if value == null?

    if (occurrence == null) {
      // check if new occurrence should be created, otherwise do nothing
      if (value == null || value.trim().length() == 0)
        return;
      
      // create new occurrence
      TopicMapBuilderIF builder = topic.getTopicMap().getBuilder();
      TopicIF occtype = (TopicIF) params.get(2);
      occurrence = builder.makeOccurrence(topic, occtype, "");
    }

    try {
      URILocator loc = new URILocator(value);
      
      occurrence.setLocator(loc);
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for occurrence: '" + value + "'", false);
    }

  }
  
}
