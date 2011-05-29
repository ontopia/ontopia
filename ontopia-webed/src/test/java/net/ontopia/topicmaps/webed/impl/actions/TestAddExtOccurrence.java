
package net.ontopia.topicmaps.webed.impl.actions;

import java.io.IOException;
import java.util.*;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.occurrence.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

public class TestAddExtOccurrence extends AbstractWebedTestCase {
  
  public TestAddExtOccurrence(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Good, Normal use, with scope
    
  */
  
  public void testNormalOperation() throws IOException {
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = makeTopic(tm, "snus");
    int numO = topic.getOccurrences().size();

    //make action
    ActionIF action = new AddExtOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("New occurrence not added", topic.getOccurrences().size() == numO);
    Iterator occIT = topic.getOccurrences().iterator();
    boolean hasit = false;
    while (occIT.hasNext()){
      OccurrenceIF occ = (OccurrenceIF) occIT.next();
            
      if (occ.getLocator().getAddress().equals("http://www.sf.net/"))
	hasit = true;
    }
    assertFalse("The Occurrence is not correct", !(hasit));
    
  }
  
  public void testNormalOperation2() throws IOException {
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = makeTopic(tm, "snus");
    int numO = topic.getOccurrences().size();

    //make action
    ActionIF action = new AddExtOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("New occurrence not added", topic.getOccurrences().size() == numO);
    Iterator occIT = topic.getOccurrences().iterator();
    boolean hasit = false;
    while (occIT.hasNext()){
      OccurrenceIF occ = (OccurrenceIF) occIT.next();
      if ((occ.getLocator().getAddress().equals("http://www.sf.net/")) && 
	  (occ.getType() == type))
	hasit = true;
      
    }
    assertFalse("The Occurrence is set correctly", !(hasit));
  }

    public void testNormalOperation3() throws IOException {
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = makeTopic(tm, "snus");
    TopicIF scope = makeTopic(tm, "general");
    int numO = topic.getOccurrences().size();

    //make action
    ActionIF action = new AddExtOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type, scope), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    assertFalse("New occurrence not added", topic.getOccurrences().size() == numO);
    Iterator occIT = topic.getOccurrences().iterator();
    boolean hasit = false;
    while (occIT.hasNext()){
      OccurrenceIF occ = (OccurrenceIF) occIT.next();
      if ((occ.getLocator().getAddress().equals("http://www.sf.net/")) && 
	  (occ.getType() == type) &&
	  (occ.getScope().iterator().next() == scope))
	hasit = true;
       
    }
    assertFalse("The Occurrence is set correctly", !(hasit));
  }

  public void testBadEmptyParams() throws IOException {    
    //make action
    ActionIF action = new AddExtOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }
 
  public void testBadUrl() throws IOException {    
    TopicIF topic = getTopicById(tm, "gamst");

    //make action
    ActionIF action = new AddExtOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "general");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params, malformed url");
    }catch (ActionRuntimeException e){
    }
  }
  
  public void testBadParams1() throws IOException {    
    //make action
    ActionIF action = new AddExtOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList("topic"), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }
 
  public void testBadParams2() throws IOException {    
    TopicIF topic = getTopicById(tm, "gamst");

    //make action
    ActionIF action = new AddExtOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, "type"), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }

  public void testBadParams3() throws IOException {    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF type = getTopicById(tm, "team");

    //make action
    ActionIF action = new AddExtOccurrence();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, type, "scope"), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params");
    }catch (ActionRuntimeException e){
    }
  }


}
