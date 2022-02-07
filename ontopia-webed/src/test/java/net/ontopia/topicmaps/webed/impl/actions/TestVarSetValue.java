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
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.variant.SetValue;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

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
    TopicNameIF bn  = topic.getTopicNames().iterator().next();
    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
            
    VariantNameIF var = builder.makeVariantName(bn, "snus", Collections.emptySet());
    
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
    TopicNameIF bn  = topic.getTopicNames().iterator().next();
    TopicIF topic2 = getTopicById(tm, "gamst");
    TopicNameIF bn2  = topic2.getTopicNames().iterator().next();

    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
    
    VariantNameIF var = builder.makeVariantName(bn, "", Collections.emptySet());
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
   
    VariantNameIF varNew = bn.getVariants().iterator().next();
        
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
    TopicNameIF bn  = topic.getTopicNames().iterator().next();
    TopicIF topic2 = getTopicById(tm, "gamst");
    TopicNameIF bn2  = topic2.getTopicNames().iterator().next();
    int bn2size = bn2.getVariants().size();

    TopicMapBuilderIF builder =
      bn.getTopicMap().getBuilder();
    
    VariantNameIF var = builder.makeVariantName(bn, "", Collections.emptySet());
    
        
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
