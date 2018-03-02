/*
 * #!
 * Ontopia Webed
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
  
  @Override
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
