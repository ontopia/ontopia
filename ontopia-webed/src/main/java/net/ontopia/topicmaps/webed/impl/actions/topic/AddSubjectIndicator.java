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

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for adding a subject indicator to a topic.
 */
public class AddSubjectIndicator implements ActionIF {

  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    
    ActionSignature paramsType = ActionSignature.getSignature("t");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    String untrimmedValue = params.getStringValue();
    if (untrimmedValue == null || untrimmedValue.equals(""))
      return;
    String value = untrimmedValue.trim();

    if (value.equals(Constants.RPV_DEFAULT))
      value = Constants.DUMMY_LOCATOR;
    
    try {
      topic.addSubjectIdentifier(new URILocator(value));
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for subject identifier: '" + value + "'", false);
    } catch (UniquenessViolationException e) {
      throw new ActionRuntimeException("Some other topic has the given subject identifier: '" + value + "'", false);
    }

  }
  
}
