
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
