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

import java.util.Iterator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.RemoveTheme;

public class TestRemoveTheme extends AbstractWebedTestCase {
  
  public TestRemoveTheme(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Bad , No good TopicName param
    3. Bad , No good scope type
    4. Double remove of scope
  */
  public void testNormalOperation() throws java.io.IOException{
    
    //make action
    ActionIF action = new RemoveTheme();

    TopicIF topicScope = getTopicById(tm, "nickname");
    String topicScopeId = topicScope.getObjectId();
    TopicIF topic = getTopicById(tm, "gamst");
      
    //find a basename that has scope.
    Iterator<TopicNameIF> basenames = topic.getTopicNames().iterator();
    TopicNameIF base;
      
    do{
      base = basenames.next();
      if (!(base.getScope().isEmpty()) ){
        break;
      }
    }while(basenames.hasNext());
            
    //build parms
    ActionParametersIF params = makeParameters(base, topicScopeId);
    ActionResponseIF response = makeResponse();
      
    //execute    
    try{
    action.perform(params, response);
    assertFalse("It still has scope, the bastard.", 
                !base.getScope().isEmpty());
    }catch (ActionRuntimeException e){
    }
  }

  public void testWrongBasename() throws java.io.IOException{

    //make action
    ActionIF action = new RemoveTheme();

    TopicIF topicScope = getTopicById(tm, "nickname");
    String topicScopeId = topicScope.getObjectId();
                
    //build parms
    ActionParametersIF params = makeParameters("base", topicScopeId);
    ActionResponseIF response = makeResponse();
      
    //execute    
    try{
    action.perform(params, response);
    fail("managed to remove scope from wrong topic");
    }catch (ActionRuntimeException e){
    }
  }
  
  public void testBadType() throws java.io.IOException{
    
    //make action
    ActionIF action = new RemoveTheme();

    TopicIF topic = getTopicById(tm, "gamst");
      
    //find a basename that has scope.
    Iterator<TopicNameIF> basenames = topic.getTopicNames().iterator();
    TopicNameIF base;
      
    do{
      base = basenames.next();
      if (!(base.getScope().isEmpty()) ){
        break;
      }
    }while(basenames.hasNext());
            
    //build parms
    ActionParametersIF params = makeParameters(base, "humbug");
    ActionResponseIF response = makeResponse();
      
    //execute    
    
    try{
      action.perform(params, response);
      fail("managed to remove scope with wrong scopetypeID");
    }catch (ActionRuntimeException e){
    }
    
  }
  
  public void testDoubleDelete() throws java.io.IOException{
    
    //make action
    ActionIF action = new RemoveTheme();

    TopicIF topicScope = getTopicById(tm, "nickname");
    String topicScopeId = topicScope.getObjectId();
    TopicIF topic = getTopicById(tm, "gamst");
      
    //find a basename that has scope.
    Iterator<TopicNameIF> basenames = topic.getTopicNames().iterator();
    TopicNameIF base;
      
    do{
      base = basenames.next();
      if (!(base.getScope().isEmpty()))
        break;
    } while (basenames.hasNext());
            
    //build parms
    ActionParametersIF params = makeParameters(base, topicScopeId);
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    action.perform(params, response);
    action.perform(params, response);
    action.perform(params, response);
    action.perform(params, response);
    action.perform(params, response);
    
    assertFalse("It still has scope, the bastard.", 
                !base.getScope().isEmpty());
  }

  
}
