
//$Id: TestSetType.java,v 1.5 2008/06/12 14:37:26 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.util.*;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

public class TestSetType extends AbstractWebedTestCase {
  
  public TestSetType(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Bad, objectID
    3. Bad, wrong parametertypes.
  */


  public void testNormalOperation() throws java.io.IOException{
    AssociationIF assoc = (AssociationIF) tm.getAssociations().iterator().next();
    TopicIF topic = getTopicById(tm, "gamst");
    String topicId = topic.getObjectId();
    TopicIF currType = assoc.getType();
    
    assertFalse("has no type", currType == null);
    //make action
    ActionIF action = new SetType();
    
    //build parms
    ActionParametersIF params = makeParameters(assoc, topicId);
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    //test      
    TopicIF newType = assoc.getType();
    assertFalse("Type not changed", newType == currType);    
  }
  
  public void testBadParams() throws java.io.IOException{
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF topic = builder.makeTopic();
    TopicIF type = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, type, "");
        
    //make action
    ActionIF action = new SetType();
   
    //build parms
    ActionParametersIF params = makeParameters("", "");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      
      //test      
      fail("Bad paramtypes");
      
    }catch (ActionRuntimeException e){
      
    }
    
  }
}
