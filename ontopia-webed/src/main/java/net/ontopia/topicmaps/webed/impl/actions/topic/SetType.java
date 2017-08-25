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

package net.ontopia.topicmaps.webed.impl.actions.topic;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.actions.topicmap.AbstractTopicMapAction;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the type of a topic.
 */
public class SetType extends AbstractTopicMapAction {
  
  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("t");
    paramsType.validateArguments(params, this);
    
    TopicIF topic = (TopicIF) params.get(0);
    TopicIF type = (TopicIF) params.getTMObjectValue();

    // first remove all existing typing topics
    Collection typing_topics = topic.getTypes();
    if (!typing_topics.isEmpty()) {
      Object[] types = typing_topics.toArray();
      for (int i=0; i < types.length; i++) {
	topic.removeType((TopicIF)types[i]);
      }
    }
    // now set the type of this topic (if the user chose one)
    if (type != null)
      topic.addType(type);
  }
  
}
