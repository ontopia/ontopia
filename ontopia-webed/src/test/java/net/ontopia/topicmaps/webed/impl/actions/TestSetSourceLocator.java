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

public class TestSetSourceLocator extends AbstractWebedTestCase {
  
  public TestSetSourceLocator(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Good , No previous SL
    3. Bad parameters
    4. Bad url
  */

  public void testNormalOperation() throws java.io.IOException {
    ActionIF action = new SetSourceLocator();
    TopicIF topic = getTopicById(tm, "super");
    LocatorIF SL = (LocatorIF) topic.getItemIdentifiers().iterator().next();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, SL), "http://mama.no");
    ActionResponseIF response = makeResponse();
    try{
      action.perform(params, response);
      int newSLNum = topic.getItemIdentifiers().size();  
      LocatorIF SLnew = (LocatorIF) topic.getItemIdentifiers().iterator().next();
      
      assertFalse("new address not set correctly", 
		  !(SLnew.getAddress().equals("http://mama.no/")));      
    } catch (ActionRuntimeException e) {
    }
  }
  
  public void testSetNewSL() throws java.io.IOException {
    // setting sourcelocator on topic without predefined SL
    ActionIF action = new SetSourceLocator();
    TopicIF topic = getTopicById(tm, "gamst");
    
    TopicIF topicDummy = getTopicById(tm, "super");
    LocatorIF SL = (LocatorIF) topic.getItemIdentifiers().iterator().next();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, SL), "http://mama.no");
    ActionResponseIF response = makeResponse();
    try {
      action.perform(params, response);
      int newSLNum = topic.getItemIdentifiers().size();  
      
      LocatorIF SLnew = (LocatorIF) topic.getItemIdentifiers().iterator().next();
      assertFalse("new address not set correctly", 
		  !(SLnew.getAddress().equals("http://mama.no/")));
    } catch (ActionRuntimeException e) {
    }
  }

  public void testNoParams() throws java.io.IOException{
    //setting sourcelocator on topic without predefined SL
    ActionIF action = new SetSourceLocator();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, "http://mama.no");
    ActionResponseIF response = makeResponse();
    try{
      action.perform(params, response);
      fail("Empty parameters..");      
      
    }catch (ActionRuntimeException e){
    }
  }
  
  public void testBadURL() throws java.io.IOException{
    
    ActionIF action = new SetSourceLocator();
    TopicIF topic = getTopicById(tm, "super");
    LocatorIF SL = (LocatorIF) topic.getItemIdentifiers().iterator().next();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, SL), "foobar");
    ActionResponseIF response = makeResponse();
    try{
      action.perform(params, response);
      fail("Bad url given");      
    }catch (ActionRuntimeException e){
      
    }
  }


}
