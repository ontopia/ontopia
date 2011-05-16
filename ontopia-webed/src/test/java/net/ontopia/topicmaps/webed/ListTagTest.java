//$ Id: ListTagTest.java,v 1.6 2006/05/22 08:13:20 opland Exp $

package net.ontopia.topicmaps.webed;

import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;

/**
 * INTERNAL.
 */
public class ListTagTest extends AbstractWebBasedTestCase {

  public ListTagTest(String aName) {
    super(aName);
  }

  public void testMultiselectAttributesWithNone () throws Exception {    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testMultiselectAttributesWithNone.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkMultiselectAttributes(node, null);
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  public void testDefaultAttributesWithNone () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testDefaultAttributesWithNone.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkListAttributes(node, null, getKnownContents());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  public void testMultiselectAttributesWithUnspecified () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testMultiselectAttributesWithUnspecified.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkMultiselectAttributes(node, "-- NO SELECTION --");
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  }

  private void checkMultiselectAttributes(Node node, String unspecified) {

    checkAttribute(node, "multiple", "multiple");
    checkScrollingAttributes(node, unspecified);
  }

  public void testDefaultAttributesWithUnspecified () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testDefaultAttributesWithUnspecified.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkListAttributes(node, "-- NO SELECTION --", getKnownContents());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  }

  private void checkCommonDefaultAttributes(Node node) {

    checkType(node, "select");
    checkNameAttribute(node, "listTest");
    checkCommonAttributes(node);
  }

  public void testCheckboxAttributes () throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testCheckboxAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkCommonCheckboxAttributes(node);
    
    HashMap knownContents = getKnownContents();
    
    for (int i = 0; i < getElementChildCount(node); i=i+2) {
      Node option = getNthElementChild(node, i);
      checkType(option, "input");
      checkNameAttribute(option, "listTest");
      String objectId = option.getAttributes().getNamedItem("value").getNodeValue();
      String value = option.getNextSibling().getNodeValue();
      
      if (selectedObjectId().equals(objectId)) checkAttribute(option, "checked", "checked");
      String text = (String)knownContents.get(objectId);
      assertNotNull("Unexpected option - value: "+ objectId + " with content: " + value, text);
      assertEquals("Incorrect option content", text, value);
      knownContents.remove(objectId);
    }
    
    assertTrue("Items expected but not found: " + knownContents, knownContents.isEmpty());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  
  }

  public void testCheckboxAttributesEL () throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testCheckboxAttributesEL.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkCommonCheckboxAttributes(node);
    
    HashMap knownContents = getKnownContents();
    
    for (int i = 0; i < getElementChildCount(node); i=i+2) {
      Node option = getNthElementChild(node, i);
      checkType(option, "input");
      checkNameAttribute(option, "listTest");
      String objectId = option.getAttributes().getNamedItem("value").getNodeValue();
      String value = option.getNextSibling().getNodeValue();
      
      if (selectedObjectId().equals(objectId)) checkAttribute(option, "checked", "checked");
      String text = (String)knownContents.get(objectId);
      assertNotNull("Unexpected option - value: "+ objectId + " with content: " + value, text);
      assertEquals("Incorrect option content", text, value);
      knownContents.remove(objectId);
    }
    
    assertTrue("Items expected but not found: " + knownContents, knownContents.isEmpty());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  
  }

  public void testCheckboxAttributesELSelect () throws Exception {
  
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testCheckboxAttributesELSelect.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkCommonCheckboxAttributes(node);
    
    HashMap knownContents = getKnownContents();
    
    for (int i = 0; i < getElementChildCount(node); i=i+2) {
      Node option = getNthElementChild(node, i);
      checkType(option, "input");
      checkNameAttribute(option, "listTest");
      String objectId = option.getAttributes().getNamedItem("value").getNodeValue();
      String value = option.getNextSibling().getNodeValue();
      
      if (selectedObjectId().equals(objectId)) checkAttribute(option, "checked", "checked");
      String text = (String)knownContents.get(objectId);
      assertNotNull("Unexpected option - value: "+ objectId + " with content: " + value, text);
      assertEquals("Incorrect option content", text, value);
      knownContents.remove(objectId);
    }
    
    assertTrue("Items expected but not found: " + knownContents, knownContents.isEmpty());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  
  }


  
  public void testRadioAttributes () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testRadioAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkCommonRadioAttributes(node);
    
    HashMap knownContents = getKnownContents();
    
    for (int i = 0; i < getElementChildCount(node); i=i+2) {
      Node option = getNthElementChild(node, i);
      checkType(option, "input");
      checkNameAttribute(option, "listTest");
      String objectId = option.getAttributes().getNamedItem("value").getNodeValue();
      String value = option.getNextSibling().getNodeValue();
      
      if (selectedObjectId().equals(objectId)) checkAttribute(option, "checked", "checked");
      String text = (String)knownContents.get(objectId);
      assertNotNull("Unexpected option - value: "+ objectId + " with content: " + value, text);
      assertEquals("Incorrect option content", text, value);
      knownContents.remove(objectId);
    }
    
    assertTrue("Items expected but not found: " + knownContents, knownContents.isEmpty());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  private void checkCommonRadioAttributes(Node aNode) {

    checkType(aNode, "div");
    checkCommonAttributes(aNode);
    
  }

  private void checkCommonCheckboxAttributes(Node aNode) {
  
    checkType(aNode, "div");
    checkCommonAttributes(aNode);
    
  }

  public void testMultiselectAttributes () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testMultiselectAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkMultiselectAttributes(node, "-- unspecified --");
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  private void checkScrollingAttributes(Node node, String unspecified) {

    checkAttribute(node, "size", "8");
    checkListAttributes(node, unspecified, getKnownContents());
  }

  private void checkListAttributes(Node node, String unspecified, HashMap knownContents) {

    checkCommonDefaultAttributes(node);
    boolean unspecifiedFound = unspecified == null;
    for (int i = 0; i < getElementChildCount(node); i++) {
      Node option = getNthElementChild(node, i);
      checkType(option, "option");
      String objectId = option.getAttributes().getNamedItem("value").getNodeValue();
      String value = option.getFirstChild().getNodeValue();
      if ( !unspecifiedFound && "-1".equals(objectId)) {
        assertEquals("Incorrect option content", unspecified, value);
        unspecifiedFound = true;
      }
      else {
        if (selectedObjectId().equals(objectId)) checkAttribute(option, "selected", "selected");
        String text = (String)knownContents.get(objectId);
        assertNotNull("Unexpected option - value: "+ objectId + " with content: " + value, text);
        assertEquals("Incorrect option content", text, value);
        knownContents.remove(objectId);
      }
    }
    if (unspecified == null) assertTrue("An 'unspecified' option was not found", unspecifiedFound);
    assertTrue("Items expected but not found: " + knownContents, knownContents.isEmpty());
  }

  public void testDefaultAttributesWithEmptyCollection () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testDefaultAttributesWithEmptyCollection.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    // Replaced the checkAttribute(...) line below with the following two lines,
    // as the checkAttribute line caused an error due to "Missing style".
    // checkAttribute(node, "style", "width: 10em");
    Node attribute = node.getAttributes().getNamedItem("style");
    assertNull(attribute);
    
    checkListAttributes(node, "-- unspecified --", new HashMap());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  public void testDefaultAttributes () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testDefaultAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkListAttributes(node, "-- unspecified --", getKnownContents());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }
  
  private String selectedObjectId() {

    return "5";
  }

  private HashMap getKnownContents() {

    HashMap contents = new HashMap();
    contents.put("10", "Test List Topic Three");
    contents.put("8", "Test List Topic Two");
    contents.put("6", "Test List Topic One");

    return contents;
  }

  private void checkCommonAttributes(Node node) {

    checkAttribute(node, "id", "ID");
    checkForExtraAttributes(node);
  }

  public void testDropdownAttributes () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testDropdownAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkListAttributes(node, "-- unspecified --", getKnownContents());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  public void testDropdownAttributesWithNone () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testDropdownAttributesWithNone.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkListAttributes(node, null, getKnownContents());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  public void testDropdownAttributesWithUnspecified () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testDropdownAttributesWithUnspecified.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkListAttributes(node, "-- NO SELECTION --", getKnownContents());
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  }

  public void testScrollingAttributes () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testScrollingAttributes.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkScrollingAttributes(node, "-- unspecified --");
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
    
  }

  public void testScrollingAttributesWithNone () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testScrollingAttributesWithNone.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkScrollingAttributes(node, null);
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  }

  public void testScrollingAttributesWithUnspecified () throws Exception {
    
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/ListTag/testScrollingAttributesWithUnspecified.jsp");
    WebForm form = resp.getForms()[0];
    Node node = getLastElementChild(form.getDOMSubtree());
    
    checkScrollingAttributes(node, "-- NO SELECTION --");
    
    //Submit the form to check that no problems occur
    form.submit();
    // Check for the correct forward
    assertEquals("Incorrect Result", webedTestApplication
        + "/test/defaultForward.html", wc.getCurrentPage().getURL().getPath());
  }

  /* Disabled because FormTag's velocity ignores readonly property
  public void testReadonlyTrue() throws Exception {
    WebResponse success = wc.getResponse(webedTestLocation
        + "/test/ListTag/testReadonly.jsp");
    assertTrue("Form element found in read-only page",
               success.getForms().length == 0);
    
    NodeList nodes = success.getDOM().getElementsByTagName("div");
    assertTrue("Wrong number of <div> elements: " + nodes.getLength(),
               nodes.getLength() == 1);
    Node node = nodes.item(0);
    String text = getElementContent((Element) node).trim();
    assertTrue("Bad text in element: '" + text + "'",
               text.equals("Test List Topic One"));
  }
  */
}
