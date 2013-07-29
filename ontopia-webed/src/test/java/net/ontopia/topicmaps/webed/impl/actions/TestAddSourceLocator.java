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
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.AddSourceLocator;

public class TestAddSourceLocator extends AbstractWebedTestCase {
  
  public TestAddSourceLocator(String name) {
    super(name);
  }

  /*
    1. Good, Normal use
    2. Bad , No TM
    3. Bad , bad url
    4. Bad , bad parameters
  */
  public void testNormalOperation() throws IOException {
    try {
      //make action
      ActionIF action = new AddSourceLocator();
      
      //build parms
      ActionParametersIF params = makeParameters(tm, "http://slashdot.org");
      ActionResponseIF response = makeResponse();
      
      //execute    
      action.perform(params, response);
      //test
     
      Iterator<LocatorIF> i = tm.getItemIdentifiers().iterator();
      boolean excists = false;

      while (i.hasNext()) {
	LocatorIF locator = i.next(); 
	String url = locator.getAddress();
	if (url.equals("http://slashdot.org/")) {
	  excists = true;
	  break;
	}
      }
      
      assertFalse("No SourceLocators", tm.getItemIdentifiers().isEmpty());
      assertFalse("Sourcelocator not set correctly", !(excists));

    } catch (ActionRuntimeException e) {
      fail("Good TM, good url, should work");
    }
  }

  public void testBadTM() throws IOException {
    try {
      String tm = "mama";
      
      // make action
      ActionIF action = new AddSourceLocator();
      
      // build parms
      ActionParametersIF params = makeParameters(tm, "http://sourceforge.org");
      ActionResponseIF response = makeResponse();
      
      // execute    
      action.perform(params, response);
      fail("Bad TM, Good url, shouldn't work");
    } catch (ActionRuntimeException e) {
    }     
  }

  public void testBadURL() throws IOException {
    try {
      // make action
      ActionIF action = new AddSourceLocator();
      
      // build parms
      ActionParametersIF params = makeParameters(tm, "foo.bar");
      ActionResponseIF response = makeResponse();
      
      // execute    
      action.perform(params, response);
      fail("Good TM, bad url, shouldn't work");
    } catch (ActionRuntimeException e) {
    }
  }

  public void testBadParameters() throws IOException {
    try {
      // make action
      ActionIF action = new AddSourceLocator();
      
      // build parms
      ActionParametersIF params = makeParameters(tm, "foo.bar mama papa");
      ActionResponseIF response = makeResponse();
      
      // execute    
      action.perform(params, response);
      fail("Good TM, bad urls, shouldn't work");
    } catch (ActionRuntimeException e) {
    }     
  }
}
