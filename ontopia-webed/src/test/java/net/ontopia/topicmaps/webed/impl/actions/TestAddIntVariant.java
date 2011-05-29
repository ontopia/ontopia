
package net.ontopia.topicmaps.webed.impl.actions;

import java.util.*;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.variant.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;

public class TestAddIntVariant extends AbstractWebedTestCase {
  
  public TestAddIntVariant(String name) {
    super(name);
  }

  
  
  public void testNormalOperation() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    int bnNum = bn.getVariants().size();
    
    //make action
    ActionIF action = new AddIntVariant();
    
    //build parms
    ActionParametersIF params = makeParameters(bn, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test
    assertFalse("No new variant set", bnNum == bn.getVariants().size());
    
    VariantNameIF vNew = (VariantNameIF) bn.getVariants().iterator().next();
    assertFalse("The URL is not correct", 
		!(vNew.getValue().equals("http://www.sf.net")));
    
  }

    public void testNormalOperation2() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF scope = getTopicById(tm, "tromso");
    TopicNameIF bn = null;
    Iterator iter = topic.getTopicNames().iterator();
    while (iter.hasNext()) {
      bn = (TopicNameIF) iter.next();
      if (bn.getValue().equals("Morten Gamst Pedersen")) break;
    }
    int bnNum = bn.getVariants().size();
    
    //make action
    ActionIF action = new AddIntVariant();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(bn, scope), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test
    assertFalse("No new variant set", bnNum == bn.getVariants().size());
    
    VariantNameIF vNew = (VariantNameIF) bn.getVariants().iterator().next();
    assertFalse("The URL is not correct", 
                !(vNew.getValue().equals("http://www.sf.net")));
    assertFalse("The scope is not correct", 
                vNew.getScope().contains(scope) && vNew.getScope().size() == 2);
    
  }
  
  public void testEmptyParams() throws java.io.IOException{
    //make action
    ActionIF action = new AddIntVariant();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, 
					       "http://snus.org");
    ActionResponseIF response = makeResponse();
    
    //execute    
    try{
      action.perform(params, response);
      fail("Empty Collection as params");
    }catch (ActionRuntimeException e){}
        
  }
  
  public void testBadParams() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    
    //make action
    ActionIF action = new AddIntVariant();
    
    //build parms
    ActionParametersIF params = makeParameters("bn", "http://snus.org");
    ActionResponseIF response = makeResponse();
    
    //execute    
    try{
      action.perform(params, response);
      fail("Bad basename (String)");
    }catch (ActionRuntimeException e){}
        
  }

  public void testBadParams1() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    
    //make action
    ActionIF action = new AddIntVariant();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(bn, "scope")
					       , "http://snus.org");
    ActionResponseIF response = makeResponse();
    
    //execute    
    try{
      action.perform(params, response);
      fail("Bad scope (String)");
    }catch (ActionRuntimeException e){}
        
  }

  
}
