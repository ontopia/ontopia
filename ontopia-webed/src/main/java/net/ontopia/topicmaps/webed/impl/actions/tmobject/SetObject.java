
package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;

/**
 * PUBLIC: Action for setting the ID of the object on the next page.
 *
 * @since 2.0
 */
public class SetObject implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {

    TMObjectIF object = (TMObjectIF) params.get(0);
    if (object == null)
      object = params.getTMObjectValue();

    response.addParameter(Constants.RP_TOPIC_ID, object.getObjectId());        
  }
  
}
