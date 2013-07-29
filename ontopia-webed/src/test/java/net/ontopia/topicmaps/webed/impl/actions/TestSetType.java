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

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.SetType;

public class TestSetType extends AbstractWebedTestCase {
  
  public TestSetType(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Bad, objectID
    3. Bad, wrong parametertypes.
  */


  public void testNormalOperation() throws java.io.IOException{
    AssociationIF assoc = (AssociationIF) tm.getAssociations().iterator().next();
    TopicIF topic = getTopicById(tm, "gamst");
    String topicId = topic.getObjectId();
    TopicIF currType = assoc.getType();
    
    assertFalse("has no type", currType == null);
    //make action
    ActionIF action = new SetType();
    
    //build parms
    ActionParametersIF params = makeParameters(assoc, topicId);
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    TopicIF newType = assoc.getType();
    assertFalse("Type not changed", newType == currType);    
  }
  
  public void testBadParams() throws java.io.IOException{
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF topic = builder.makeTopic();
    TopicIF type = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, type, "");
        
    //make action
    ActionIF action = new SetType();
   
    //build parms
    ActionParametersIF params = makeParameters("", "");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      
      //test      
      fail("Bad paramtypes");
      
    }catch (ActionRuntimeException e){
      
    }
    
  }
}
