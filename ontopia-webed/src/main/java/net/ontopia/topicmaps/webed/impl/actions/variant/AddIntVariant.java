
// $Id: AddIntVariant.java,v 1.6 2008/06/12 14:37:26 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.variant;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.actions.topicmap.AbstractTopicMapAction;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for adding an internal variant to a topic. The
 * variant scope may optionally be specified.
 */
public class AddIntVariant extends AbstractTopicMapAction {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("b t?");
    paramsType.validateArguments(params, this);

    TopicNameIF basename = (TopicNameIF) params.get(0);
    Collection themes = params.getCollection(1);
    String value = params.getStringValue().trim();
    
    TopicMapBuilderIF builder =
      basename.getTopicMap().getBuilder();
    
    // do not create variant with empty string value
    if (value == null || value.equals(""))
      return;
    
    // create new (internal) variant for base name
    VariantNameIF variant = builder.makeVariantName(basename, value);

    // set scope, if provided
    if (themes != null) {
      Iterator it = themes.iterator();
      while (it.hasNext()) 
        variant.addTheme((TopicIF) it.next());
    }
  }
}
