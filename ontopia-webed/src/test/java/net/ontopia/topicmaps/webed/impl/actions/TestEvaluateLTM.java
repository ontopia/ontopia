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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.WebEdRequestIF;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.EvaluateLTM;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.basic.WebEdRequest;

public class TestEvaluateLTM extends AbstractWebedTestCase {
  
  public TestEvaluateLTM(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Does %new% work?
    3. Does %value% work?
    4. Does %topic% work?
    3. Bad , No good topicId
  */


  public void testNormalOperation() throws IOException {
    ActionIF action = new EvaluateLTM();
    String ltm = "[%new% : team = \"snus\"]";
      
    // build parms
    ActionParametersIF params = makeParameters(makeList(tm, ltm), "mama");
    ActionResponseIF response = makeResponse();
      
    // execute    
    action.perform(params, response);
      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    assertFalse("id of topic not recorded in response parameters", id == null);
      
    TopicIF topic = (TopicIF) tm.getObjectById(id);
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", topic.getTopicMap() != tm);
    assertFalse("created topic hasn't basename", topic.getTopicNames().isEmpty());
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));      
  }

  public void testValue() throws IOException{
    ActionIF action = new EvaluateLTM();
    String ltm = "[%new% : team = \"%value%\"]";
      
    //build parms
    ActionParametersIF params = makeParameters(makeList(tm, ltm), "mama");
    ActionResponseIF response = makeResponse();
      
    //execute    
    action.perform(params, response);
      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    assertFalse("id of topic not recorded in response parameters", id == null);
      
    TopicIF topic = (TopicIF) tm.getObjectById(id);
      
    assertFalse("created topic hasn't basename", topic.getTopicNames().isEmpty());

    Iterator<TopicNameIF> i = topic.getTopicNames().iterator();
    boolean basenameIsCorr = false;
      
    while (i.hasNext()){
      TopicNameIF name = i.next();

      if (name.getValue().equals("mama")){
        basenameIsCorr = true;
      }
    } 
      
    assertFalse("basename is not correct", !(basenameIsCorr));
    assertFalse("More then one basename", topic.getTopicNames().size() != 1);
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", topic.getTopicMap() != tm);
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));      
  }


  public void testMoreValue() throws IOException {
    ActionIF action = new EvaluateLTM();
    String ltm = "[%new% : %team%  = \"%value%\"]";
      
    // build params
    // type becomes the topic "team", base name value is "mama"
    Map actionmap = new HashMap();
    actionmap.put("team", makeParameters(null, "team2",
                                         getTopicById(tm, "team").getObjectId()));
    WebEdRequestIF request = new WebEdRequest(null, actionmap, null, null);
    ActionParametersIF params = makeParameters(makeList(tm, ltm), "mama", request);
    ActionResponseIF response = makeResponse();
      
    //execute    
    action.perform(params, response);
      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    assertFalse("id of topic not recorded in response parameters", id == null);
      
    TopicIF topic = (TopicIF) tm.getObjectById(id);
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", topic.getTopicMap() != tm);
      
    //check if correct basename
    assertFalse("created topic has wrong number of base names",
                topic.getTopicNames().size() != 1);
    TopicNameIF bn = topic.getTopicNames().iterator().next();
    assertFalse("basename is not correct", !bn.getValue().equals("mama"));
    assertFalse("basename scope is not empty", !bn.getScope().isEmpty());
      
    //check if correct type
    TopicIF corrType = getTopicById(tm, "team");
      
    boolean typeIsCorr = topic.getTypes().contains(corrType);

    assertFalse("type is not correct", !(typeIsCorr));
    assertFalse("More then one type", topic.getTypes().size() != 1);    
      
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));      
  }
  
  public void testIdForSrclocatorlessTopic() throws IOException {
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF newtopic = builder.makeTopic();
    
    ActionIF action = new EvaluateLTM();
    String ltm = "[%new% : %team%  = \"value\"]";

    // build params
    Map actionmap = new HashMap();
    actionmap.put("team", makeParameters(null, "team2", newtopic.getObjectId()));

    WebEdRequestIF request = new WebEdRequest(null, actionmap, null, null);
    ActionParametersIF params = makeParameters(makeList(tm, ltm), "mama", request);
    ActionResponseIF response = makeResponse();
      
    // execute    
    action.perform(params, response);
      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    assertFalse("id of topic not recorded in response parameters", id == null);
      
    TopicIF topic = (TopicIF) tm.getObjectById(id);
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", topic.getTopicMap() != tm);
    assertFalse("created topic hasn't basename", topic.getTopicNames().isEmpty());
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));      
  }  
}
