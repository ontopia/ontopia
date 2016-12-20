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

import org.w3c.dom.Node;

import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;

/**
 * INTERNAL: Tests for the <webed:button> tag.
 */
public class ButtonTagTest extends AbstractWebBasedTestCase {

  public ButtonTagTest(String aName) {
    super(aName);
  }

  public void testSubmitAttributes () throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testSubmitAttributes.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    checkAttribute(button, "type", "submit");
    checkAttribute(button, "value", "BUTTON");
    checkNameAttribute(button, "buttonTest");
    checkCommonButtonAttributes(button);
    
    //Submit the form to check that no problems occur
    form.getButtons()[0].click();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  }

  public void testImageAttributesNoButtonMapImage () throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testImageAttributesNoButtonMapImage.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    checkAttribute(button, "src", "button.gif");
    checkCommonImageAttributes(button);
    
    //Submit the form to check that no problems occur
    form.getButtons()[0].click();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  
  }

  protected void checkCommonImageAttributes(Node button) {

    checkAttribute(button, "type", "image");
    checkAttribute(button, "title", "BUTTON");
    checkNameAttribute(button, "buttonTest");
    checkCommonButtonAttributes(button);
    
  }

  public void testImageAttributesButtonMapSrc () throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testImageAttributesButtonMapSrc.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    checkAttribute(button, "src", webedTestApplication + "/test/ButtonTag/button.gif");
    checkAttribute(button, "border", "0");
    checkCommonImageAttributes(button);
    
    //Submit the form to check that no problems occur
    form.getButtons()[0].click();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  
  }

  public void testImageAttributesButtonMapAbsoluteSrc () throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testImageAttributesButtonMapAbsoluteSrc.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    checkAttribute(button, "src","button.gif");
    checkAttribute(button, "border", "0");
    checkCommonImageAttributes(button);
    
    //Submit the form to check that no problems occur
    form.getButtons()[0].click();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  
  }

  public void testResetAttributes () throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testResetAttributes.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    checkAttribute(button, "type", "reset");
    checkAttribute(button, "value", "BUTTON");
    checkCommonButtonAttributes(button);
    
    //Submit the form to check that no problems occur
    form.getButtons()[0].click();
    // Check that no forward occurs
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/ButtonTag/testResetAttributes.jsp", wc.getCurrentPage().getURL().getPath());
  }

  public void testReadonlyNot() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testReadonlyNot.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    Node id = button.getAttributes().getNamedItem("id");
    assertTrue("Button element not rendered on a form that is not read-only.", 
        id.getNodeValue().equals("ID"));
  }
  
  public void testReadonlyButtonTrue() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testReadonlyButtonTrue.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    Node id = button.getAttributes().getNamedItem("id");
    assertFalse("Button element with readonly=\"true\" was rendered.", 
        id.getNodeValue().equals("ID"));
  }
  
  public void testReadonlyButtonEvalTrue() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testReadonlyButtonEvalTrue.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    Node id = button.getAttributes().getNamedItem("id");
    assertFalse("Button element with readonly evaluating to true was rendered.", 
        id.getNodeValue().equals("ID"));
  }
  
  public void testReadonlyButtonEvalFalse() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testReadonlyButtonEvalFalse.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    Node id = button.getAttributes().getNamedItem("id");
    assertTrue("Button element with readonly evaluating to false was not " +
        "rendered.", id.getNodeValue().equals("ID"));
  }
  
  public void testReadonlyTrue() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testReadonlyTrue.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    Node id = button.getAttributes().getNamedItem("id");
    assertFalse("Button element rendered on read-only form.", 
        id.getNodeValue().equals("ID"));
  }
  
  public void testReadonlyTrueButtonFalse() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ButtonTag/testReadonlyTrueButtonFalse.jsp");
    WebForm form = resp.getForms()[0];
    // The button is rendered as the last item in the frames DOM
    Node button = getLastElementChild(form.getDOMSubtree());
    
    Node id = button.getAttributes().getNamedItem("id");
    assertTrue("Button element with readonly=\"false\" not rendered on " +
        "read-only form.", 
        id.getNodeValue().equals("ID"));
  }
  
  public void testLocking1() throws Exception {
    assertTrue("Testing testing", true);
  }
  
  // --- Internal helpers

  protected void checkCommonButtonAttributes(Node button) {
    checkType(button, "input");
    checkAttribute(button, "class", "bttn");
    checkAttribute(button, "id", "ID");
    checkForExtraAttributes(button);
  }  
}
