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
import net.ontopia.topicmaps.webed.impl.actions.occurrence.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

public class TestAddIntOccurrence extends AbstractWebedTestCase {
  
  public TestAddIntOccurrence(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Good, Normal use, with scope
    
  */
  
  public void testNormalOperation() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = makeTopic(tm, "snus");
    int numO = topic.getOccurrences().size();

    //make action
    ActionIF action = new AddIntOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("New occurrence not added", topic.getOccurrences().size() == numO);
    Iterator occIT = topic.getOccurrences().iterator();
    boolean hasit = false;
    while (occIT.hasNext()){
      OccurrenceIF occ = (OccurrenceIF) occIT.next();
            
      if (occ.getValue().equals("http://www.sf.net"))
	hasit = true;
    }
    assertFalse("The Occurrence is not correct", !(hasit));
    
  }
  
  public void testNormalOperation2() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = makeTopic(tm, "snus");
    int numO = topic.getOccurrences().size();

    //make action
    ActionIF action = new AddIntOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("New occurrence not added", topic.getOccurrences().size() == numO);
    Iterator occIT = topic.getOccurrences().iterator();
    boolean hasit = false;
    while (occIT.hasNext()){
      OccurrenceIF occ = (OccurrenceIF) occIT.next();
      if ((occ.getValue().equals("http://www.sf.net")) && 
	  (occ.getType() == type))
	hasit = true;
      
    }
    assertFalse("The Occurrence is set correctly", !(hasit));
  }

    public void testNormalOperation3() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = makeTopic(tm, "snus");
    TopicIF scope = makeTopic(tm, "general");
    int numO = topic.getOccurrences().size();

    //make action
    ActionIF action = new AddIntOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type, scope), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("New occurrence not added", topic.getOccurrences().size() == numO);
    Iterator occIT = topic.getOccurrences().iterator();
    boolean hasit = false;
    while (occIT.hasNext()){
      OccurrenceIF occ = (OccurrenceIF) occIT.next();
      if ((occ.getValue().equals("http://www.sf.net")) && 
	  (occ.getType() == type) &&
	  (occ.getScope().iterator().next() == scope))
	hasit = true;
       
    }
    assertFalse("The Occurrence is set correctly", !(hasit));
  }

  public void testBadEmptyParams() throws java.io.IOException{    
    //make action
    ActionIF action = new AddIntOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }
 
  public void testEmptyOccString() throws java.io.IOException{    
    TopicIF topic = getTopicById(tm, "gamst");
    int occcount = topic.getOccurrences().size();
    
    //make action
    ActionIF action = new AddIntOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
    } catch (ActionRuntimeException e) {
    }

    // verify that no new occurrence was created
    assertFalse("A new occurrence was created",
                occcount != topic.getOccurrences().size());
  }
  
  public void testBadParams1() throws java.io.IOException{    
    //make action
    ActionIF action = new AddIntOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList("topic"), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }
 
  public void testBadParams2() throws java.io.IOException{    
    TopicIF topic = getTopicById(tm, "gamst");

    //make action
    ActionIF action = new AddIntOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, "type"), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }

  public void testBadParams3() throws java.io.IOException{    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = getTopicById(tm, "team");

    //make action
    ActionIF action = new AddIntOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type, "scope"), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }


}
