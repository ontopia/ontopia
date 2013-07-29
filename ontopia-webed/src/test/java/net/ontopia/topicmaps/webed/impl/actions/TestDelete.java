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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.Delete;

public class TestDelete extends AbstractWebedTestCase {
  
  public TestDelete(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Bad , Double topic delete
    3. Bad , No good topicId
  */


  public void testNormalOperation() throws java.io.IOException{
    try{
      
      ActionIF action = new Delete();
      TopicIF topic = makeTopic(tm, "snus");
      
      //build parms
      ActionParametersIF params = makeParameters(makeList(topic));
      ActionResponseIF response = makeResponse();
      
      //execute    
      action.perform(params, response);
      
      //test
      assertFalse("topic still exists", tm.getTopics().contains(topic));
      
    }catch (ActionRuntimeException e){
      fail("Good Topic, should work");
    } 
  }

  public void testDoubleDelete() throws java.io.IOException{
    ActionIF action = new Delete();
    TopicIF topic = makeTopic(tm, "snus");
      
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic));
    ActionResponseIF response = makeResponse();
      
    //execute    
    action.perform(params, response);
     
    ActionIF action1 = new Delete();
      
    //build parms
    ActionParametersIF params1 = makeParameters(makeList(topic));
    ActionResponseIF response1 = makeResponse();
      
    //execute    
    action1.perform(params, response);
  }

  public void testWrongTopicParam() throws java.io.IOException{
    try{
      
      ActionIF action = new Delete();
      
      //build parms
      ActionParametersIF params = makeParameters(makeList("baluba"));
      ActionResponseIF response = makeResponse();
      
      //execute    
      action.perform(params, response);
      
      //test
      fail("Bad topic, shouldn't work");
    }catch (ActionRuntimeException e){
      
    } 
  }  

}
