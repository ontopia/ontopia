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
import net.ontopia.topicmaps.webed.impl.actions.tmobject.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

public class TestRemoveSourceLocator extends AbstractWebedTestCase {
  
  public TestRemoveSourceLocator(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Bad , No good topicId
  */

  
  public void testNormalOperation() throws java.io.IOException{
    ActionIF action = new RemoveSourceLocator();
    TopicIF topic = getTopicById(tm, "super");
    LocatorIF SL = (LocatorIF) topic.getItemIdentifiers().iterator().next();
    int prevSLNum = topic.getItemIdentifiers().size();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, SL));
    ActionResponseIF response = makeResponse();
  
    //execute    
    try{
      action.perform(params, response);
      int newSLNum = topic.getItemIdentifiers().size();  
      assertFalse("The sourcelocator was not removed" , prevSLNum == newSLNum );
    }catch (ActionRuntimeException e){      
    }
  }
  
  public void testNormalOperationUsingStringParameter() throws java.io.IOException{
    ActionIF action = new RemoveSourceLocator();
    TopicIF topic = getTopicById(tm, "super");
    LocatorIF SL = (LocatorIF) topic.getItemIdentifiers().iterator().next();
    int prevSLNum = topic.getItemIdentifiers().size();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, SL.getAddress()));
    ActionResponseIF response = makeResponse();
  
    //execute    
    try{
      action.perform(params, response);
      int newSLNum = topic.getItemIdentifiers().size();  
      assertFalse("The sourcelocator was not removed" , prevSLNum == newSLNum );
    }catch (ActionRuntimeException e){      
    }
  }
  
  public void testBadLocator() throws java.io.IOException{
    ActionIF action = new RemoveSourceLocator();
    TopicIF topic = getTopicById(tm, "super");
    String SL = "super";
        
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, SL));
    ActionResponseIF response = makeResponse();
  
    //execute    
    try{
      action.perform(params, response);
      fail("String given instead of LocatorIF, should fail");
    }catch (ActionRuntimeException e){
      
    }
  }

  public void testBadTopic() throws java.io.IOException{
    ActionIF action = new RemoveSourceLocator();
    TopicIF topic = getTopicById(tm, "super");
    LocatorIF SL = (LocatorIF) topic.getItemIdentifiers().iterator().next();
        
    //build parms
    ActionParametersIF params = makeParameters(makeList("topic", SL));
    ActionResponseIF response = makeResponse();
  
    //execute    
    try{
      action.perform(params, response);
      fail("String given instead of TopicIF, should fail");
    }catch (ActionRuntimeException e){
      
    }
  }


}
