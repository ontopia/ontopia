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
import net.ontopia.topicmaps.webed.impl.actions.topicmap.CreateTopic;
import net.ontopia.topicmaps.webed.impl.basic.Constants;

public class TestCreateTopic extends AbstractWebedTestCase {
  
  public TestCreateTopic(String name) {
    super(name);
  }
  
  
  
  /*
    1. Good case, TM no type
    2. Good case, TM, type
    3. Bad case, No params
    4. Bad case, Wrong type TM, no type
    5. Bad case, TM, wrong type type 
  */
  
  //good cases
  
  
  //some problems with this test.. why? I don't know.
  public void testGood1() throws java.io.IOException {
    //Good TM with topic as part of param.
    
    //make action
    ActionIF action = new CreateTopic();
    TopicIF topicDummy = makeTopic(tm, "snus");  
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(tm, topicDummy));
    ActionResponseIF response = makeResponse();
    //execute
    
    action.perform(params, response);      
    
    // verify that a topic was created correctly      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    assertFalse("id of topic not recorded in response parameters", id == null);
    
    TopicIF topic = (TopicIF) tm.getObjectById(id);
    
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", !(topic.getTopicMap() == tm));
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));      
    
  }
  
  public void testGood2() throws java.io.IOException {
    //Good TM with topictype as req.
    
    TopicIF topicDummy = makeTopic(tm, "snus");  
    String topicId = topicDummy.getObjectId();
    //make action
    ActionIF action = new CreateTopic();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(tm), topicId);
    ActionResponseIF response = makeResponse();
    //execute
    
    
    action.perform(params, response);      
    
    // verify that a topic was created correctly      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    
    assertFalse("id of topic not recorded in response parameters", id == null);
    
    TopicIF topic = (TopicIF) tm.getObjectById(id);
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", topic.getTopicMap() != tm);
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));
    
  }
  
  //bad cases
  
  public void testNoParameters() {
    //make action
    ActionIF topic = new CreateTopic();
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    //execute
    
    try {
      topic.perform(params, response);      
      fail("Made topic without parameters");
    } catch (ActionRuntimeException e) {	    
    }
  }
  
  public void testWrongTMType() {
    //make action
    ActionIF topic = new CreateTopic();
    //build parms
    ActionParametersIF params = makeParameters(makeList("topicmapparam"));
    ActionResponseIF response = makeResponse();
    //execute
    
    try {
      topic.perform(params, response);      
      fail("Made topic with wrong TM parameter");
    } catch (ActionRuntimeException e) {
      
    }
    
  }
  
  public void testWrongTypeType() throws java.io.IOException {
    //make action
    ActionIF topic = new CreateTopic();
    //build parms
    ActionParametersIF params = makeParameters(makeList(tm, "topicparam"));
    ActionResponseIF response = makeResponse();
    //execute
    
    try {
      topic.perform(params, response);      
      fail("Made topic with wrong type parameter");
    } catch (ActionRuntimeException e) {
      
    }
    
  }
  
  
}
