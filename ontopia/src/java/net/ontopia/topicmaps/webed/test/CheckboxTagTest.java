
// $Id: CheckboxTagTest.java,v 1.8 2007/07/13 19:05:54 eirik.opland Exp $

package net.ontopia.topicmaps.webed.test;

import org.w3c.dom.Node;

import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;

/**
 * INTERNAL: Tests for the <webed:checkbox> tag.
 */
public class CheckboxTagTest extends AbstractWebBasedTestCase {

  public CheckboxTagTest(String aName) {
    super(aName);
  }
  
  public void testNoStateAttributes () throws Exception {

    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/CheckboxTag/testNoStateAttributes.jsp");
    WebForm form = resp.getForms()[0];
    // The checkbox is rendered as the last item in the frames DOM
    Node checkbox = form.getDOMSubtree().getLastChild();
    
    checkCommonAttributes(checkbox);
    assertNull("Checked attribute found", checkbox.getAttributes().getNamedItem("checked"));
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
      
  }
  private void checkCommonAttributes(Node checkbox) {

    checkType(checkbox, "input");
    checkAttribute(checkbox, "type", "checkbox");
    checkAttribute(checkbox, "id", "ID");
    checkNameAttribute(checkbox, "checkboxTest");
    checkForExtraAttributes(checkbox);
  }
  public void testStateCheckedAttributes () throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/CheckboxTag/testStateCheckedAttributes.jsp");
    WebForm form = resp.getForms()[0];
    // The checkbox is rendered as the last item in the frames DOM
    Node checkbox = form.getDOMSubtree().getLastChild();
    
    checkAttribute(checkbox, "checked", "checked");
    checkCommonAttributes(checkbox);
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }
  public void testStateUnCheckedAttributes () throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/CheckboxTag/testStateUnCheckedAttributes.jsp");
    WebForm form = resp.getForms()[0];
    // The checkbox is rendered as the last item in the frames DOM
    Node checkbox = form.getDOMSubtree().getLastChild();
    
    checkCommonAttributes(checkbox);
    assertNull("Checked attribute found", checkbox.getAttributes().getNamedItem("checked"));
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  }

  public void testSubaction() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/CheckboxTag/testSubactionForward.jsp");
    WebForm form = resp.getForms()[0];
    form.getButtons()[0].click();
    
    resp = wc.getResponse(webedTestLocation
        + "/test/CheckboxTag/testSubaction.jsp");
    form = resp.getForms()[0];
    form.getButtons()[0].click();
    
    resp = wc.getCurrentPage();
    assertTrue("Created topic with subaction of unchecked checkbox." , 
        wc.getCurrentPage().getText().indexOf("testCheckboxSubactionTopic") < 0);
    
    resp = wc.getResponse(webedTestLocation
        + "/test/CheckboxTag/testSubaction.jsp");
    form = resp.getForms()[0];
    Node checkbox = form.getDOMSubtree().getLastChild();
    form.setCheckbox(checkbox.getAttributes().getNamedItem("name").getNodeValue(), true);
    form.getButtons()[0].click();
    resp = wc.getCurrentPage();
    assertTrue("Failed to create topic with subaction of checked checkbox." , 
        wc.getCurrentPage().getText().indexOf("testCheckboxSubactionTopic") >= 0);
  }
  
  // FIXME: This the JSP for this test case doesn't seem to exist, so I think
  // this test case should either be removed or the JSP created.
  //! public void testStateEL() throws Exception {
  //!   wc.getResponse(webedTestLocation + "/test/CheckboxTag/testStateEL.jsp");
  //! }

  public void testReadonlyTrue() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/CheckboxTag/testReadonlyTrue.jsp");
    WebForm form = resp.getForms()[0];
    // The checkbox is rendered as the last item in the frames DOM
    Node checkbox = form.getDOMSubtree().getLastChild();
    
    Node disabled = checkbox.getAttributes().getNamedItem("disabled");
    assertTrue("Checkbox element enabled on read-only form.", 
        disabled != null && disabled.getNodeValue().equals("disabled"));
  }
  
  public void testReadonlyTrueCheckboxFalse() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/CheckboxTag/testReadonlyTrueCheckboxFalse.jsp");
    WebForm form = resp.getForms()[0];
    // The checkbox is rendered as the last item in the frames DOM
    Node checkbox = form.getDOMSubtree().getLastChild();
    
    Node disabled = checkbox.getAttributes().getNamedItem("disabled");
    assertFalse("Checkbox element with readonly=\"false\" disabled on " +
        "read-only form.",
        disabled != null && disabled.getNodeValue().equals("disabled"));
  }
}
