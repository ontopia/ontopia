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

package net.ontopia.topicmaps.webed.impl.actions;

import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: This action sets the forward page.
 *
 * @since 2.0
 */
public class SetForwardAction implements ActionIF {
  
  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response)
    throws ActionRuntimeException {
    
    ActionSignature paramsType = ActionSignature.getSignature("s? s?+");
    paramsType.validateArguments(params, this);
    
    // set basic URI
    String uri = (String) params.get(0);
    if (uri == null) {
      uri = params.getStringValue();
      if (uri == null || uri.equals(""))
        return;
    }    
    response.setForward(uri);

    // set params to forward to
    for (int ix = 1; params.get(ix) != null; ix++) {
      if (!(params.get(ix) instanceof String))
        throw new ActionRuntimeException("Parameter " + ix + " to SetForwardAction "+
                                         "should be string, but was " +
                                         params.get(ix).getClass().getName());
      String param = (String) params.get(ix);
      response.addParameter(param, null); // value comes from request
    }
  }
}
