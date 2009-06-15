
// $Id: SetValue.java,v 1.16 2008/06/13 12:31:33 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.basename;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

import org.apache.log4j.Logger;

/**
 * PUBLIC: Action for setting the string value of a topic name. Creates
 * a new topic name if none already exists. 
 */
public class SetValue implements ActionIF {

  // initialization of logging facility
  private static Logger log = Logger.getLogger(SetValue.class.getName());

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    // test params
    ActionSignature paramsType = ActionSignature.getSignature("b t? t?& t?");
    paramsType.validateArguments(params, this);

    // do the job
    setValue(params, response, 3);
  }

  // --- Internal methods
  
  protected void setValue(ActionParametersIF params, ActionResponseIF response, int typeParamIx) {

    // we now assume that the caller has validated the parameters
    // for SetValue:       b t? t?& t?
    // for SetValueUnique: b t? t?& s? t?
    
    TopicNameIF basename = (TopicNameIF) params.get(0); // the topic name
    TopicIF topic = (TopicIF) params.get(1);          // the owner

    log.debug("Setting topic name " + basename + " on " + topic);
    
    // check if there was a value
    String value = params.getStringValue();
    if (value == null)
      return; // this *can* happen; we've had reports of it
    
    if (value.trim().equals("")) {
      // no value, so remove the topic name
      if (basename != null) {
        if (topic == null) // poor usage, but we allow it, since we can work around it
          topic = basename.getTopic();
        basename.remove();
        log.debug("No string value, so removed topic name.");
      } else
        log.debug("No topic name and no string value, so doing nothing.");
      return;
    } else if (!isUnique(params, response, typeParamIx))
      return; // don't do anything more; method has already reported problem
    
    if (basename == null) {
      // check that we have a topic
      if (topic == null)
        throw new ActionRuntimeException("Topic name and topic parameters both empty;"+
                                         " cannot create new topic name");
      
      // no topic name, so create one
      TopicMapBuilderIF builder = topic.getTopicMap().getBuilder();
      basename = builder.makeTopicName(topic, value);
      log.debug("Created topic name");

      // set type of topic name (optional)
      TopicIF type = (TopicIF) params.get(typeParamIx);
      if (type != null) 
        basename.setType(type);

      // if a scope is given, apply it
      Collection scope = params.getCollection(2);
      log.debug("Scope parameter was: " + scope);
      if (scope != null) {
        Iterator it = scope.iterator();
        while (it.hasNext()) {
          TopicIF theme = (TopicIF) it.next(); 
          basename.addTheme(theme);
          log.debug("Added theme " + theme);
        }
      }
    } else {
      // simply set the value
      basename.setValue(value);
      log.debug("Set value of existing topic name");
    }
  }

  protected boolean isUnique(ActionParametersIF params, ActionResponseIF response, int typeParamIx) {
    return true; // we don't check, since there's no need for it to be unique here
    // it's SetValueUnique that does this
  }
  
}
