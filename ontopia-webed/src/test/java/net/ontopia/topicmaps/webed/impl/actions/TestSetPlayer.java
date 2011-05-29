
package net.ontopia.topicmaps.webed.impl.actions;

import java.util.*;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.assocrole.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;

public class TestSetPlayer extends AbstractWebedTestCase {
  
  public TestSetPlayer(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use, with player
    2. Good, Normal use, player as req.
  */

  


  public void testNormalOperation() throws java.io.IOException, InvalidQueryException{
    AssociationIF assoc = (AssociationIF) tm.getAssociations().iterator().next();
    AssociationRoleIF role = (AssociationRoleIF) assoc.getRoles().iterator().next();
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
    AssociationIF assoc = (AssociationIF) tm.getAssociations().iterator().next();
    AssociationRoleIF role = (AssociationRoleIF) assoc.getRoles().iterator().next();
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
    AssociationIF assoc = (AssociationIF) tm.getAssociations().iterator().next();
    AssociationRoleIF role = (AssociationRoleIF) assoc.getRoles().iterator().next();
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
    AssociationIF assoc = (AssociationIF) tm.getAssociations().iterator().next();
    AssociationRoleIF role = (AssociationRoleIF) assoc.getRoles().iterator().next();
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
