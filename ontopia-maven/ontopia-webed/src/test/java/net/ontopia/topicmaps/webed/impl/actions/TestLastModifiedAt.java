
// $Id: TestLastModifiedAt.java,v 1.3 2008/06/13 08:17:57 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions;

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
import net.ontopia.infoset.impl.basic.URILocator;

public class TestLastModifiedAt extends AbstractWebedTestCase {
  
  public TestLastModifiedAt(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Good, Normal use, with scope
    
  */
  
  public void testNormalOperation() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicMapIF topicmap = topic.getTopicMap();
    int numO = topic.getOccurrences().size();

    //make action
    ActionIF action = new LastModifiedAt();
    
    //build parms
    ActionParametersIF params = makeParameters(topic, "");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test          
    assertFalse("New occurrence not added", topic.getOccurrences().size() == numO);
    
    Iterator occIT = topic.getOccurrences().iterator();
    boolean hasit = false;
    while (occIT.hasNext()){
      OccurrenceIF occ = (OccurrenceIF) occIT.next();   
      LocatorIF loc = (LocatorIF) occ.getType().getSubjectIdentifiers().iterator().next();
      if (loc.getAddress().equals("http://psi.ontopia.net/xtm/occurrence-type/last-modified-at"))
	hasit = true;
    }
    assertFalse("The Occurrence is not correct", !(hasit));
    
  }
  
  public void testNoParam() throws java.io.IOException{
        
    //make action
    ActionIF action = new LastModifiedAt();
    
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
