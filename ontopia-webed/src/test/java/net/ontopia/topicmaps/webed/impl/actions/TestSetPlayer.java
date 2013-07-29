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

import java.util.Collections;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.assocrole.SetPlayer;

public class TestSetPlayer extends AbstractWebedTestCase {
  
  public TestSetPlayer(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use, with player
    2. Good, Normal use, player as req.
  */

  


  public void testNormalOperation() throws java.io.IOException, InvalidQueryException{
    AssociationIF assoc = tm.getAssociations().iterator().next();
    AssociationRoleIF role = assoc.getRoles().iterator().next();
    TopicIF topicRole = getTopicById(tm, "gamst");
    
    //make action
    ActionIF action = new SetPlayer();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(role, topicRole));
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test  
    assertFalse("New role not set", role.getPlayer() != topicRole);
  }
  
  public void testNormalOperation2() throws java.io.IOException, InvalidQueryException{
    AssociationIF assoc = tm.getAssociations().iterator().next();
    AssociationRoleIF role = assoc.getRoles().iterator().next();
    TopicIF topicRole = getTopicById(tm, "gamst");
    String topicRoleId = topicRole.getObjectId();
    //make action
    ActionIF action = new SetPlayer();
    
    //build parms
    ActionParametersIF params = makeParameters(role, topicRoleId);
    ActionResponseIF response = makeResponse();
    //execute    
    action.perform(params, response);
    
    //test  
    assertFalse("New role not set", role.getPlayer() != topicRole);
  }

 public void testBadRole() throws java.io.IOException, InvalidQueryException{
    
    TopicIF topicRole = getTopicById(tm, "gamst");
    String topicRoleId = topicRole.getObjectId();
    //make action
    ActionIF action = new SetPlayer();
    
    //build parms
    ActionParametersIF params = makeParameters("role", topicRoleId);
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test  
      fail("Bad AssocRole(String), should fail");
    }catch (ActionRuntimeException e){
    }
    }

  public void testBadPlayer() throws java.io.IOException, InvalidQueryException{
    AssociationIF assoc = tm.getAssociations().iterator().next();
    AssociationRoleIF role = assoc.getRoles().iterator().next();
    //make action
    ActionIF action = new SetPlayer();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(role, "topicRoleId"));
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test  
      fail("Bad Player (String), should fail");
    }catch (ActionRuntimeException e){
    }
    }
  
  public void testEmptyCollection() throws java.io.IOException, InvalidQueryException{
    
    //make action
    ActionIF action = new SetPlayer();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test  
      fail("Bad Player(String) in req, should fail");
    }catch (ActionRuntimeException e){
    }
  }

  public void testNoTopic() throws java.io.IOException, InvalidQueryException{
    AssociationIF assoc = tm.getAssociations().iterator().next();
    AssociationRoleIF role = assoc.getRoles().iterator().next();
    //make action
    ActionIF action = new SetPlayer();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(role));
    ActionResponseIF response = makeResponse();
    //execute    
    try {
      action.perform(params, response);
      //test  
      fail("Bad params no topicPlayer given, should fail");
    } catch (ActionRuntimeException e) {
    }
  }

  
}
