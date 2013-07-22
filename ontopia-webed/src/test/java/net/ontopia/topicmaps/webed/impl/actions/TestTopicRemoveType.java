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

import java.util.*;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.topic.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;

public class TestTopicRemoveType extends AbstractWebedTestCase {
  
  public TestTopicRemoveType(String name) {
    super(name);
  }

  
  public void testNormalOperation() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = (TopicIF) topic.getTypes().iterator().next();
    int numSLs = topic.getTypes().size();
   
    //make action
    ActionIF action = new RemoveType();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type));
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test    
    assertFalse("Topic not removed", numSLs == topic.getTypes().size());
    
  }
  
  public void testBadType() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = makeTopic(tm, "snus");

    int numSLs = topic.getTypes().size();
   
    //make action
    ActionIF action = new RemoveType();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type));
    ActionResponseIF response = makeResponse();
    
    try{
      //execute    
      action.perform(params, response);
      assertFalse("Removed a type which the topic didn't have", 
		  numSLs != topic.getTypes().size());
    }catch (ActionRuntimeException e){}
  }

  public void testBadTopicParam() throws java.io.IOException{
    TopicIF type = makeTopic(tm, "snus");
    
    //make action
    ActionIF action = new RemoveType();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList("topic", type));
    ActionResponseIF response = makeResponse();
    
    try{
      //execute    
      action.perform(params, response);
      fail("Bad param for topic (String)");
    }catch (ActionRuntimeException e){}
  }
  
 
   public void testEmptyParams() throws java.io.IOException{
    //make action
    ActionIF action = new RemoveType();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    
    try{
      //execute    
      action.perform(params, response);
      fail("Removed a type with empty params");
    }catch (ActionRuntimeException e){}
  }
 
   
}
  
