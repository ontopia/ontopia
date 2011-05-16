   
//$Id: TestSetSubjectIndicator.java,v 1.5 2009/04/27 11:08:58 lars.garshol Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.io.IOException;
import java.util.Collections;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.topic.SetSubjectIndicator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.infoset.core.LocatorIF;

public class TestSetSubjectIndicator extends AbstractWebedTestCase {
  protected ActionIF action; // see TestSetSubjectIndicator2...
  
  public TestSetSubjectIndicator(String name) {
    super(name);
  }

  public void setUp() {
    super.setUp();
    action = new SetSubjectIndicator();
  }
  
  public void testNormalOperation() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    int numSLs = topic.getSubjectIdentifiers().size();
    LocatorIF newSL = new URILocator("http://www.slashdot.org");
    
    //build parms
    ActionParametersIF params = makeParameters(makeList
            (topic, newSL), "http://www.freshmeat.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response); 
    //test    
    assertFalse("Topic not added", numSLs > topic.getSubjectIdentifiers()
            .size());
    LocatorIF loc = (LocatorIF) topic.getSubjectIdentifiers().iterator().next();
    assertFalse("SI not set to new value", !(loc.getAddress()
            .equals("http://www.freshmeat.net/")));
  }
  
  public void testNoParams() throws IOException {        
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST);
    ActionResponseIF response = makeResponse();
    
    try{
      //execute    
      action.perform(params, response);
      fail("No params (EMPTY_LIST)");
    }catch (ActionRuntimeException e){
      
    }
  }
  
  public void testWrongParams() throws IOException {        
    //build parms
    ActionParametersIF params = makeParameters(makeList(""));
    ActionResponseIF response = makeResponse();
    
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params (String for Topic)");
    }catch (ActionRuntimeException e){
      
    }
  }
  
  public void testWrongParams2() throws IOException {        
    LocatorIF newSL = new URILocator("http://www.slashdot.org");
    
    //build parms
    ActionParametersIF params = makeParameters(makeList("", newSL));
    ActionResponseIF response = makeResponse();
    
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params (String for Topic, good SI)");
    } catch (ActionRuntimeException e) {
    }
  }

  public void testWrongParams3() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(topic, ""));
    ActionResponseIF response = makeResponse();
    
    try{
      //execute    
      action.perform(params, response);
      fail("Bad params (String for SI)");
    } catch (ActionRuntimeException e) {      
    }
  }
  
  public void testWrongURL() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    LocatorIF newSL = new URILocator("http://www.slashdot.org");
    
    //build parms
    ActionParametersIF params = makeParameters(makeList
            (topic, newSL), "snurrepip");
    ActionResponseIF response = makeResponse();
    
    try{
      //execute    
      action.perform(params, response);
      fail("Bad url i req");
    } catch (ActionRuntimeException e) {
    }
  }

  public void testSameURL() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    int numSLs = topic.getSubjectIdentifiers().size();
    LocatorIF newSL = new URILocator("http://www.slashdot.org");
    
    //build parms
    ActionParametersIF params = makeParameters(makeList
            (topic, newSL), "http://www.slashdot.org");
    ActionResponseIF response = makeResponse();
    
    action.perform(params, response);
    assertFalse("added another SA with same URL", 
            numSLs < topic.getSubjectIdentifiers().size());
  }
  
  public void testNormalOperationUsingStringParameter() 
          throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    int numSLs = topic.getSubjectIdentifiers().size();
    String newSL = "http://www.slashdot.org";
    
    //build parms
    ActionParametersIF params = makeParameters(makeList
            (topic, newSL), "http://www.freshmeat.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response); 
    //test    
    assertFalse("Topic not added", numSLs > topic.getSubjectIdentifiers()
            .size());
    LocatorIF loc = (LocatorIF) topic.getSubjectIdentifiers().iterator().next();
    assertFalse("SI not set to new value", !(loc.getAddress()
            .equals("http://www.freshmeat.net/")));
  }

  public void testEmptyURL() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    LocatorIF newSL = new URILocator("http://www.slashdot.org");
    topic.addSubjectIdentifier(newSL);
    int sisbefore = topic.getSubjectIdentifiers().size();
    
    // execute
    try {
      ActionParametersIF params =
        makeParameters(makeList(topic, newSL), "");
      ActionResponseIF response = makeResponse();
      action.perform(params, response);
      fail("Invalid URI was accepted as subject indicator");
    } catch (ActionRuntimeException e) {
      // "" is not a valid URI
    }
    
    // test
    int sisnow = topic.getSubjectIdentifiers().size();
    assertTrue("Number of subject indicators changed from " + sisbefore +
               " to " + sisnow,
               sisbefore == sisnow);
  }
}
