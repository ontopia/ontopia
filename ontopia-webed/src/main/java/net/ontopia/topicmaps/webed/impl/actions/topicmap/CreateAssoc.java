
// $Id: CreateAssoc.java,v 1.23 2008/05/23 09:24:24 geir.gronmo Exp $

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
