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

import com.meterware.httpunit.*;

import net.ontopia.topicmaps.webed.impl.basic.Constants;

/**
 * INTERNAL.
 */
public class GeneralActionTest extends AbstractWebBasedTestCase {
  
  public GeneralActionTest(String name) {
    super(name);
  }

  public void testDefaultForward() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/General/testDefaultForward.jsp");
    WebForm form = resp.getForms()[0];
    resp = form.submit();
    assertEquals("Default Forward Failed", webedTestApplication
        + "/test/defaultForward.html", resp.getURL().getPath());
  }

  /**
   * Submit the form, if the correct action (TestAction) is executed,
   * then the response request should contain the request parameter
   * "result=SUCCESS", which is interpreted by the forwardPage
   * requestResult.jsp
   */
  public void testActionExecuted() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/General/testActionExecuted.jsp");
    WebForm form = resp.getForms()[0];
    form.getButtons()[0].click();
    resp = wc.getCurrentPage();      
    
    assertEquals("Action not executed", "SUCCESS", resp.getText());
  }

  public void testPassesParameters() throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/General/testActionParameters.jsp");
    WebForm form = resp.getForms()[0];
    form.getButtons()[0].click();
    resp = wc.getCurrentPage();      
    assertEquals("An error while checking passed parameters","SUCCESS", resp.getText());
    
  }

  public void testPeakAction() throws Exception {
  
    WebResponse response = wc.getResponse(webedTestLocation
        + "/test/General/testPeakAction.jsp");
    
    WebForm form = response.getForms()[0];
    // Set the value of the field which associated with one action, 
    // then click the button and execute a second action associated 
    // with it which peaks at the parameters sent to the action 
    // associated with the field.
    String result = "NEW VALUE";
    form.setParameter((response.getElementWithID("FLD").getName()),result);      
    Button button = form.getButtonWithID("BTN");
    button.click();
    assertEquals("The target value was not visable", result, wc.getCurrentPage().getText());
  }

  public void testValueChanged() throws Exception {
  
    WebResponse response = wc.getResponse(webedTestLocation
        + "/test/General/testValueChanged.jsp");
    
    WebForm form = response.getForms()[0];
    // Click the button, no value has changed, so no action should
    // be executed
    form.getButtonWithID("BTN").click();
    
    assertEquals("The action appears to have beed executed", "null", wc.getCurrentPage().getText());
    
    // Refresh form
    response = wc.getResponse(webedTestLocation
        + "/test/General/testValueChanged.jsp");
    form = response.getForms()[0];
    
    // Modify the value of the input field and click the
    // button again. This time the action should run.
    form.setParameter((response.getElementWithID("FLD").getName()),"MODIFIED");
    form.getButtonWithID("BTN").click();
    assertEquals("The action does not appear to have run", "MODIFIED", wc.getCurrentPage().getText());
  }

  public void testSubActions() throws Exception {
  
    WebResponse response = wc.getResponse(webedTestLocation
        + "/test/General/testSubActions.jsp");
    
    WebForm form = response.getForms()[0];
    // Click the button with no sub action attached.
    // This action should be executed, but not the sub-action
    form.getButtonWithID("NSA").click();
    assertEquals("Incorrect responce", "NO-SUB-ACTION", wc.getCurrentPage().getText());
    
    // Refresh form
    response = wc.getResponse(webedTestLocation
        + "/test/General/testSubActions.jsp");
    form = response.getForms()[0];
    
    // Click the button with a sub action attached.
    // The sub-action should run.
    form.getButtonWithID("SA").click();
    assertEquals("Incorrect responce", "SUB-ACTION", wc.getCurrentPage().getText());
    
  }

  public void testActionOrdering() throws Exception {
    
    WebResponse response = wc.getResponse(webedTestLocation
        + "/test/General/testActionOrdering.jsp");
    
    WebForm form = response.getForms()[0];
    // Modify all fields
    form.setParameter(response.getElementWithID("O1").getName(), "VALUE1" );
    form.setParameter(response.getElementWithID("O2").getName(), "VALUE2" );
    //Submit the form
    form.getButtons()[0].click();
    // All actions should run, and the result string should be
    // VALUE1-VALUE2
    assertEquals("Incorrect responce text", "VALUE1-VALUE2", wc.getCurrentPage().getText());
  
  }

  public void testNotExclusive() throws Exception {
  
    WebResponse response = wc.getResponse(webedTestLocation
        + "/test/General/testNotExclusiveAction.jsp");
    
    WebForm form = response.getForms()[0];
    // Modify both fields
    form.setParameter(response.getElementWithID("NET1").getName(), "NET1V" );
    form.setParameter(response.getElementWithID("NET2").getName(), "NET2V");
    //Submit the form
    form.getButtons()[0].click();
    // Since neither action is exclusive, the response text should contain 
    // both "NET1V" and "NET2V"
    assertTrue("Response does not contain the expected text 'NET1V'", wc.getCurrentPage().getText().indexOf("NET1V") >= 0);
    assertTrue("Response does not contain the expected text 'NET2V'", wc.getCurrentPage().getText().indexOf("NET2V") >= 0);
  }

  public void testExclusive() throws Exception {
  
    WebResponse response = wc.getResponse(webedTestLocation
        + "/test/General/testExclusiveAction.jsp");
    
    WebForm form = response.getForms()[0];
    // Modify both fields
    form.setParameter(response.getElementWithID("ET").getName(), "ETV" );
    form.setParameter(response.getElementWithID("AT").getName(), "ATV");
    //Submit the form
    form.getButtons()[0].click();
    // If only the exclusive action has executed, then the response text
    // could contain only "ETV". If both are executed, then the responce 
    // text would also contain "ETV"
    assertTrue("Response does not contain the expected text 'ETV'", wc.getCurrentPage().getText().indexOf("ETV") >= 0);
    assertFalse("Response contains the unexpected text 'ATV'", wc.getCurrentPage().getText().indexOf("ATV") >= 0);
    
  }

  public void testExclusiveEvaluateLTM() throws Exception {
  
    WebResponse response = wc.getResponse(webedTestLocation
        + "/test/General/testExclusiveEvaluateLTM.jsp");
    
    WebForm form = response.getForms()[0];
    // Modify both fields
    form.setParameter(response.getElementWithID("field").getName(), "passedExclusiveEvaluateLTM" );

    //Submit the form
    form.getButtons()[0].click();
    
    // Check if the webed:field content was evaluated (as it should), even
    // though it's action was excluded.
    assertTrue("Response doesn not contain expected text "
        + "'passedExclusiveEvaluateLTM'", wc.getCurrentPage().getText()
            .indexOf("passedExclusiveEvaluateLTM") >= 0);
  }


  public void testDefinedForward() throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/General/testDefinedForward.jsp");
    WebForm form = resp.getForms()[0];
    resp = form.submit();
    assertEquals("Default Forward Failed", webedTestApplication
        + "/test/definedForward.html", resp.getURL().getPath());
    
  }

  public void testFormUnregister() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/General/testFormUnregister.jsp");
    WebForm form = resp.getForms()[0];
    Button button = form.getButtonWithID("testFormUnregister");
    
    String requestId = form.getParameterValue(Constants.RP_REQUEST_ID);
    wc.getResponse(webedTestLocation + "/unregister?requestid=" + requestId);
    
    try {
      button.click();
      fail("Failed to unregister form action data.");
    } catch (HttpInternalErrorException e) {
    }
  }

  public void testTimedExpiryOfActionData() throws Exception {
    WebResponse resp1, resp2;
    WebForm form1, form2;
    
    // Set expiry age low.
    wc.getResponse(webedTestLocation
        + "/test/General/setBundleExpiryAge.jsp?bundleExpiryAge=0");

    // Test that form action data are expired after a short time.
    resp1 = wc.getResponse(webedTestLocation
        + "/test/General/testTimedExpiryOfActionData.jsp");
    form1 = resp1.getForms()[0];
    resp2 = wc.getResponse(webedTestLocation
        + "/test/General/testTimedExpiryOfActionData.jsp");
    form2 = resp2.getForms()[0];
    form2.submit();
    try {
      form1.submit();
      fail("Failed to expire form action data after set expiry time.");
    } catch (HttpInternalErrorException e) {
    }
    
    // Reset expiry.
    wc.getResponse(webedTestLocation
        + "/test/General/setBundleExpiryAge.jsp");

    // Test that form action data are _not_ expired after a short time.
    resp1 = wc.getResponse(webedTestLocation
        + "/test/General/testTimedExpiryOfActionData.jsp");
    form1 = resp1.getForms()[0];
    resp2 = wc.getResponse(webedTestLocation
        + "/test/General/testTimedExpiryOfActionData.jsp");
    form2 = resp2.getForms()[0];
    form2.submit();
    form1.submit();
  }

  public void testParamsEL() throws Exception {
    wc.getResponse(webedTestLocation + "/test/General/testParamsEL.jsp");
  }
}
