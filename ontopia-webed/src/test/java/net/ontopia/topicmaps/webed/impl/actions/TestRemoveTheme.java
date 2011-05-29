
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
    Iterator basenames = topic.getTopicNames().iterator();
    TopicNameIF base;
      
    do{
      base = (TopicNameIF) basenames.next();
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
    Iterator basenames = topic.getTopicNames().iterator();
    TopicNameIF base;
      
    do{
      base = (TopicNameIF) basenames.next();
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
    Iterator basenames = topic.getTopicNames().iterator();
    TopicNameIF base;
      
    do{
      base = (TopicNameIF) basenames.next();
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
