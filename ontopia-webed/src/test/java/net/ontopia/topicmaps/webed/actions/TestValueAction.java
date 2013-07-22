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

import org.junit.Ignore;

/**
 * INTERNAL:
 * PRIVATE:
 * TESTING:
 */

@Ignore
public class TestValueAction implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response)
      throws ActionRuntimeException {

    String value = params.getStringValue();
    if (value==null) value = (String) params.get(0);
    String param = response.getParameter("value");
    String result;
    if (param==null) result = value;
    else result = param + "-" + value;
    
    if (result != null) response.addParameter("value", result);

  }

}
