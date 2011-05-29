
package net.ontopia.topicmaps.webed.impl.actions.variant;

import java.net.MalformedURLException;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the locator of an external variant.
 */
public class SetLocator implements ActionIF {

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

    try {
      URILocator loc = new URILocator(value);

      variant.setLocator(loc);
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for variant: '" + value + "'", false);
    }

  }
  
}
