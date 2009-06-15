
// $Id: FormTagTest.java,v 1.9 2006/05/25 06:34:48 larsga Exp $

package net.ontopia.topicmaps.webed.test;

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
    
    NodeList children = formNode.getChildNodes();
    
    Node tm = children.item(0);
    checkType(tm, "input");
    checkAttribute(tm, "type", "hidden");
    checkAttribute(tm, "name","tm");
    checkAttribute(tm, "value", "test.ltm");
    checkForExtraAttributes(tm);
        
    Node actionGroup = children.item(2);
    checkType(actionGroup, "input");
    checkAttribute(actionGroup, "type", "hidden");
    checkAttribute(actionGroup, "name", "ag");
    checkAttribute(actionGroup, "value", "testActionGroup");
    checkForExtraAttributes(actionGroup);
    
    Node requestId = children.item(4);
    checkType(requestId, "input");
    checkAttribute(requestId, "type", "hidden");
    checkAttribute(requestId, "name", "requestid");
    checkAttributeStartsWith(requestId, "value", "rid");
    checkForExtraAttributes(requestId);
    
    Node linkForward = children.item(6);
    checkType(requestId, "input");
    checkAttribute(linkForward, "type", "hidden");
    checkAttribute(linkForward, "name", "linkforward");
    checkAttributeStartsWith(linkForward, "id", "linkforwardrid");
    checkForExtraAttributes(linkForward);
    
    assertNull("Unexpected element", children.item(7));
   
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  }

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

  public void testLockingEL() throws Exception {
    // First session should succeed.
    WebResponse success = wc.getResponse(webedTestLocation
        + "/test/FormTag/testLockingEL.jsp");
  }

  public void testReadonlyTrue() throws Exception {
    WebResponse success = wc.getResponse(webedTestLocation
        + "/test/FormTag/testReadonlyTrue.jsp");
    assertTrue("Form element found in read-only page",
               success.getForms().length == 0);
  }

  public void testReadonlyFalse() throws Exception {
    WebResponse success = wc.getResponse(webedTestLocation
        + "/test/FormTag/testReadonlyFalse.jsp");
    assertTrue("No form element found in read-write page",
               success.getForms().length == 1);
  }
}
