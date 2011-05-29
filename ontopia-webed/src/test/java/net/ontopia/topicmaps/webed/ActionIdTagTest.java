
package net.ontopia.topicmaps.webed;

import org.w3c.dom.Node;

import com.meterware.httpunit.Button;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.SubmitButton;

/**
 * INTERNAL: Tests for the <webed:actionid> tag.
 */
public class ActionIdTagTest extends AbstractWebBasedTestCase {

  public ActionIdTagTest(String aName) {
    super(aName);
  }

  public void testAttributesWithForm() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ActionIdTag/testAttributesWithForm.jsp");
    WebForm form = resp.getForms()[0];
    Node input = getLastElementChild(form.getDOMSubtree());
    checkNameAttribute(input,"actionidTest");
    checkAttribute(input, "type", "submit");
    checkForExtraAttributes(input);
    
    // Submit the form to check that no problems occur
    form.getButtons()[0].click();
    
    // Check for the correct forward
    assertEquals("Incorrect Result",
                 webedTestApplication + "/test/defaultForward.html",
                 wc.getCurrentPage().getURL().getPath());
  }

  /**
   * Unless control=button actionid buttons on the page will always
   * run.  This should be the default value of 'control', but in OKS
   * 3.0.2 it was not.  This means that actions will now be executed
   * in cases where they were not executed before. Bad, bad, bad.
   */
  /* Disabled because testControlDefault.jsp is missing
  public void testControlDefault() throws Exception {
    // render the form with two buttons in it
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ActionIdTag/testControlDefault.jsp");
    WebForm form = resp.getForms()[0];

    // submit the form using the other button (our action should not execute)
    Button click = form.getButtonWithID("click");
    resp = form.submit((SubmitButton) click);

    // now verify that the action did not execute
    assertTrue("Action attached to button executed when button was not pressed",
               !resp.getText().equals("SUCCESS"));
  }
  */
}
