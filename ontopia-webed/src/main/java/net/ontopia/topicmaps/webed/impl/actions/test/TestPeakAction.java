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

package net.ontopia.topicmaps.webed.actions;

import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;

/**
 * INTERNAL:
 * PRIVATE:
 * TESTING:
 */

public class TestPeakAction implements ActionIF {

  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response)
      throws ActionRuntimeException {
  
    ActionParametersIF target = params.getRequest().getActionParameters("dummy");
    if (target == null) response.addParameter("value", "FAILURE: Failed to access 'dummy' action actions parameters");
    else response.addParameter("value",target.getStringValue());
  
  }

}
