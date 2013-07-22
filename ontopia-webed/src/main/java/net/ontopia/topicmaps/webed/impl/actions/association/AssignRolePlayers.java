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

package net.ontopia.topicmaps.webed.impl.actions.association;

import java.util.Collection;
import java.util.List;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.topicmap.AbstractTopicMapAction;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Action for setting up a Ternary association. If the association
 * does not already exist it will be created.
 */
public class AssignRolePlayers extends AbstractTopicMapAction {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(AssignRolePlayers.class
      .getName());

  public void perform(ActionParametersIF params, ActionResponseIF response) {

    // verify parameters
    ActionSignature paramsType = ActionSignature.getSignature("a? t t& t&?");
    paramsType.validateArguments(params, this);

    // get parameters
    AssociationIF association = (AssociationIF) params.get(0);
    log.debug("Association: {}", association);
    TopicIF assocType = (TopicIF) params.get(1);
    log.debug("Association Type: {}", assocType);
    List roleTypes = (List) params.getCollection(2);
    log.debug("Role Types: {}", roleTypes);
    List topics = (List) params.getCollection(3);
    log.debug("Topics: {}", topics);
    TopicIF fieldInput = (TopicIF) params.getTMObjectValue();
    log.debug("Field Input: {}", fieldInput);

    if (topics.contains(null) & fieldInput == null)
      throw new ActionRuntimeException(
          "Topics collection contained null, but no field input found");

    if (roleTypes.size() != topics.size())
      throw new ActionRuntimeException(
          "Role types and Topics collections must be the same size");
    
    TopicMapIF topicmap = assocType.getTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();

    if (association == null) {
      association = builder.makeAssociation(assocType);
    }

    for (int i = 0; i < roleTypes.size(); i++) {
      TopicIF roleType = (TopicIF) roleTypes.get(i);
      TopicIF topic = (TopicIF) topics.get(i);
      if (topic == null)
        topic = fieldInput;

      Collection roles = association.getRolesByType(roleType);
      if (roles.isEmpty())
        builder.makeAssociationRole(association, roleType, topic);
      else
        ((AssociationRoleIF) roles.iterator().next()).setPlayer(topic);
    }
  }
}
