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

import java.io.IOException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.actions.occurrence.SetValue2;

public class TestOccSetValue2 extends TestOccSetValue {
  
  public TestOccSetValue2(String name) {
    super(name);
  }

  @Override
  public void setUp() {
    super.setUp();
    action = new SetValue2();
  }
 
  @Override
  public void testEmptyValue() throws IOException {
    // get ready
    TopicIF topic = getTopicById(tm, "tromso");
    OccurrenceIF occ = getOccurrenceWithValue(topic);
    int occsbefore = topic.getOccurrences().size();
    
    // run action
    ActionParametersIF params = makeParameters(makeList(occ, topic), "");
    ActionResponseIF response = makeResponse();
    action.perform(params, response);

    // test post-action state
    int occsnow = topic.getOccurrences().size();
    assertTrue("Occurrence was not deleted", occsbefore == occsnow + 1);
    assertTrue("Wrong occurrence deleted",
               occ.getTopic() == null);
  } 
}
