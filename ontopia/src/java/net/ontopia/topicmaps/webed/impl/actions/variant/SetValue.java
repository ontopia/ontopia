
// $Id: SetValue.java,v 1.8 2008/06/12 14:37:26 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.variant;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the value of an internal variant.
 */
public class SetValue implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("v b?");
    paramsType.validateArguments(params, this);

    VariantNameIF variant = (VariantNameIF) params.get(0);
    TopicNameIF basename = (TopicNameIF) params.get(1);
    String value = params.getStringValue();

    if (variant == null) {
      // check if new variant should be created, otherwise do nothing
      if (value.trim().length() == 0)
        return;
      
      // create new occurrence
      TopicMapBuilderIF builder = basename.getTopicMap().getBuilder();
      variant = builder.makeVariantName(basename, "");
    }
    
    variant.setValue(value);

  }
  
}
