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

package net.ontopia.topicmaps.webed;

import net.ontopia.topicmaps.webed.impl.basic.Constants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;

/**
 * INTERNAL: Tests for the <webed:form> tag.
 */
public class FormTagTest extends AbstractWebBasedTestCase {

  public FormTagTest(String aName) {
    super(aName);
  }
  
  /* Disabled because httpunit doesn't seem to be able to test this
   * Verified in browser that functionality does work
  public void testTargetAttribute() throws Exception {    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FormTag/testTarget.jsp");
    WebForm form = resp.getForms()[0];
    
    checkAttribute(form.getDOMSubtree(), "target", "testTarget");
    
    form.getButtons()[0].click();
    resp = wc.getCurrentPage();

    // Check that the "current" window is still where it should be
    assertEquals("target Failed", webedTestApplication
        + "/test/FormTag/testTarget.jsp", resp.getURL().getPath());
    
    // Check that there now exists a new window "testTarget" and that
    // it points to the correct page.
    resp = wc.getFrameContents("testTarget");
    
    assertEquals("target Failed", webedTestApplication
        + "/test/defaultForward.html", resp.getURL().getPath());    
  }
  */

  public void testRelativeActionURI() throws Exception {   
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FormTag/testRelativeActionURI.jsp");
    WebForm form = resp.getForms()[0];
    
    assertEquals("Incorrect action", "TestTarget", form.getAction());
  }

  public void testAbsoluteActionURI() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FormTag/testAbsoluteActionURI.jsp");
    WebForm form = resp.getForms()[0];
    
    assertEquals("Incorrect action", "/TestTarget", form.getAction());
  }

  public void testAttributes() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FormTag/testAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node formNode = form.getDOMSubtree();
    
    checkType(formNode, "form");
    checkAttribute(formNode, "id", "ID");
    checkAttribute(formNode, "method", "POST");
    checkAttribute(formNode, "name", Constants.FORM_EDIT_NAME);
    checkAttribute(formNode, "action", webedTestApplication + "/process");
    checkAttribute(formNode, "onsubmit", "return true;");
    checkForExtraAttributes(formNode);
    
    Node tm = getNthElementChild(formNode, 0);
    checkType(tm, "input");
    checkAttribute(tm, "type", "hidden");
    checkAttribute(tm, "name","tm");
    checkAttribute(tm, "value", "test.ltm");
    checkForExtraAttributes(tm);
        
    Node actionGroup = getNthElementChild(formNode, 1);
    checkType(actionGroup, "input");
    checkAttribute(actionGroup, "type", "hidden");
    checkAttribute(actionGroup, "name", "ag");
    checkAttribute(actionGroup, "value", "testActionGroup");
    checkForExtraAttributes(actionGroup);
    
    Node requestId = getNthElementChild(formNode, 2);
    checkType(requestId, "input");
    checkAttribute(requestId, "type", "hidden");
    checkAttribute(requestId, "name", "requestid");
    checkAttributeStartsWith(requestId, "value", "rid");
    checkForExtraAttributes(requestId);
    
    Node linkForward = getNthElementChild(formNode, 3);
    checkType(requestId, "input");
    checkAttribute(linkForward, "type", "hidden");
    checkAttribute(linkForward, "name", "linkforward");
    checkAttributeStartsWith(linkForward, "id", "linkforwardrid");
    checkForExtraAttributes(linkForward);
    
    assertNull("Unexpected element", getNthElementChild(formNode, 4));
   
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  }
  
  /* Disabled because FormTag throws a NPE because the attribute 
   * 'javax.servlet.jsp.jstl.fmt.localizationContext.page' is missing
  public void testLocking() throws Exception {
    // First session should succeed.
    WebResponse success = wc.getResponse(webedTestLocation
        + "/test/FormTag/testLocking.jsp");
    assertEquals("No locks are in place, but page failed",
        webedTestApplication + "/test/FormTag/testLocking.jsp", success.getURL()
        .getPath());
    
    // Second session should get lock forward.
    WebConversation second = new WebConversation();
    WebResponse locked = second.getResponse(webedTestLocation
        + "/test/FormTag/testLocking.jsp");
    assertEquals("Lock in place, should get locking forward page.",
                 webedTestApplication + "/test/FormTag/lockingForward.html",
                 locked.getURL().getPath());
    
    // Submitting first page should release the lock.
    WebForm form = success.getForms()[0];
    form.getButtons()[0].click();
    
    // Second session should now succeed
    success = second.getResponse(webedTestLocation + "/test/FormTag/testLocking.jsp");
    assertEquals("Locks removed, but page failed", webedTestApplication
        + "/test/FormTag/testLocking.jsp", success.getURL().getPath());
    
    // Submit this second locked page so that further tests do not lock
    form = success.getForms()[0];
    form.getButtons()[0].click();
  }
  */

  /* Disabled because FormTag throws a NPE because the attribute 
   * 'javax.servlet.jsp.jstl.fmt.localizationContext.page' is missing
  public void testLockingEL() throws Exception {
    // First session should succeed.
    WebResponse success = wc.getResponse(webedTestLocation
        + "/test/FormTag/testLockingEL.jsp");
  }
  */

  /* Disabled because FormTag's velocity no longer reacts to readonly
  public void testReadonlyTrue() throws Exception {
    WebResponse success = wc.getResponse(webedTestLocation
        + "/test/FormTag/testReadonlyTrue.jsp");
    assertTrue("Form element found in read-only page",
               success.getForms().length == 0);
  }
  */

  public void testReadonlyFalse() throws Exception {
    WebResponse success = wc.getResponse(webedTestLocation
        + "/test/FormTag/testReadonlyFalse.jsp");
    assertTrue("No form element found in read-write page",
               success.getForms().length == 1);
  }
}
