  
//$Id: TestAddSubjectIndicator.java,v 1.3 2009/04/27 11:08:58 lars.garshol Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.io.IOException;
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

import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;

public class TestAddSubjectIndicator extends AbstractWebedTestCase {
  
  public TestAddSubjectIndicator(String name) {
    super(name);
  }
  
  /*
    1. Good, Normal use, with player
    2. Good, Normal use, player as req.
  */
  
  public void testNormalOperation() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    int numIndikators = topic.getSubjectIdentifiers().size();
    //make action
    ActionIF action = new AddSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "http://www.slashdot.org");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test  
    assertFalse("New subjectIndicator not set", topic.getSubjectIdentifiers().size() < numIndikators);
    LocatorIF loc = (LocatorIF) topic.getSubjectIdentifiers().iterator().next();
    assertFalse("New subjectIndicator not set correctly", !(loc.getAddress().equals("http://www.slashdot.org/")));
  }
  
  
  public void testBadUrl() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    int numIndikators = topic.getSubjectIdentifiers().size();
    //make action
    ActionIF action = new AddSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "snus");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test  
      fail("Malformed URL, should fail");
    } catch(ActionRuntimeException e) {
    }
  }

  public void testBadTopic() throws IOException {
    //make action
    ActionIF action = new AddSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters("topic", "http://www.slashdot.org");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test  
      fail("Bad param(String) for topic, should fail");
    } catch(ActionRuntimeException e) {
    }
  }
  
  public void testNoParams() throws IOException {
    //make action
    ActionIF action = new AddSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      //test  
      fail("Bad params(EMPTY_LIST), should fail");
    } catch(ActionRuntimeException e) {
    }
  }
}
