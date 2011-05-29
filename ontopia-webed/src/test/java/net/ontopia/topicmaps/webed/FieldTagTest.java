
package net.ontopia.topicmaps.webed;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;


import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;


/**
 * INTERNAL.
 */
public class FieldTagTest extends AbstractWebBasedTestCase {

  public FieldTagTest(String aName) {
    super(aName);
  }

  public void testTrimAttributeTrue () throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testTrimAttributeTrue.jsp");
    String original = wc.getResponse(webedTestLocation
        + "/test/FieldTag/value.txt").getText();

    WebForm form = resp.getForms()[0];
    String value = form.getParameterValue(resp.getElementWithID("FLD").getName());

    // Trimming should be carried out
    assertFalse("No trimming occurred", original.equals(value));
    assertEquals("Incorrect Trimming", original.trim(), value);
  }

  public void testTrimAttributeFalse () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testTrimAttributeFalse.jsp");
    String original = wc.getResponse(webedTestLocation
        + "/test/FieldTag/value.txt").getText();
  
    HTMLElement field = resp.getElementWithID("FLD");
    WebForm form = resp.getForms()[0];
    String value = form.getParameterValue(field.getName());
  
    // Trimming should not be carried out
    assertEquals("Trimming occurred", original, value);
  
  }

  public void testTextAttributes () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testTextAttributes.jsp");
    WebForm form = resp.getForms()[0];
    // The field is rendered as the last item in the frames DOM
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkType(node, "input");
    checkAttribute(node, "value", "VALUE");
    checkAttribute(node, "class", "input");
    checkAttribute(node, "type", "text");
    checkAttribute(node, "maxlength", "75");
    checkAttribute(node, "size", "50");
    checkCommonAttributes(node);
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  public void testHiddenAttributes () throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testHiddenAttributes.jsp");
    WebForm form = resp.getForms()[0];
    // The field is rendered as the last item in the frames DOM
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkType(node, "input");
    checkAttribute(node, "value", "VALUE");
    checkAttribute(node, "type", "hidden");
    checkCommonAttributes(node);
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  private void checkCommonAttributes(Node node) {

    checkAttribute(node, "id", "ID");
    checkNameAttribute(node, "fieldTest");
    checkForExtraAttributes(node);
  }

  public void testPasswordAttributes () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testPasswordAttributes.jsp");
    WebForm form = resp.getForms()[0];
    // The field is rendered as the last item in the frames DOM
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkType(node, "input");
    checkAttribute(node, "value", "VALUE");
    checkAttribute(node, "type", "password");
    checkAttribute(node, "class", "input");
    checkAttribute(node, "maxlength", "75");
    checkAttribute(node, "size", "50");
    checkCommonAttributes(node);
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  public void testTextAreaAttributes () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testTextAreaAttributes.jsp");
    WebForm form = resp.getForms()[0];
    // The field is rendered as the last item in the frames DOM
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkType(node, "textarea");
    checkAttribute(node, "cols", "50");
    checkAttribute(node, "rows", "3");
    checkCommonAttributes(node);
    
    assertEquals("Missing value", "VALUE", node.getFirstChild().getNodeValue());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }
  
  /* Disabled test because it hasn't been working since r188
     The ProcessServlet can't find the request actions and gives a HTTP 500
  public void testJavaScriptValidation () throws Exception {
    // Validation is against the regexp /foo|bar/.
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testValidation.jsp");

    // Validating ian should fail (matches regular expression on jsp).
    changeField("field1", "ian", resp);

    //I could not get this to work, so I have re-written 
    // this with a different test.
    //resp.getLinkWith("Validate.").click();
    
    resp.getForms()[1].submit();
    assertNotNull(wc.popNextAlert());

    // Validating foo should pass (matches regular expression on jsp).
    changeField("field1", "foo", resp);
    
    resp.getForms()[1].submit();
    assertNull(wc.getNextAlert());
  }
  */
  
  // FIXME: !!! Take out the "1" before submitting. Do not take out this test!!!!
  public void t1estSubstringFails () throws Exception {
    // Validation is against the regexp /foo|bar/.
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testValidation.jsp");

    // Validating fo should fail (does not match regular expression).
    changeField("field1", "fo", resp);
    resp.getLinkWith("Validate.").click();
    resp = wc.getCurrentPage();
    assertEquals(getFieldValue("field1", resp), "fo");
  }
  
  // FIXME: !!! Take out the "1" before submitting. Do not take out this test!!!!
  public void t1estSuperstringFails () throws Exception {
    // Validation is against the regexp /foo|bar/.
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testValidation.jsp");

    // Validating fooo should fail (does not match regular expression).
    changeField("field1", "fooo", resp);
    resp.getLinkWith("Validate.").click();
    resp = wc.getCurrentPage();
    assertEquals(getFieldValue("field1", resp), "fooo");
  }
  
  /**
   * Check the contents of a given input field.
   * @param fieldID The ID of the field.
   * @throws SAXException
   */
  protected String getFieldValue(String fieldID, WebResponse response)
      throws SAXException {
    HTMLElement field = response.getElementWithID(fieldID);
    assertNotNull(field);
    WebForm forms[] = response.getForms();
    assertTrue(forms.length > 0);
    WebForm form = forms[1];
    return form.getParameterValue(field.getName());
  }

  /**
   * Change the contents of a given input field.
   * @param fieldID The ID of the field.
   * @param newValue The new value to put in the input field.
   * @throws SAXException
   */
  protected void changeField(String fieldID, String newValue, 
      WebResponse response)
      throws SAXException {
    HTMLElement field = response.getElementWithID(fieldID);
    assertNotNull(field);
    WebForm forms[] = response.getForms();
    assertTrue(forms.length > 0);
    WebForm form = forms[1];
    form.setParameter(field.getName(), newValue);
  }

  public void testNoTrimAttribute () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testNoTrimAttribute.jsp");
    String original = wc.getResponse(webedTestLocation
        + "/test/FieldTag/value.txt").getText();
  
    WebForm form = resp.getForms()[0];
    String value = form.getParameterValue(resp.getElementWithID("FLD").getName());
  
    // By default trimming should be carried out
    assertFalse("No trimming occurred", original.equals(value));
    assertEquals("Incorrect Trimming", original.trim(), value);
  }
  
  public void testLineBreakInParams() throws Exception {
    wc.getResponse(webedTestLocation 
        + "/test/FieldTag/testLineBreakInParams.jsp");
  }

  public void testReadonlyTrue() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testReadonlyTrue.jsp");
    WebForm form = resp.getForms()[0];
    // The field is rendered as the last item in the frames DOM
    Node field = getLastElementChild(form.getDOMSubtree());
    
    Node id = field.getAttributes().getNamedItem("id");
    assertFalse("Field element rendered on read-only form.", 
        id.getNodeValue().equals("ID"));
  }
  
  public void testReadonlyTrueFieldFalse() throws Exception {
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/FieldTag/testReadonlyTrueFieldFalse.jsp");
    WebForm form = resp.getForms()[0];
    // The field is rendered as the last item in the frames DOM
    Node field = getLastElementChild(form.getDOMSubtree());
    
    Node id = field.getAttributes().getNamedItem("id");
    assertTrue("Field element with readonly=\"false\" not rendered on " +
        "read-only form.", 
        id.getNodeValue().equals("ID"));
  }
}
