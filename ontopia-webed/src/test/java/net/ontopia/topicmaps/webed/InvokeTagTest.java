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

//$ Id: InvokeTagTest.java,v 1.6 2005/05/09 13:53:32 grove Exp $

package net.ontopia.topicmaps.webed;

import org.w3c.dom.Node;

import com.meterware.httpunit.Button;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;

/**
 * INTERNAL: Tests for the <webed:invoke> tag.
 */
public class InvokeTagTest extends AbstractWebBasedTestCase {

  public InvokeTagTest(String aName) {
    super(aName);
  }

  public void testTopicValueAttributes () throws Exception {  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/InvokeTag/testTopicValueAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    // The "1" is the objectId of the TM object passed as the value.
    // Since we do not have access to the TM here, we must just do a 
    // hard coded check. 
    // NOTE: this value may change if the LTM parser changes.
    checkAttribute(node, "value", "1");
    checkCommonAttributes(node);
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  private void checkCommonAttributes(Node node) {

    checkType(node, "input");
    checkAttribute(node, "type", "hidden");
    checkNameAttribute(node,"invokeTest");
    checkForExtraAttributes(node);
  }

  public void testStringValueAttributes () throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/InvokeTag/testStringValueAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkAttribute(node, "value", "VALUE");
    checkCommonAttributes(node);
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  
  }

  public void testAttributes () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/InvokeTag/testAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkAttribute(node, "value", "no-value-given");
    checkCommonAttributes(node);
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
      
  }

  public void testRunIfNoChangesTrue() throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/InvokeTag/testRunIfNoChangesTrue.jsp");
    WebForm form = resp.getForms()[0];
    Button button = form.getButtons()[0];
    // With "runIfNoChanges=true" the action should run every time
    // the form is submited
    button.click();
    assertEquals("Action should have executed", "SUCCESS", wc.getCurrentPage().getText());
      
  }

  public void testRunIfNoChangesFalse() throws Exception {
  
    WebResponse response = wc.getResponse(webedTestLocation
        + "/test/InvokeTag/testRunIfNoChangesFalse.jsp");
    WebForm form = response.getForms()[0];
    // With "runIfNoChanges=false" the action should only run if another
    // action on the form have run.
    
    // First time, no action fired
    form.getButtons()[0].click();
    assertEquals("Action should not have executed", "null", wc.getCurrentPage().getText());

    // Refresh form
    response = wc.getResponse(webedTestLocation
        + "/test/InvokeTag/testRunIfNoChangesFalse.jsp");
    form = response.getForms()[0];
    
    // Modify value, action should run
    form.setParameter(response.getElementWithID("FLD").getName(),"NEW VALUE");
    form.getButtons()[0].click();
    assertEquals("Action should have executed", "SUCCESS", wc.getCurrentPage().getText());
    
  }

  public void testDoubleSubmit() throws Exception {
  
    WebResponse response = wc.getResponse(webedTestLocation
        + "/test/InvokeTag/testRunIfNoChangesFalse.jsp");
    WebForm form = response.getForms()[0];
    
    // Submit different modification second time
    form.setParameter(response.getElementWithID("FLD").getName(),"NEW VALUE1");
    form.getButtons()[0].click();
    assertEquals("Action should have executed", "SUCCESS", wc.getCurrentPage().getText());
    
    try {    
      // Submit different modification second time
      form.setParameter(response.getElementWithID("FLD").getName(),"NEW VALUE2");
      form.getButtons()[0].click();

      fail("Could submit form twice");

    } catch (Exception e) {
      // ok
    }
  }
  
  public void testReadonlyTrue() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/InvokeTag/testReadonlyTrue.jsp");
    WebForm form = resp.getForms()[0];
    // The invoke is rendered as the last item in the frames DOM
    Node invoke = getLastElementChild(form.getDOMSubtree());
    
    Node value = invoke.getAttributes().getNamedItem("value");
    assertFalse("Invoke element rendered on read-only form.", 
        value != null && value.getNodeValue().equals("no-value-given"));
  }
  
  public void testReadonlyTrueInvokeFalse() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/InvokeTag/testReadonlyTrueInvokeFalse.jsp");
    WebForm form = resp.getForms()[0];
    // The invoke is rendered as the last item in the frames DOM
    Node invoke = getLastElementChild(form.getDOMSubtree());
    
    Node value = invoke.getAttributes().getNamedItem("value");
    assertTrue("Invoke element with readonly=\"false\" not rendered on " +
        "read-only form.", 
        value != null && value.getNodeValue().equals("no-value-given"));
  }
}
