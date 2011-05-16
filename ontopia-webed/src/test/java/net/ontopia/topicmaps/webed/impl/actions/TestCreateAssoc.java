
// $Id: TestCreateAssoc.java,v 1.6 2008/05/23 09:24:24 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.util.*;

import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.topicmap.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
    
public class TestCreateAssoc extends AbstractWebedTestCase {

    public TestCreateAssoc(String name) {
	super(name);
    }


    // tests

    /*
      1. No parameters
      2. No topicmap input, StringType all params
      3. Empty string as type
      4. 
    */

    
    //good cases
  public void testTM() throws java.io.IOException, ActionRuntimeException {
    //Good TM.
    
    //make action
    ActionIF action = new CreateAssoc();
    //build parms
    ActionParametersIF params = makeParameters(makeList(tm));
    ActionResponseIF response = makeResponse();
    //execute
    
    action.perform(params, response);      

    // verify that an association was created correctly
    String id = response.getParameter(Constants.RP_ASSOC_ID);
    assertFalse("id of association not recorded in response parameters", id == null);
      
    AssociationIF assoc = (AssociationIF) tm.getObjectById(id);
    assertFalse("created association not found", assoc == null);
    assertFalse("created association in wrong TM", assoc.getTopicMap() != tm);
    assertFalse("created association has scope", !(assoc.getScope().isEmpty()) );
    assertFalse("created association has roles", !(assoc.getRoles().isEmpty()) );
  }
  
  public void testTMGoodPlayer() throws java.io.IOException, ActionRuntimeException {
    //Good TM good topic.
    
    TopicIF topicDummy = makeTopic(tm, "snus");
    //make action
    ActionIF action = new CreateAssoc();
    //build parms
    ActionParametersIF params = makeParameters(makeList(tm, topicDummy));
    ActionResponseIF response = makeResponse();
    //execute
    
    action.perform(params, response);
      
    // verify that an association was created correctly
    String id = response.getParameter(Constants.RP_ASSOC_ID);
    assertFalse("id of association not recorded in response parameters", id == null);
      
    AssociationIF assoc = (AssociationIF) tm.getObjectById(id);
    assertFalse("created association not found", assoc == null);
    assertFalse("created association in wrong TM", assoc.getTopicMap() != tm);
      
    assertFalse("created association has no type, it should have", 
                assoc.getType() == null);
    assertFalse("created association has scope", !(assoc.getScope().isEmpty()) );
    assertFalse("created association has roles", !(assoc.getRoles().isEmpty()) );
  }
  
  //bad cases
  
  public void testNoParameters() throws ActionRuntimeException {
    //make action
    ActionIF assoc = new CreateAssoc();
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    //execute
    
    try {
      assoc.perform(params, response);      
      fail("Made assoc without TM, or any other parameters");
    } catch (ActionRuntimeException e) {
    }
    
  }
  
  public void testNoTopicmap() throws ActionRuntimeException {
    //make action
    ActionIF assoc = new CreateAssoc();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList("topicmap", "type", "mama"));
    ActionResponseIF response = makeResponse();
    
    try {
      assoc.perform(params, response);      
      fail("Made assoc with TM as string, and other strings");
    } catch (ActionRuntimeException e) {
      
    }
  }
  
  
  public void testTMNullTopicAssoc() throws java.io.IOException{
    //Good TM but no excisiting topic.
    
    //make action
    ActionIF assoc = new CreateAssoc();
    //build parms
    ActionParametersIF params = makeParameters(makeList(tm, ""));
    ActionResponseIF response = makeResponse();
    //execute
    
    try {
      assoc.perform(params, response);      
      fail("Made assoc with TM, but empty string as topic assosiationtype");
    } catch (ActionRuntimeException e) {
      
    }
    
  }
  
  
}
