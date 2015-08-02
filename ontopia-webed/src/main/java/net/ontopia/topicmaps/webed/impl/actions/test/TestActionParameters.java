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

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: PRIVATE: TESTING:
 */

public class TestActionParameters implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response)
      throws ActionRuntimeException {
  
    TopicMapIF tm = (TopicMapIF) params.get(0);
    TopicIF topic = (TopicIF) params.get(1);
    String string = (String) params.get(2);
    Object empty = params.get(3);
  
    LocatorIF testLocator = null;
    try {
      testLocator = new URILocator("http://psi.ontopia.net/test/test-topic");
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  
    TopicIF actualTopic = tm.getTopicBySubjectIdentifier(testLocator);
    if (actualTopic != topic) {
      response.addParameter("result", "FAILURE: Incorrect topic passed as parameter - got: "
          + actualTopic.toString() + ", expected: " + topic.toString());
      return;
    }
  
    if (!string.equals("STRING")) {
      response.addParameter("result", "FAILURE: Expected string parameter 'STRING' but got: "
          + string.toString());
      return;
    }
    
    if (empty != null) {
      response.addParameter("result", "FAILURE: Expected empty parameter but got: "
          + empty.toString());
      return;
    }
    
    response.addParameter("result", "SUCCESS");
    
  }
}
