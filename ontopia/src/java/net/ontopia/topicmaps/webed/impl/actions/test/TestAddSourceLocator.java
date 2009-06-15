// $Id: TestAddSourceLocator.java,v 1.3 2009/04/27 11:08:58 lars.garshol Exp $

package net.ontopia.topicmaps.webed.impl.actions.test;

import java.io.IOException;
import java.util.*;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.tmobject.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.infoset.core.*;

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
     
      Iterator i = tm.getItemIdentifiers().iterator();
      boolean excists = false;

      while (i.hasNext()) {
	LocatorIF locator = (LocatorIF) i.next(); 
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
