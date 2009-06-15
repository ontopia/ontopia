  
//$Id: TestAddType.java,v 1.2 2004/10/06 20:29:55 larsga Exp $

package net.ontopia.topicmaps.webed.impl.actions.test;

import java.util.*;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.topic.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.infoset.core.*;

import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;

public class TestAddType extends AbstractWebedTestCase {
  
  public TestAddType(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use, with player
    2. Good, Normal use, player as req.
  */

  
  public void testNormalOperation() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    int numTypes = topic.getTypes().size();

    TopicIF newTopic = makeTopic(tm, "snus");
    String newTopicId = newTopic.getObjectId();
    //make action
    ActionIF action = new AddType();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, newTopicId);
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test  
    assertFalse("Type not set...", numTypes == topic.getTypes().size());
    //assertFalse();
  }

  public void testBadNewTopicType() throws java.io.IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    int numTypes = topic.getTypes().size();

    //make action
    ActionIF action = new AddType();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();
    try {
      //execute    
      action.perform(params, response);
    
      //test  
      assertFalse("Type set...", numTypes != topic.getTypes().size());
      fail("bad newtopic type");
      //assertFalse();
    } catch (ActionRuntimeException e) {
    }
  }
  
  public void testNoParams() throws java.io.IOException{
    //make action
    ActionIF action = new AddType();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
    
      //test  
      fail("Empty params");
      //assertFalse();
    }catch (ActionRuntimeException e){
    }
  }

  
}
