
package net.ontopia.topicmaps.webed.impl.actions.association;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.actions.topicmap.AbstractTopicMapAction;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Action for setting up a unary association. If the association does
 * not already exist it will be created If the association already exists it
 * will be deleted. Action Parameters are: - association - association type -
 * topic (player A) - role type of A
 */
public class AssignUnaryAssociation extends AbstractTopicMapAction {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(AssignUnaryAssociation.class
      .getName());

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    log.debug("perform() started");
    
    // verify parameters
    ActionSignature paramsType = ActionSignature.getSignature("a t t t");
    paramsType.validateArguments(params, this);
    
    // get parameters
    AssociationIF association = (AssociationIF) params.get(0);
    log.debug("association: " + association);
    TopicIF assoctype = (TopicIF) params.get(1);
    TopicIF playerA = (TopicIF) params.get(2);
    TopicIF typeA = (TopicIF) params.get(3);
    
    TopicMapIF topicmap = playerA.getTopicMap();
    
    // last param given; only use topic if we have a request param value
    if (params.getStringValue() == null) {
      association.remove();
    } else {
      TopicMapBuilderIF builder = topicmap.getBuilder();
      association = builder.makeAssociation(assoctype);
      // create role A
      builder.makeAssociationRole(association, typeA, playerA);
    }    
  }
}
