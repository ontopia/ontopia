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

import java.util.Collections;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.topic.AddType;

public class TestAddType extends AbstractWebedTestCase {
  
  public TestAddType(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use, with player
    2. Good, Normal use, player as req.
  */

  
  public void testNormalOperation() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    int numTypes = topic.getTypes().size();

    TopicIF newTopic = makeTopic(tm, "snus");
    String newTopicId = newTopic.getObjectId();
    //make action
    ActionIF action = new AddType();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, newTopicId);
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test  
    assertFalse("Type not set...", numTypes == topic.getTypes().size());
    //assertFalse();
  }

  public void testBadNewTopicType() throws java.io.IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    int numTypes = topic.getTypes().size();

    //make action
    ActionIF action = new AddType();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();
    try {
      //execute    
      action.perform(params, response);
    
      //test  
      assertFalse("Type set...", numTypes != topic.getTypes().size());
      fail("bad newtopic type");
      //assertFalse();
    } catch (ActionRuntimeException e) {
    }
  }
  
  public void testNoParams() throws java.io.IOException{
    //make action
    ActionIF action = new AddType();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
    
      //test  
      fail("Empty params");
      //assertFalse();
    }catch (ActionRuntimeException e){
    }
  }

  
}
