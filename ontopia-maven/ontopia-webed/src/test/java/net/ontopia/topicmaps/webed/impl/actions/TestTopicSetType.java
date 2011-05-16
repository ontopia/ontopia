
// $Id: TestTopicSetType.java,v 1.2 2004/10/05 18:08:02 larsga Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.util.*;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.topic.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

public class TestTopicSetType extends AbstractWebedTestCase {
  
  public TestTopicSetType(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
   
  */
  
  public void testNormalOperation() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = (TopicIF) topic.getTypes().iterator().next();
    TopicIF newtyp = makeTopic(tm, "snus");
    int numSLs = topic.getTypes().size();
    
    //make action
    ActionIF action = new SetType();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, newtyp.getObjectId());
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("We still have same type", topic.getTypes().iterator().next() == type);
    assertFalse("The type is not correct", topic.getTypes().iterator().next() != newtyp);
    
  }

  public void testBadParam1() throws java.io.IOException{
    TopicIF newtyp = makeTopic(tm, "snus");
    
    //make action
    ActionIF action = new SetType();
    
    //build parms
    ActionParametersIF params = makeParameters("topic", newtyp.getObjectId());
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
      fail("topic is String-type");
    }catch (ActionRuntimeException e){
    }
  }

  public void testBadParam2() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    
    // make action
    ActionIF action = new SetType();
    
    // build parms
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();

    // execute    
    action.perform(params, response);

    // verify that the topic no longer has any types
    assertFalse("Topic still has a type", !topic.getTypes().isEmpty());
  }


  public void testNoParam() throws java.io.IOException{
    TopicIF newtyp = makeTopic(tm, "snus");
    
    //make action
    ActionIF action = new SetType();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test      
      fail("Collection is empty");
    }catch (ActionRuntimeException e){
    }
  }

}
