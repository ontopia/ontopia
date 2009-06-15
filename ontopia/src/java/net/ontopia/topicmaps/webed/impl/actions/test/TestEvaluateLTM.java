
//$Id: TestEvaluateLTM.java,v 1.12 2008/06/12 14:37:26 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.test;

import java.io.IOException;
import java.util.*;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.infoset.core.*;

public class TestEvaluateLTM extends AbstractWebedTestCase {
  
  public TestEvaluateLTM(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Does %new% work?
    3. Does %value% work?
    4. Does %topic% work?
    3. Bad , No good topicId
  */


  public void testNormalOperation() throws IOException {
    ActionIF action = new EvaluateLTM();
    String ltm = "[%new% : team = \"snus\"]";
      
    // build parms
    ActionParametersIF params = makeParameters(makeList(tm, ltm), "mama");
    ActionResponseIF response = makeResponse();
      
    // execute    
    action.perform(params, response);
      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    assertFalse("id of topic not recorded in response parameters", id == null);
      
    TopicIF topic = (TopicIF) tm.getObjectById(id);
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", topic.getTopicMap() != tm);
    assertFalse("created topic hasn't basename", topic.getTopicNames().isEmpty());
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));      
  }

  public void testValue() throws IOException{
    ActionIF action = new EvaluateLTM();
    String ltm = "[%new% : team = \"%value%\"]";
      
    //build parms
    ActionParametersIF params = makeParameters(makeList(tm, ltm), "mama");
    ActionResponseIF response = makeResponse();
      
    //execute    
    action.perform(params, response);
      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    assertFalse("id of topic not recorded in response parameters", id == null);
      
    TopicIF topic = (TopicIF) tm.getObjectById(id);
      
    assertFalse("created topic hasn't basename", topic.getTopicNames().isEmpty());

    Iterator i = topic.getTopicNames().iterator();
    boolean basenameIsCorr = false;
      
    while (i.hasNext()){
      TopicNameIF name = (TopicNameIF) i.next();

      if (name.getValue().equals("mama")){
        basenameIsCorr = true;
      }
    } 
      
    assertFalse("basename is not correct", !(basenameIsCorr));
    assertFalse("More then one basename", topic.getTopicNames().size() != 1);
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", topic.getTopicMap() != tm);
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));      
  }


  public void testMoreValue() throws IOException {
    ActionIF action = new EvaluateLTM();
    String ltm = "[%new% : %team%  = \"%value%\"]";
      
    // build params
    // type becomes the topic "team", base name value is "mama"
    Map actionmap = new HashMap();
    actionmap.put("team", makeParameters(null, "team2",
                                         getTopicById(tm, "team").getObjectId()));
    WebEdRequestIF request = new WebEdRequest(null, actionmap, null, null);
    ActionParametersIF params = makeParameters(makeList(tm, ltm), "mama", request);
    ActionResponseIF response = makeResponse();
      
    //execute    
    action.perform(params, response);
      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    assertFalse("id of topic not recorded in response parameters", id == null);
      
    TopicIF topic = (TopicIF) tm.getObjectById(id);
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", topic.getTopicMap() != tm);
      
    //check if correct basename
    assertFalse("created topic has wrong number of base names",
                topic.getTopicNames().size() != 1);
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    assertFalse("basename is not correct", !bn.getValue().equals("mama"));
    assertFalse("basename scope is not empty", !bn.getScope().isEmpty());
      
    //check if correct type
    TopicIF corrType = getTopicById(tm, "team");
      
    boolean typeIsCorr = topic.getTypes().contains(corrType);

    assertFalse("type is not correct", !(typeIsCorr));
    assertFalse("More then one type", topic.getTypes().size() != 1);    
      
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));      
  }
  
  public void testIdForSrclocatorlessTopic() throws IOException {
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF newtopic = builder.makeTopic();
    
    ActionIF action = new EvaluateLTM();
    String ltm = "[%new% : %team%  = \"value\"]";

    // build params
    Map actionmap = new HashMap();
    actionmap.put("team", makeParameters(null, "team2", newtopic.getObjectId()));

    WebEdRequestIF request = new WebEdRequest(null, actionmap, null, null);
    ActionParametersIF params = makeParameters(makeList(tm, ltm), "mama", request);
    ActionResponseIF response = makeResponse();
      
    // execute    
    action.perform(params, response);
      
    String id = response.getParameter(Constants.RP_TOPIC_ID);
    assertFalse("id of topic not recorded in response parameters", id == null);
      
    TopicIF topic = (TopicIF) tm.getObjectById(id);
    assertFalse("created topic not found", topic == null);
    assertFalse("created topic in wrong TM", topic.getTopicMap() != tm);
    assertFalse("created topic hasn't basename", topic.getTopicNames().isEmpty());
    assertFalse("created topic has roles", !(topic.getRoles().isEmpty()));      
  }  
}
