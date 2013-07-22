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

import java.io.IOException;
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

public class TestAddExtVariant extends AbstractWebedTestCase {
  
  public TestAddExtVariant(String name) {
    super(name);
  }
  
  public void testNormalOperation() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    int bnNum = bn.getVariants().size();
    
    //make action
    ActionIF action = new AddExtVariant();
    
    //build parms
    ActionParametersIF params = makeParameters(bn, "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test
    assertFalse("No new variant set", bnNum == bn.getVariants().size());
    
    VariantNameIF vNew = (VariantNameIF) bn.getVariants().iterator().next();
    assertFalse("The URL is not correct", 
		!(vNew.getLocator().getAddress().equals("http://www.sf.net/")));
  }

  public void testNormalOperation2() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    TopicIF scope = getTopicById(tm, "tromso");
    Iterator iter = topic.getTopicNames().iterator();
    TopicNameIF bn = null;
    while (iter.hasNext()) {
      bn = (TopicNameIF) iter.next();
      if (bn.getValue().equals("Morten Gamst Pedersen")) break;
    }
    int bnNum = bn.getVariants().size();
    
    //make action
    ActionIF action = new AddExtVariant();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(bn, scope), "http://www.sf.net");
    ActionResponseIF response = makeResponse();
    
    //execute    
    action.perform(params, response);
    
    //test
    assertFalse("No new variant set", bnNum == bn.getVariants().size());
    
    VariantNameIF vNew = (VariantNameIF) bn.getVariants().iterator().next();
    assertFalse("The URL is not correct", 
                !(vNew.getLocator().getAddress().equals("http://www.sf.net/")));
    assertFalse("The scope is not correct",
                vNew.getScope().contains(scope) && vNew.getScope().size() == 2);
  }
  
  public void testBadURL() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    
    //make action
    ActionIF action = new AddExtVariant();
    
    //build parms
    ActionParametersIF params = makeParameters(bn, "snus");
    ActionResponseIF response = makeResponse();
    
    //execute    
    try{
      action.perform(params, response);
      fail("Malformed URL");
    } catch (ActionRuntimeException e) {
    }
  }


  public void testEmptyParams() throws IOException {
    //make action
    ActionIF action = new AddExtVariant();
    
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
  
  public void testBadParams() throws IOException {
    
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    
    //make action
    ActionIF action = new AddExtVariant();
    
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
    TopicIF topic = getTopicById(tm, "gamst");
    TopicNameIF bn  = (TopicNameIF) topic.getTopicNames().iterator().next();
    
    //make action
    ActionIF action = new AddExtVariant();
    
    //build parms
    ActionParametersIF params = makeParameters(makeList(bn, "scope")
					       , "http://snus.org");
    ActionResponseIF response = makeResponse();
    
    //execute    
    try{
      action.perform(params, response);
      fail("Bad scope (String)");
    } catch (ActionRuntimeException e) {
    }
  }
}
