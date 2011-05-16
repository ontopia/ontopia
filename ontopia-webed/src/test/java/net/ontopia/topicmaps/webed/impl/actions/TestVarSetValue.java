
// $Id: TestVarSetValue.java,v 1.5 2008/06/12 14:37:26 geir.gronmo Exp $

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
import java.net.MalformedURLException;


public class TestVarSetValue extends AbstractWebedTestCase {
  
  public TestVarSetValue(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Good, Normal use, with scope
    
  */
  
  public void testNormalOperation() throws java.io.IOException{
     
    TopicIF topic = getTopicById(tm, "tromso");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
            
    VariantNameIF var = builder.makeVariantName(bn, "snus");
    
    //make action
    ActionIF action = new SetValue();
    
    //build parms
    ActionParametersIF params = makeParameters(var, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test              
    assertFalse("The value is not correct", 
		!(var.getValue().equals("http://www.sf.net")));
    
  }

  public void testNormalOperation2() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "tromso");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    TopicIF topic2 = getTopicById(tm, "gamst");
    TopicNameIF bn2  = (TopicNameIF) topic2.getTopicNames().iterator().next();

    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
    
    VariantNameIF var = builder.makeVariantName(bn, "");
    int bnsize = bn.getVariants().size();
    
    //make action
    ActionIF action = new SetValue();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(var, bn),
					       "The variant name string");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test             
    int bnNewSize = bn.getVariants().size();
    assertFalse("Variant of base name added or removed", 
		bnsize != bnNewSize);
   
    VariantNameIF varNew = (VariantNameIF) bn.getVariants().iterator().next();
        
    assertFalse("The value is not correct", 
                !(var.getValue().equals("The variant name string")));
  }


  public void testEmptyParams() throws java.io.IOException{
    //make action
    ActionIF action = new SetValue();
    
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
        
    //make action
    ActionIF action = new SetValue();
    
    //build parms
    ActionParametersIF params = makeParameters("bn", "http://snus.org");
    ActionResponseIF response = makeResponse();
    
    //execute    
    try{
      action.perform(params, response);
      fail("Bad basename (String)");
    } catch (ActionRuntimeException e) {
    }
        
  }

  public void testBadParams1() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "tromso");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    TopicIF topic2 = getTopicById(tm, "gamst");
    TopicNameIF bn2  = (TopicNameIF) topic2.getTopicNames().iterator().next();
    int bn2size = bn2.getVariants().size();

    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
    
    VariantNameIF var = builder.makeVariantName(bn, "");
    
        
    //make action
    ActionIF action = new SetValue();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(var, "scope")
					       , "http://snus.org");
    ActionResponseIF response = makeResponse();
    
    //execute    
    try{
      action.perform(params, response);
      fail("Bad basename (String)");
    } catch (ActionRuntimeException e) {
    }
  }


}
