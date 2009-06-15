
// $Id $

package net.ontopia.topicmaps.webed.impl.actions.test;

import java.util.*;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.basename.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.infoset.core.*;

public class TestAddBasename extends AbstractWebedTestCase {
  
  public TestAddBasename(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Good, Normal use, with scope
    
  */
  
  public void testNormalOperation() throws java.io.IOException{
    
    TopicIF topic = makeTopic(tm, "snus");
    int numBN = topic.getTopicNames().size();
    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "general");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("No new basename added", topic.getTopicNames().size() == numBN);
    Iterator baseIT = topic.getTopicNames().iterator();
    boolean hasit = false;
    while (baseIT.hasNext()){
      TopicNameIF base = (TopicNameIF) baseIT.next();
      if (base.getValue().equals("general"))
	hasit = true;
    }
    
    assertFalse("The basename is not correct", !(hasit));
    
  }

  public void testNormalOperation2() throws java.io.IOException{
    
    TopicIF topic = makeTopic(tm, "snus");
    TopicIF bntype = makeTopic(tm, "bntype");
    TopicIF scope = getTopicById(tm, "gamst");
    int numBN = topic.getTopicNames().size();
    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, scope, bntype), "general");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("No new basename added", topic.getTopicNames().size() == numBN);
    
    Iterator baseIT = topic.getTopicNames().iterator();
    boolean hasit = false;
    boolean hastype = false;
    boolean hascope = false;

    while (baseIT.hasNext()){
      TopicNameIF base = (TopicNameIF) baseIT.next();
      if (base.getValue().equals("general")){
	hasit = true;
	hastype = (base.getType() == bntype);
	Iterator ix = base.getScope().iterator();
	while (ix.hasNext()){
	  if (ix.next() == scope)
	    hascope = true;
	}
      }
    }
    assertFalse("The basename is not correct", !(hasit));
    assertFalse("The type is not correct", !(hastype));
    assertFalse("The scope is not correct", !(hascope));
  }

  public void testBadEmptyParams() throws java.io.IOException{    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, "general");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }

  public void testBadParamsEmptyString() throws java.io.IOException{    
    TopicIF topic = makeTopic(tm, "snus");
    int numBN = topic.getTopicNames().size();
    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
    } catch (ActionRuntimeException e) {
    }

    // verify no new base names added
    assertFalse("New base name added", numBN != topic.getTopicNames().size());
  }
  
  public void testBadParams2() throws java.io.IOException{    
    TopicIF topic = makeTopic(tm, "snus");
    int numBN = topic.getTopicNames().size();
    
    //make action
    ActionIF action = new AddBasename();
    
    //build parms
    ActionParametersIF params = makeParameters("", "");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params, Basename string empty, topic String");
    }catch (ActionRuntimeException e){
    }
  }
  

}
