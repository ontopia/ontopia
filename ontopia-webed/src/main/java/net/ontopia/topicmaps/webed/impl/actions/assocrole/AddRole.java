
package net.ontopia.topicmaps.webed.impl.actions.assocrole;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;

/**
 * PUBLIC: Action for adding a role to an association; does nothing if
 * no role player is selected.
 */
public class AddRole implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {

    AssociationIF assoc = (AssociationIF) params.get(0);    
    TopicIF player = (TopicIF) params.getTMObjectValue();
    if (player == null)
      return; // nothing to be done
    TopicIF type = (TopicIF) params.get(1);
    
    TopicMapBuilderIF builder =
      assoc.getTopicMap().getBuilder();

    builder.makeAssociationRole(assoc, type, player);
  }
  
}
