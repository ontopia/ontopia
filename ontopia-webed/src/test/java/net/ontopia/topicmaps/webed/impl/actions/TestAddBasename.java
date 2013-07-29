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

// $Id $

package net.ontopia.topicmaps.webed.impl.actions;

import java.util.Collections;
import java.util.Iterator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.basename.AddBasename;

public class TestAddBasename extends AbstractWebedTestCase {
  
  public TestAddBasename(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Good, Normal use, with scope
    
  */
  
  public void testNormalOperation() throws java.io.IOException{
    
    TopicIF topic = makeTopic(tm, "snus");
    int numBN = topic.getTopicNames().size();
    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "general");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("No new basename added", topic.getTopicNames().size() == numBN);
    Iterator baseIT = topic.getTopicNames().iterator();
    boolean hasit = false;
    while (baseIT.hasNext()){
      TopicNameIF base = (TopicNameIF) baseIT.next();
      if (base.getValue().equals("general"))
	hasit = true;
    }
    
    assertFalse("The basename is not correct", !(hasit));
    
  }

  public void testNormalOperation2() throws java.io.IOException{
    
    TopicIF topic = makeTopic(tm, "snus");
    TopicIF bntype = makeTopic(tm, "bntype");
    TopicIF scope = getTopicById(tm, "gamst");
    int numBN = topic.getTopicNames().size();
    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, scope, bntype), "general");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("No new basename added", topic.getTopicNames().size() == numBN);
    
    Iterator baseIT = topic.getTopicNames().iterator();
    boolean hasit = false;
    boolean hastype = false;
    boolean hascope = false;

    while (baseIT.hasNext()){
      TopicNameIF base = (TopicNameIF) baseIT.next();
      if (base.getValue().equals("general")){
	hasit = true;
	hastype = (base.getType() == bntype);
	Iterator ix = base.getScope().iterator();
	while (ix.hasNext()){
	  if (ix.next() == scope)
	    hascope = true;
	}
      }
    }
    assertFalse("The basename is not correct", !(hasit));
    assertFalse("The type is not correct", !(hastype));
    assertFalse("The scope is not correct", !(hascope));
  }

  public void testBadEmptyParams() throws java.io.IOException{    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, "general");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }

  public void testBadParamsEmptyString() throws java.io.IOException{    
    TopicIF topic = makeTopic(tm, "snus");
    int numBN = topic.getTopicNames().size();
    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
    } catch (ActionRuntimeException e) {
    }

    // verify no new base names added
    assertFalse("New base name added", numBN != topic.getTopicNames().size());
  }
  
  public void testBadParams2() throws java.io.IOException{    
    TopicIF topic = makeTopic(tm, "snus");
    int numBN = topic.getTopicNames().size();
    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters("", "");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params, Basename string empty, topic String");
    }catch (ActionRuntimeException e){
    }
  }
  

}
