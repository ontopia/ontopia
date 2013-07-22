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

package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the specified value of locator address
 * to the related source locator.
 */
public class SetSourceLocator implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("x ls");
    paramsType.validateArguments(params, this);

    TMObjectIF object = (TMObjectIF) params.get(0);
    Object param1 = params.get(1);

    String newAddress = params.getStringValue();

    LocatorIF locator = null;
    if (param1 instanceof LocatorIF)
      locator = (LocatorIF) param1;
    else if (param1 instanceof String) {
      try {
        locator = new URILocator((String) param1);
      } catch (MalformedURLException e) {
        throw new ActionRuntimeException("Malformed URL for source locator: '"
            + param1 + "'", false);
      }
    }
    
    // only do anything if old locator does not equal current value
    if (locator != null && newAddress.equals(locator.getAddress()))
      return;
    
    try {
      object.addItemIdentifier(new URILocator(newAddress));
      object.removeItemIdentifier(locator);
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for source locator: '" + newAddress + "'", false);
    } catch (UniquenessViolationException e) {
      throw new ActionRuntimeException("Some other topic map object has the given source locator: '" + newAddress + "'", false);
    }
  }
  
}
