
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

public class TestTologDelete extends AbstractWebedTestCase {
  
  public TestTologDelete(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Bad, query
    3. Bad TMObj
  */


  public void testNormalOperation() throws java.io.IOException{
    
    //make action
    ActionIF action = new TologDelete();
    String query = "instance-of($TEAMS, team)?";
    String topicId = getTopicById(tm, "tromso").getObjectId();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(query), topicId);
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test      
    TopicIF topic = getTopicById(tm, "tromso");
    assertFalse("Map still has teams", topic != null);
  }
  
  public void testBadQuery() throws java.io.IOException{
    
    //make action
    ActionIF action = new TologDelete();
    String query = "instance-of($TEAMS, team)";
    String topicId = getTopicById(tm, "tromso").getObjectId();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(query), topicId);
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
            
    }catch (ActionRuntimeException e){
    }
  }
  
  public void testParams() throws java.io.IOException{
    
    //make action
    ActionIF action = new TologDelete();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList("mama"), "papa");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
            
    }catch (ActionRuntimeException e){
    }
  }
  

}
