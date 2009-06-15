
// $Id: SetSubjectIndicator.java,v 1.26 2008/06/13 08:17:57 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.topic;

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the locator address of a subject
 * indicator.
 */
public class SetSubjectIndicator implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("t ls");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    Object param1 = params.get(1);

    String newAddress = params.getStringValue();
    
    LocatorIF locator = null;
    if (param1 instanceof LocatorIF)
      locator = (LocatorIF) param1;
    else if (param1 instanceof String) {
      try {
        locator = new URILocator((String) param1);
      } catch (MalformedURLException e) {
        throw new ActionRuntimeException("Malformed URL for subject indicator: '"
            + param1 + "'", false);
      }
    } else if (param1 == null) {
      // Found empty Collection, so must add PSI using the body.
            
    } else
      throw new ActionRuntimeException("Unable to create URI locator."
              + " Second parameter to SetSubjectIndicator must be either a"
              + " LocatorIF or a String, but was a " 
              + param1.getClass().getName() + ".", true);
    
    // only do anything if old locator does not equal current value
    if (locator != null && newAddress.equals(locator.getAddress()))
      return;
    
    try {
      topic.addSubjectIdentifier(new URILocator(newAddress));
      if (locator != null && 
          (param1 instanceof LocatorIF || param1 instanceof String))
        topic.removeSubjectIdentifier(locator);
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for subject identifier: '" + newAddress + "'", false);
    } catch (UniquenessViolationException e) {
      throw new ActionRuntimeException("Some other topic has the given subject identifier: '" + newAddress + "'", false);
    }
  }
  
}
