
// $Id: SetValue.java,v 1.10 2008/06/11 16:56:03 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the value of an internal occurrence.
 */
public class SetValue implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("o t? t?");
    paramsType.validateArguments(params, this);

    // do the job
    setValue(params, response, 2);
  }

  // --- Internal methods

  protected void setValue(ActionParametersIF params, ActionResponseIF response,
                          int typeParamIx) {

    // we now assume that the caller has validated the parameters
    // for SetValue:       o t? t?
    // for SetValueUnique: o t? s? t?
    
    OccurrenceIF occurrence = (OccurrenceIF) params.get(0);
    TopicIF topic = (TopicIF) params.get(1);
    String value = params.getStringValue();

    if (!isUnique(params, response, typeParamIx))
      return;
    
    if (occurrence == null) {
      // check if new occurrence should be created, otherwise do nothing
      if (value == null || value.trim().length() == 0)
        return;
      
      // create new occurrence
      TopicMapBuilderIF builder = topic.getTopicMap().getBuilder();

      occurrence = builder.makeOccurrence(topic, 
																					(TopicIF) params.get(typeParamIx),
																					value);
    } else {    
			occurrence.setValue(value);
		}
  }  

  protected boolean isUnique(ActionParametersIF params,
                             ActionResponseIF response, int typeParamIx) {
    //  we don't check, since there's no need for it to be unique here it's
    // SetValueUnique that does this.
    return true;
  }
}
