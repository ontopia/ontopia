
// $Id: TestVarSetLocator.java,v 1.6 2009/04/27 11:08:58 lars.garshol Exp $

package net.ontopia.topicmaps.webed.impl.actions.test;

import java.io.IOException;
import java.util.*;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.variant.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;
import java.net.MalformedURLException;


public class TestVarSetLocator extends AbstractWebedTestCase {
  
  public TestVarSetLocator(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use
    2. Good, Normal use, with scope
  */
  
  public void testNormalOperation() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
    
    // create new (external) variant for base name
    URILocator locator = null;
    try {
      locator = new URILocator("http://www.snus.org");
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for occurrence: " + e);
    }
    
    VariantNameIF var = builder.makeVariantName(bn, locator);
    
    //make action
    ActionIF action = new SetLocator();
    
    //build parms
    ActionParametersIF params = makeParameters(var, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test              
    LocatorIF locNew = var.getLocator();
    assertFalse("The locator is not correct", 
		!(var.getLocator().getAddress().equals("http://www.sf.net/")));
  }

  public void testNormalOperation2() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    TopicIF topic2 = getTopicById(tm, "gamst");
    TopicNameIF bn2  = (TopicNameIF) topic2.getTopicNames().iterator().next();

    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
    
    VariantNameIF var = builder.makeVariantName(bn, "");
    int bnsize = bn.getVariants().size();
    
    //make action
    ActionIF action = new SetLocator();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(var, bn), 
					       "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test             
    int bnNewSize = bn.getVariants().size();
    assertFalse("New locator added", bnsize != bnNewSize);
   
    VariantNameIF varNew = (VariantNameIF) bn.getVariants().iterator().next();
    LocatorIF locNew = varNew.getLocator();
    
     assertFalse("The locator is not correct", 
		!(var.getLocator().getAddress().equals("http://www.sf.net/")));
  }


public void testEmptyParams() throws IOException {
    //make action
    ActionIF action = new SetLocator();
    
    //build parms
    ActionParametersIF params = makeParameters(Collections.EMPTY_LIST, 
					       "http://snus.org");
    ActionResponseIF response = makeResponse();
    
    //execute    
    try{
      action.perform(params, response);
      fail("Empty Collection as params");
    } catch (ActionRuntimeException e) {
    }
  }

  public void testMalformedURL() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
    
    // create new (external) variant for base name
    URILocator locator = null;
    try {
      locator = new URILocator("http://www.snus.org");
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for occurrence: " + e);
    }
    
    VariantNameIF var = builder.makeVariantName(bn, locator);
        
    //make action
    ActionIF action = new SetLocator();
    
    //build parms
    ActionParametersIF params = makeParameters("bn", "tuttut");
    ActionResponseIF response = makeResponse();
    
    //execute    
    try{
      action.perform(params, response);
      fail("Bad URL");
    } catch (ActionRuntimeException e) {
    }
  }

  public void testBadParams() throws IOException {
    //make action
    ActionIF action = new SetLocator();
    
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

  public void testBadParams1() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    TopicIF topic2 = getTopicById(tm, "gamst");
    TopicNameIF bn2  = (TopicNameIF) topic2.getTopicNames().iterator().next();
    int bn2size = bn2.getVariants().size();

    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
    
    VariantNameIF var = builder.makeVariantName(bn, "");
    
    //make action
    ActionIF action = new SetLocator();
    
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
