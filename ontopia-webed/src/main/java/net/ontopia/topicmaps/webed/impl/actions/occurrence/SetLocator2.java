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

package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import java.util.Collections;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.Delete;

/**
 * PUBLIC: Action for setting the locator of an external occurrence. If there
 * already exists an occurrence and the supplied value is null, or an empty
 * string, then the occurrence is deleted.
 *
 * @since 3.0
 */
public class SetLocator2 implements ActionIF {
  private ActionIF setLocator = new SetLocator();
  private ActionIF delete = new Delete();

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    // test params
    ActionSignature paramsType = ActionSignature.getSignature("o t? t?");
    paramsType.validateArguments(params, this);

    OccurrenceIF occurrence = (OccurrenceIF) params.get(0);
    String value = params.getStringValue();

    if ((value == null || value.trim().length() == 0) && occurrence != null)
      delete.perform(params.cloneAndOverride(Collections
          .singletonList(Collections.singletonList(occurrence))), response);
    else
      setLocator.perform(params, response);
  }
}
