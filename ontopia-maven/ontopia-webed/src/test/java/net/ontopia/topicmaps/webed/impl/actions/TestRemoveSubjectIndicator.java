  
//$Id: TestRemoveSubjectIndicator.java,v 1.5 2008/06/13 08:17:57 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.actions.topic.RemoveSubjectIndicator;
import net.ontopia.topicmaps.webed.impl.basic.Constants;

public class TestRemoveSubjectIndicator extends AbstractWebedTestCase {
  
  public TestRemoveSubjectIndicator(String name) {
    super(name);
  }

  
  public void testNormalOperation() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    
    LocatorIF newSL = new URILocator("http://www.slashdot.org");
    topic.addSubjectIdentifier(newSL);
    int numSLs = topic.getSubjectIdentifiers().size();
   
    //make action
    ActionIF action = new RemoveSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, newSL));
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test    
    assertFalse("SubjectIndicator not removed", 
            numSLs == topic.getSubjectIdentifiers().size());
    
  }

  public void testNormalOperationUsingStringParameter() 
          throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    
    LocatorIF newSL = new URILocator("http://www.slashdot.org");
    topic.addSubjectIdentifier(newSL);
    int numSLs = topic.getSubjectIdentifiers().size();
   
    //make action
    ActionIF action = new RemoveSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, newSL.getAddress()));
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test    
    assertFalse("SubjectIndicator not removed", numSLs == topic.getSubjectIdentifiers().size());
    
  }

  public void testNoparams() throws java.io.IOException{   
    //make action
    ActionIF action = new RemoveSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    
    try{
      //execute    
      action.perform(params, response);
      fail("Empty list params");       
    }catch (ActionRuntimeException e){}
  }
   
  public void testBadLocatorParam() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    
    //make action
    ActionIF action = new RemoveSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, "newSL"));
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad locator param (string)");       
    }catch (ActionRuntimeException e){}    
  }
  
  public void testBadTopicParam() throws java.io.IOException{
    LocatorIF newSL = new net.ontopia.infoset.impl.basic.URILocator("http://www.slashdot.org");
    
    //make action
    ActionIF action = new RemoveSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList("topic", newSL));
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      fail("Bad locator param (string)");       
    }catch (ActionRuntimeException e){}
  }

  public void testDeleteNonLocator() throws java.io.IOException{
    TopicIF topic = getTopicById(tm, "gamst");
    
    LocatorIF newSL = new net.ontopia.infoset.impl.basic.URILocator("http://www.slashdot.org");
    
    int numSLs = topic.getSubjectIdentifiers().size();
   
    //make action
    ActionIF action = new RemoveSubjectIndicator();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, newSL));
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    try{
      //test    
      assertFalse("Lost a subjectIndicator that should not have been deleted", numSLs != topic.getSubjectIdentifiers().size());
    }catch (ActionRuntimeException e){}
  }
  

}
