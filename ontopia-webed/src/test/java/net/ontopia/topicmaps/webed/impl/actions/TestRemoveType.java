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
import java.io.IOException;
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

public class TestRemoveType extends AbstractWebedTestCase {
  
  public TestRemoveType(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Bad , No good basename param
    4. Double remove of type
  */
  
  public void testNormalOperation() throws IOException {
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF topic = builder.makeTopic();
    TopicIF type = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, type, "");
            
    //make action
    ActionIF action = new RemoveType();
            
    //build parms
    ActionParametersIF params = makeParameters(makeList(bn));
    ActionResponseIF response = makeResponse();
      
    //execute    
    action.perform(params, response);

    //test      
    TopicIF deftype = tm.getTopicBySubjectIdentifier(PSI.getSAMNameType());
    assertFalse("Type is not default name type", bn.getType() != deftype);
  }
  
  public void testWrongObjectParam() throws java.io.IOException{
    try{
      
      //make action
      ActionIF action = new RemoveType();
            
      //build parms
      ActionParametersIF params = makeParameters(makeList("snus"));
      ActionResponseIF response = makeResponse();
      
      //execute    
      action.perform(params, response);
      //test      
      fail("Bad param shouldn't work");
    }catch (ActionRuntimeException e){
      
    }

  }

  public void testMultipleDelete() throws java.io.IOException{
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF topic = builder.makeTopic();
    TopicIF type = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, type, "");
            
    //make action
    ActionIF action = new RemoveType();
            
    //build parms
    ActionParametersIF params = makeParameters(makeList(bn));
    ActionResponseIF response = makeResponse();
      
    //execute    
    action.perform(params, response);
    action.perform(params, response);
    action.perform(params, response);
    action.perform(params, response);

    //test      
    TopicIF deftype = tm.getTopicBySubjectIdentifier(PSI.getSAMNameType());
    assertFalse("Type is not default name type", bn.getType() != deftype);
  }
}
