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
import java.util.ArrayList;
import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the locator address of a subject indicator. If the
 * supplied address is empty or null, and there exists a subject indicator, then
 * the subject indicator is removed.
 *
 * @since 3.0
 */
public class SetSubjectIndicator2 implements ActionIF {

  private ActionIF set = new SetSubjectIndicator();
  private ActionIF remove = new RemoveSubjectIndicator();

  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response) {

    // test params
    ActionSignature paramsType = ActionSignature.getSignature("t ls");
    paramsType.validateArguments(params, this);

    Object param1 = params.get(1);
    String value = params.getStringValue();

    if ((value == null || value.trim().length() == 0) && param1 != null) {
      LocatorIF locator;
      if (param1 instanceof LocatorIF)
        locator = (LocatorIF) param1;
      else {
        try {
          locator = new URILocator((String) param1);
        } catch (MalformedURLException e) {
          throw new ActionRuntimeException(
              "Unable to create URI locator from '" + param1 + "'", false);
        }
      }
      ArrayList newParams = new ArrayList(2);
      newParams.add(params.getCollection(0));
      newParams.add(Collections.singletonList(locator));
      remove.perform(params.cloneAndOverride(newParams), response);
    } else
      set.perform(params, response);
  }
}
