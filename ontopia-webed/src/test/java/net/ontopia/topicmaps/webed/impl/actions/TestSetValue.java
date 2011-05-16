
// $Id: TestSetValue.java,v 1.7 2008/06/12 14:37:26 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.util.*;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.basename.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

public class TestSetValue extends AbstractWebedTestCase {
  
  public TestSetValue(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Good, Normal use, with scope
    
  */
  
  public void testNormalOperation() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF basename = (TopicNameIF) topic.getTopicNames().iterator().next();
    int numBN = topic.getTopicNames().size();

    //make action
    ActionIF action = new SetValue();
    
    //build parms
    ActionParametersIF params = makeParameters(basename, "general");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("New basename added", topic.getTopicNames().size() > numBN);
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
    TopicIF type = makeTopic(tm, "bntype");
    TopicIF scope = getTopicById(tm, "gamst");
    int numBN = topic.getTopicNames().size();
    
    // make action
    ActionIF action = new SetValue();
    
    // build params
    List plist = new ArrayList();
    plist.add(Collections.EMPTY_SET);
    plist.add(Collections.singleton(topic));
    plist.add(Collections.singleton(scope));
    plist.add(Collections.singleton(type));
    ActionParametersIF params = makeParameters(plist, "general");
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
	hastype = (base.getType() == type);
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
  
  public void testNormalOperation3() {
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF basename = (TopicNameIF) topic.getTopicNames().iterator().next();
    int numBN = topic.getTopicNames().size();

    //make action
    ActionIF action = new SetValue();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(basename), "");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    int newNumBN = topic.getTopicNames().size();
    assertFalse("New basename added", newNumBN > numBN);
    assertFalse("the basename is not deleted", numBN == newNumBN);
    
    
  }


  public void testBadEmptyParams() throws java.io.IOException{    
    //make action
    ActionIF action = new SetValue();
    
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
    ActionIF action = new SetValue();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params, Basename string empty");
    }catch (ActionRuntimeException e){
    }
  }
  
  public void testBadParams2() throws java.io.IOException{    
    TopicIF topic = makeTopic(tm, "snus");
    int numBN = topic.getTopicNames().size();
    
    //make action
    ActionIF action = new SetValue();
    
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
