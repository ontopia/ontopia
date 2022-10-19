/*
 * #!
 * Ontopia Access Control webapplication
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

package net.ontopia.topicmaps.nav2.webapps.accessctl;

import com.meterware.httpunit.Button;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import java.io.IOException;
import net.ontopia.topicmaps.webed.AbstractWebBasedTestCase;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * INTERNAL.
 * Tests the web application accessctl, located in:
 * src/java/j2ee/webapps/accessctl
 */
public class AccessctlTest extends AbstractWebBasedTestCase {

  private WebResponse resp;

  /**
   * Create a new accessctl test.
   * @param aName
   */
  public AccessctlTest(String aName) {
    super(aName);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    // different webapp default
    webedTestApplication = System.getProperty("net.ontopia.webed.test.testApplicationPath", "/accessctl");
    webedTestLocation = System.getProperty("net.ontopia.webed.test.testServerLocation", "http://127.0.0.1:8080") + webedTestApplication;
  }

  /**
   * Logs in and out.
   * @throws Exception
   */
  @Test
  public void testLogin() throws Exception {
    
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    login();
    clickOn("Logout");
  }
  
  /**
   * Navigates from index.jsp to password.jsp, changes the password and returns
   * to the password page to verify that the password was actually changed.
   * Initiates another password change, but then cancels the changes.
   * Changes the password to "secret" (required by other test cases).
   * @throws Exception
   */
  @Test
  public void testPasswordChange () throws Exception {
    getPage("index.jsp");
    
    // There should be six links.
    int linkCount = resp.getLinks().length;
    assertEquals("number of links", 6, linkCount);
    
    // Go to the password page, check that no default password is displayed,
    // and change it to "pencil".
    clickOn("Click here to change your password.");
    login();
    checkField("enterpw", "");
    changeField("enterpw", "pencil");
    clickButton("submit");
    
    // Go to the change password page check that no default password is
    // displayed, change the password to "pen", but cancel. Return to the change
    // password page and check that no default password was displayed
    clickOn("Click here to change your password.");
    checkField("enterpw", "");
    changeField("enterpw", "pen");
    clickButton("cancel");
    getPage("index.jsp");
    clickOn("Click here to change your password.");
    checkField("enterpw", "");
    changeField("enterpw", "secret");
    clickButton("submit");
  }
  
  /**
   * Tests the administrator main page (main.jsp), by inspectin the treewidget
   * (actually a table) and verifying that there's the corect number of rows in
   * it in collapsed and expanded form.
   * @throws Exception
   */
  @Test
  public void testMain() throws Exception {
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    login();
    
    // Check that the tree widget (in collapsed form) is of appropriate size.
    WebTable table = resp.getTables()[0];
    assertEquals( "rows", 5, table.getRowCount());
    assertEquals( "columns", 3, table.getColumnCount());
    
    // Expand the tree widget.
    WebLink link = table.getTableCell(0, 0).getLinks()[0];
    assertNotNull(link);
    link.click();
    resp = wc.getCurrentPage();
    
    // Check that the tree widget (in expanded form) is of appropriate size.
    table = resp.getTables()[0];
    assertEquals( "rows", 10, table.getRowCount());
    assertEquals( "columns", 3, table.getColumnCount());
    
  }
  
  /**
   * Tests manipulation of user groups.
   * @throws Exception
   */
  @Test
  public void testUserGroupChange () throws Exception {
    // Maintain old user group
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    login();
    clickOn("Maintain User Groups");
    clickButton("create");
    changeField("enterName", "Clay Users");
    clickButton("save");
    clickOn("Clay Users");
    
    // Change name save and check
    changeField("enterName", "Platinum Users");
    clickButton("save");
    clickOn("Platinum Users");
    
    // Verify that the privilege "Private Administrative Users" in row 1 is
    // unchecked, check it, verify that it has been checked, save and verify
    // that the check was saved.
    assertUnchecked("Private Administrative Users");
    setCheckbox(rowOf("Private Administrative Users"), true);
    assertChecked("Private Administrative Users");
    clickButton("save");
    clickOn("Platinum Users");
    assertChecked("Private Administrative Users");
    
    // Change name, cancel and check not updated.
    changeField("enterName", "Clay Users");
    clickButton("cancel");
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    clickOn("User Groups");
    clickButton(rowOfLink("Platinum Users"));
    
    // Create new, save, check and double check, delete and check deleted
    clickButton("create");
    changeField("enterName", "New Users");
    clickButton("save");
    clickOn("New Users");
    checkField("enterName", "New Users");
    clickButton("cancel");
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    clickOn("User Groups");
    
    // Check that a privilege exists. Delete it, and verify that it was deleted.
    assertNotNull(resp.getLinkWith("New Users"));
    clickButton(rowOfLink("New Users"));
    assertNull(resp.getLinkWith("New Users"));
    
    // Create new, cancel, check not created.
    clickButton("create");
    changeField("enterName", "Dummygroup");
    clickButton("cancel");
    assertNull(resp.getLinkWith("Dummygroup"));
  }
  
  /**
   * Gives and removes privileges to/from user groups.
   * Tests the use of checkboxes.
   * @throws Exception
   */
  @Test
  public void testPrivilegeCheckBox() throws Exception {
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    login();
    
    // Create the privilege "Live in Oslo"
    clickButton("createNewPrivilege");
    changeField("enterName", "Live in Oslo");
    changeField("enterSI", "http://Live.in.Oslo");
    clickButton("save");
    
    // Create the user group "Osloensere"
    clickOn("User Groups");
    clickButton("create");
    changeField("enterName", "Osloensere");
    
    // Give the privilege "Live in Oslo" and verify.
    assertUnchecked("Live in Oslo");
    setCheckbox(rowOf("Live in Oslo"), true);
    assertChecked("Live in Oslo");
    clickButton("save");
    clickOn("Osloensere");
    
    assertChecked("Live in Oslo");
    clickButton("cancel");
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    clickOn("Privileges");
    clickButton(rowOfLink("Live in Oslo"));
    clickOn("User Groups");
    clickButton(rowOfLink("Osloensere"));
  }
  
  /**
   * Tests manipulation of users.
   * @throws Exception
   */
  @Test
  public void testUserChange () throws Exception {
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    login();
    clickOn("Maintain Users");
    
    // Create new user.
    clickButton("create");
    changeField("enterName", "Mickey Mouse");
    changeField("enterUsername", "mickey mouse");
    changeField("enterPassword", "mikke mus");
    clickButton("save");
    clickOn("Mickey Mouse");
    
    // Check and change the values.
    checkField("enterUsername", "mickey mouse");
    checkField("enterPassword", "");
    changeField("enterName", "Mickenney Mouse");
    changeField("enterUsername", "mickenney mouse");
    changeField("enterPassword", "mikkel mus");
    clickButton("save");
    clickOn("Mickenney Mouse");

    // Change name, but cancel check that no change was made.
    changeField("enterName", "Mickey Mouse");
    clickButton("cancel");
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    clickOn("Users");
    clickOn("Mickenney Mouse");
    clickButton("cancel");
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    clickOn("Users");
    clickButton(rowOfLink("Mickenney Mouse"));
    
    // Create new user with name and password. Check that it was created.
    clickButton("create");
    checkField("enterName", "New User");
    checkField("enterUsername", "New User");
    checkField("enterPassword", "");
    changeField("enterName", "Mr. Nelson");
    changeField("enterPassword", "foo");
    clickButton("save");
    clickOn("Mr. Nelson");
    checkField("enterName", "Mr. Nelson");
    checkField("enterPassword", "");
    clickButton("cancel");
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    clickOn("Users");
    
    // Check that a privilege exists. Delete it, and verify that it was deleted.
    assertNotNull(resp.getLinkWith("Mr. Nelson"));
    clickButton(rowOfLink("Mr. Nelson"));
    assertNull(resp.getLinkWith("Mr. Nelson"));
    
    // Create new user, but cancel. Check that the user was not created.
    clickButton("create");
    changeField("enterName", "Dummy");
    changeField("enterPassword", "dum");
    clickButton("cancel");
    assertNull(resp.getLinkWith("Dummy"));
  }
  
  /**
   * Test manipulation of privileges.
   * @throws Exception
   */
  @Test
  public void testPrivilegeChange () throws Exception {
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    login();
    clickOn("Maintain Privileges");
    
    // Create new privilege.
    clickButton("create");
    changeField("enterName", "To be happy");
    changeField("enterSI", "http://to.be.happy");
    clickButton("save");
    clickOn("To be happy");
    
    // Change name.
    changeField("enterName", "To be cheerful");
    clickButton("save");
    clickOn("To be cheerful");
    
    // Change name, but cancel check that no change was made.
    changeField("enterName", "To be happy");
    clickButton("cancel");
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    clickOn("Privileges");
    clickButton(rowOfLink("To be cheerful"));
    
    // Create new privilege. Check that it was created.
    clickButton("create");
    changeField("enterName", "To get lunch");
    changeField("enterSI", "http://togetlunch.no");
    clickButton("save");
    clickOn("To get lunch");
    checkField("enterName", "To get lunch");
    
    // Modify privilege, but cancel.
    changeField("enterSI", "http://togetlunch.no");
    clickButton("cancel");
    getPage("index.jsp");
    clickOn("Click here to enter the Administrator Main Page.");
    clickOn("Privileges");
    clickButton(rowOfLink("To get lunch"));
    
    // Create new user, but cancel. Check that the user was not created.
    clickButton("create");
    changeField("enterName", "Dummylege");
    changeField("enterSI", "http://dummylege.no");
    clickButton("cancel");
    assertNull(resp.getLinkWith("Dummylege"));
    clickOn("Logout");
  }
    
  /**
   * Faced with the login form, perform the necessary steps to log in.
   * @throws Exception
   */
  protected void login() throws Exception {
    assertNotNull(resp.getElementWithID("name"));
    assertNotNull(resp.getElementWithID("passw"));
    changeField("name", "johndoe");
    changeField("passw", "secret");
    clickButton("submit");
  }
  
  /**
   * Make the page of a path relative to the test location the active page.
   * @param relativePath The path, relative to webedTestLocation.
   * @throws SAXException
   * @throws IOException
   */
  protected void getPage(String relativePath)
      throws SAXException, IOException {
    resp = wc.getResponse(webedTestLocation + "/" + relativePath);
  }
  
  /**
   * Click on the link with a given text.
   * @param linkText The text of the link.
   * @throws SAXException
   * @throws IOException
   */
  protected void clickOn(String linkText)
      throws SAXException, IOException {
    WebLink link = resp.getLinkWith(linkText);
 
    assertNotNull(link);
    link.click();
    resp = wc.getCurrentPage();
  }
  
  /**
   * Click on the button with a given ID and on the first form.
   * @param buttonID The id of the button to click.
   * @throws SAXException
   * @throws IOException
   */
  protected void clickButton(String buttonID) throws SAXException, IOException {
    WebForm forms[] = resp.getForms();
    assertTrue(forms.length > 0);
    WebForm form = forms[0];
    Button button = form.getButtonWithID(buttonID);
    assertNotNull(button);
    button.click();
    resp = wc.getCurrentPage();
  }
  
  /**
   * Change the contents of a given input field.
   * @param fieldID The ID of the field.
   * @param newValue The new value to put in the input field.
   * @throws SAXException
   */
  protected void changeField(String fieldID, String newValue)
      throws SAXException {
    WebForm forms[] = resp.getForms();
    WebForm form = forms[0];
    HTMLElement field = resp.getElementWithID(fieldID);
    assertNotNull(field);
    form.setParameter(field.getName(), newValue);
  }

  /**
   * Check the contents of a given input field against a control value.
   * @param fieldID The ID of the field to control.
   * @param controlValue The value to check the field contents against.
   * @throws SAXException
   */
  protected void checkField(String fieldID, String controlValue)
      throws SAXException {
    HTMLElement field = resp.getElementWithID(fieldID);
    assertNotNull(field);
    WebForm[] forms = resp.getForms();
    assertTrue("more than 0 forms", forms.length > 0);
    WebForm form0 = forms[0];
    String value = form0.getParameterValue(field.getName());
    assertEquals(fieldID, controlValue, value);
  }
  
  /**
   * Assert that the checkbox of a given row in the table must be checked.
   * The table is the first table of the page.
   * @param name Name of the checkbox (text printed next to it in table).
   * @param row The index number of the row in the table.
   * @throws SAXException
   */
  protected void assertChecked(String name, int row) throws SAXException {
    WebTable privilegeTable = resp.getTables()[0];
    assertNotNull(privilegeTable);
    String assignName = privilegeTable.getTableCell(row, 0)
        .getElementNames()[0];
    // The checkbox must be checked.
    assertEquals(name, "on",
        resp.getForms()[0].getParameterValue(assignName));
  }
  
  /**
   * Assert that a given checkbox of a given text is checked.
   * Assumes that the checkbox is next to the given text inside the first table.
   * @param name The text printed next to the checkbox.
   * @throws SAXException
   */
  protected void assertChecked(String name) throws SAXException {
    assertChecked(name, rowOf(name));
  }
  
  /**
   * Assert that the checkbox of a given row in the table must be unchecked.
   * The table is the first table of the page.
   * @param name Name of the checkbox (text printed next to it in table).
   * @param row The index number of the row in the table.
   * @throws SAXException
   */
  protected void assertUnchecked(String name, int row) throws SAXException {
    WebTable privilegeTable = resp.getTables()[0];
    assertNotNull(privilegeTable);
    String assignName = privilegeTable.getTableCell(row, 0)
        .getElementNames()[0];
    // The checkbox must be unchecked.
    assertEquals(name, null, resp.getForms()[0].getParameterValue(assignName));
  }

  /**
   * Assert that the checkbox of a given text is not checked.
   * Assumes that the checkbox is next to the given text inside the first table.
   * @param name The text printed next to the checkbox.
   * @throws SAXException
   */
  protected void assertUnchecked(String name) throws SAXException {
    assertUnchecked(name, rowOf(name));
  }
  
  /**
   * Return the first row of the table where the given text occurs in the 2nd
   * column. The table is the first table of the page.
   * @param text The text to look for in the second column of each row.
   * @return The first row where the text occurs.
   * @throws SAXException
   */
  protected int rowOf(String text) throws SAXException {
    WebTable table = resp.getTables()[0];
    assertNotNull(table);
    
    int row = 0;
    while (row < table.getRowCount() && !table.getTableCell(row, 1).asText()
        .equals(text))
      row ++;
        
    assertTrue("The text \"" + text + "\" was not found in any tablerow.",
        row < table.getRowCount());
    return row;
  }

  /**
   * Return the first row of the table where the given text occurs as text on
   * a link in the 1st column. The table is the first table of the page.
   * @param text The text of the link to search for in the 1st column.
   * @return The first row where a link with the text occurs.
   * @throws SAXException
   */
  protected int rowOfLink(String text) throws SAXException {
    WebTable table = resp.getTables()[0];
    assertNotNull(table);
    
    int row = 0;
    while (row < table.getRowCount() && table.getTableCell(row, 0)
        .getLinkWith(text) == null)
      row ++;
        
    assertTrue("A link with the text \"" + text + "\" was not found in any"
        + " tablerow.", 
        row < table.getRowCount());
    return row;
  }

  /**
   * Click the delete button with the given id on the 2nd form.
   * @param row The row of the delete button that should be clicked.
   * @throws SAXException
   * @throws IOException
   */
  protected void clickButton(int row) throws SAXException, IOException {
    WebForm forms[] = resp.getForms();
    assertTrue(forms.length > 0);
    WebForm form = forms[0];
    clickButton(form, row);
  }
  
  protected WebForm getForm(String formID) throws SAXException {
    WebForm form = resp.getFormWithID(formID);
    assertNotNull(form);
    return form;
  }
  
  protected void clickButton(WebForm form, int row) throws SAXException, IOException {
    Button buttons[] = form.getButtons();
    assertTrue(buttons.length > row + 1);
    Button button = buttons[row + 1];
    button.click();
    resp = wc.getCurrentPage();
  }

  /**
   * Set the state of the checkbox of the given row in the 1st column of the
   * table. The table is the first table of the current page.
   * @param row The row of the textbox.
   * @param value The new state of the checkbox.
   * @throws SAXException
   */
  protected void setCheckbox(int row, boolean value) throws SAXException {
    WebTable tables[] = resp.getTables();
    assertTrue(tables.length > 0);
    WebTable table = tables[0];
    assertNotNull(table);
    String assignName = table.getTableCell(row, 0)
        .getElementNames()[0];
    resp.getForms()[0].setCheckbox(assignName, value);
  }
}
