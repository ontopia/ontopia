/*
 * #!
 * Ontopia Webed
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.webed.impl.actions;

import java.util.Collections;
import java.util.Iterator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.variant.AddIntVariant;

public class TestAddIntVariant extends AbstractWebedTestCase {
  
  public TestAddIntVariant(String name) {
    super(name);
  }

  
  
  public void testNormalOperation() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF bn  = topic.getTopicNames().iterator().next();
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
    
    VariantNameIF vNew = bn.getVariants().iterator().next();
    assertFalse("The URL is not correct", 
		!(vNew.getValue().equals("http://www.sf.net")));
    
  }

    public void testNormalOperation2() throws java.io.IOException{
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF scope = getTopicById(tm, "tromso");
    TopicNameIF bn = null;
    Iterator<TopicNameIF> iter = topic.getTopicNames().iterator();
    while (iter.hasNext()) {
      bn = iter.next();
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
    
    VariantNameIF vNew = bn.getVariants().iterator().next();
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
    TopicNameIF bn  = topic.getTopicNames().iterator().next();
    
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
    TopicNameIF bn  = topic.getTopicNames().iterator().next();
    
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
