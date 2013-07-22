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

//import net.ontopia.topicmaps.webed.test.AbstractWebBasedTestCase;

import com.meterware.httpunit.Button;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebResponse;
import java.io.IOException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import net.ontopia.utils.StringUtils;
import net.ontopia.topicmaps.webed.AbstractWebBasedTestCase;
import org.junit.Test;

/**
 * INTERNAL.
 * Tests the web application accessctl, located in:
 * src/java/j2ee/webapps/accessctl
 * Visits the index.jsp webpage and from there navigates from webpage to
 * webpage, sometimes changing fields and checkboxes on those webpages.
 * Stops after visiting as many webpages as specified by VISIT_LIMIT.
 */
public class AccessctlRandomizedTest extends AbstractWebBasedTestCase {
  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(AccessctlRandomizedTest.class.getName());

  // The number of webpages visited so far.
  protected int visitCount;
  // The maximum number of webpages to visit.
  public static int VISIT_LIMIT = 100000;
  
  // The number of privileges, users and user groups that was last seen.
  protected int privileges;
  protected int users;
  protected int userGroups;
  
  // Decides what do do next at any given moment (e.g. which link to click).
  protected Random random;
  
  private WebResponse resp;

  /**
   * Create a new tester.
   * @param aName
   */
  public AccessctlRandomizedTest(String aName) {
    super(aName);
    visitCount = 0;
    random = new Random();
    log.debug("Started");
    privileges = 0;
    users = 0;
    userGroups = 0;
  }

  protected void setUp() throws Exception {
    super.setUp();
    // different webapp default
    webedTestApplication = System.getProperty("net.ontopia.webed.test.testApplicationPath", "/accessctl");
    webedTestLocation = System.getProperty("net.ontopia.webed.test.testServerLocation", "http://127.0.0.1:8080") + webedTestApplication;
  }
  
  /**
   * Moves from webpage to webpage, possibly filling modifying fields and
   * checkboxes on those pages. Stops after visiting VISIT_LIMIT webpages.
   * @throws IOException
   * @throws SAXException
   */
  @Test
  public void testAtRandom () throws IOException, SAXException {
    // Maintain old user group
    getPage("index.jsp");
    while (visitCount <= VISIT_LIMIT) 
      visitCurrentPage();
    log.debug("Finished");
  }
  
  /**
   * Choose which actions to take for the current page based on its URL.
   * Also keep track of how many webpages have been visited.
   * @throws IOException
   * @throws SAXException
   */
  public void visitCurrentPage () throws IOException, SAXException {
    visitCount++;
    String pageName = resp.getURL().getPath().toString();
    pageName = pageName.substring(pageName.lastIndexOf('/') + 1);

    log.debug("Visiting " + pageName);
    if (pageName.equals("index.jsp"))
      visitIndex();
    else if (pageName.equals("main.jsp"))
      visitMain();
    else if (pageName.equals("password.jsp"))
      visitPassword();
    else if (pageName.equals("privilege.jsp"))
      visitPrivilege();
    else if (pageName.equals("privileges.jsp"))
      visitPrivileges();
    else if (pageName.equals("user.jsp"))
      visitUser();
    else if (pageName.equals("users.jsp"))
      visitUsers();
    else if (pageName.equals("userGroup.jsp"))
      visitUserGroup();
    else if (pageName.equals("userGroups.jsp"))
      visitUserGroups();
    else
      log.debug("Don't recognize " + pageName);
  }
  
  /**
   * 
   * @throws IOException
   * @throws SAXException
   */
  public void visitIndex () throws IOException, SAXException {
    if (random.nextBoolean())
      clickLinkNumber(0);
    else
      clickRandomLink();
    login();
  }

  /**
   * @throws IOException
   * @throws SAXException
   */
  public void visitMain () throws IOException, SAXException {
    log.debug("Main - Links: " + resp.getLinks().length);
    int choice = random.nextInt(100);
    
    if (choice < 30)
      clickButton("privilegeCreation", "createNewPrivilege");
    else if (choice < 60)
      clickButton("userCreation", "createNewUser");
    else if (choice < 90)
      clickButton("userGroupCreation", "createNewUserGroup");
    else
      clickRandomLinkOrButton();  
  }
  
  /**
   * @throws IOException
   * @throws SAXException
   */
  public void visitPassword () throws IOException, SAXException {  
    clickRandomButton();  
  }

  /**
   * @throws IOException
   * @throws SAXException
   */
  public void visitPrivilege () throws IOException, SAXException {  
    changeField("enterName", StringUtils.makeRandomId(2 + random.nextInt(10)));
    changeField("enterSI", StringUtils.makeRandomId(2 + random.nextInt(4)) + "://"
        + StringUtils.makeRandomId(2 + random.nextInt(15)));

    clickRandomButton();
  }
  
  /**
   * @return
   * @throws SAXException
   */
  public int getButtonCount() throws SAXException {
    WebForm forms[] = resp.getForms();
    assertTrue(forms.length > 0);
    WebForm form = forms[0];
    return form.getButtons().length;
  }

  /**
   * @throws IOException
   * @throws SAXException
   */
  public void visitPrivileges () throws IOException, SAXException {
    int buttonCount = getButtonCount();
    if (buttonCount > privileges) {
      log.debug("Privileges: " + ": " + privileges);
      privileges = buttonCount;
    }

    int choice = random.nextInt(100);
    if (choice < 95)
      clickButton("create");
    else
      clickRandomLinkOrButton();  
  }

  /**
   * @throws IOException
   * @throws SAXException
   */
  public void visitUser () throws IOException, SAXException {
    if (random.nextBoolean())
      changeField("enterName", StringUtils.makeRandomId(2 + random.nextInt(10)));
    if (random.nextBoolean())
      changeField("enterUsername", StringUtils.makeRandomId(2 + random.nextInt(10)));
    if (random.nextBoolean())
      changeField("enterPassword", StringUtils.makeRandomId(2 + random.nextInt(10)));
    
    clickRandomButton();  
  }

  /**
   * @throws IOException
   * @throws SAXException
   */
  public void visitUsers () throws IOException, SAXException {  
    int buttonCount = getButtonCount();
    if (buttonCount > users) {
      log.debug("Users: " + ": " + users);
      users = buttonCount;
    }

    int choice = random.nextInt(100);
    if (choice < 95)
      clickButton("create");
    else
      clickRandomLinkOrButton();  
  }

  /**
   * @throws IOException
   * @throws SAXException
   */
  public void visitUserGroup () throws IOException, SAXException {  
    if (random.nextBoolean())
      changeField("enterName", StringUtils.makeRandomId(2 + random.nextInt(10)));
    
    clickRandomButton();  
  }

  /**
   * @throws IOException
   * @throws SAXException
   */
  public void visitUserGroups () throws IOException, SAXException {  
    int buttonCount = getButtonCount();
    if (buttonCount > userGroups) {
      log.debug("User Groups: " + ": " + userGroups);
      userGroups = buttonCount;
    }

    int choice = random.nextInt(100);
    if (choice < 95)
      clickButton("create");
    else
      clickRandomLinkOrButton(); 
  }

  /**
   * @throws IOException
   * @throws SAXException
   */
  public void clickRandomLinkOrButton () throws IOException, SAXException {
    if (random.nextBoolean()) 
      clickRandomLink();
    else 
      clickRandomButton();
  }
  
  /**
   * Clicks a random link on the current page.
   * @throws Exception
   */
  public void clickRandomLink () throws IOException, SAXException {
    WebLink links[] = resp.getLinks();
    int choice = random.nextInt(links.length);
    WebLink link = links[choice];
    if (!(link == null || link.getURLString().startsWith("http://"))) {
      log.debug("Clicking Link: " + link.asText());
      clickFiltered(link);
    }
  }

  /**
   * @param link
   * @throws IOException
   * @throws SAXException
   */
  public void clickFiltered(WebLink link) throws IOException, SAXException {
    String text = link.asText();
    if (!(text.equals("John Doe") || text.equals("Gold Users") 
        || text.equals("Private Administrative Users"))) {
      log.debug("Clicing Link: " + text);
      link.click();
      resp = wc.getCurrentPage();
    }
  }
  
  /**
   * Clicks the link at a given index position.
   * @param index The index position of the link.
   * @throws IOException
   * @throws SAXException
   */
  public void clickLinkNumber (int index) throws IOException, SAXException {
    log.debug("Clicking link number: " + index);
    WebLink links[] = resp.getLinks();
    log.debug("Links: " + links.length);
    if (index >= links.length)
      index = links.length - 1;
    WebLink link = links[index];
    log.debug("Index: " + index + " text: " + link.getName());
    if (!(link == null || link.getURLString().startsWith("http://"))) {
      clickFiltered(link);
    }
  }
  
  /**
   * @throws IOException
   * @throws SAXException
   */
  public void clickRandomButton () throws IOException, SAXException {
    WebForm forms[] = resp.getForms();
    WebForm form = forms[random.nextInt(forms.length)];
    Button buttons[] = form.getButtons();
    Button button = buttons[random.nextInt(buttons.length)];
    button.click();
    log.debug("Clicked Button: " + button.getID());
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
    HTMLElement field = resp.getElementWithID(fieldID);
    assertNotNull(field);
    WebForm forms[] = resp.getForms();
    assertTrue(forms.length > 0);
    WebForm form = forms[0];
    form.setParameter(field.getName(), newValue);
    log.debug("Changed Field: fieldID=" + fieldID + " newValue=" + newValue);
  }

  /**
   * Click on the link with a given text.
   * @param linkText The text of the link.
   * @throws SAXException
   * @throws IOException
   */
  protected void clickOn(String linkText) throws SAXException, IOException {
    WebLink link = resp.getLinkWith(linkText);
 
    assertNotNull(link);
    clickFiltered(link);
  }
  
  /**
   * Click on the button with a given ID within the first form.
   * @param buttonID The id of the button to click.
   * @throws SAXException
   * @throws IOException
   */
  protected void clickButton(String buttonID) throws SAXException, IOException {
    log.debug("Clicking Button: " + buttonID);
    WebForm forms[] = resp.getForms();
    assertTrue(forms.length > 0);
    WebForm form = forms[0];
    Button button = form.getButtonWithID(buttonID);
    assertNotNull(button);
    button.click();
    resp = wc.getCurrentPage();
  }
  
  /**
   * Click on the button with a given ID within a given form.
   * @param formID The id of the form with the button.
   * @param buttonID The id of the button to click.
   * @throws SAXException
   * @throws IOException
   */
  protected void clickButton(String formID, String buttonID) throws SAXException, IOException {
    log.debug("Clicking Button: " + buttonID + " formID=" + formID);
    WebForm form = resp.getFormWithID(formID);
    assertNotNull(form);
    Button button = form.getButtonWithID(buttonID);
    assertNotNull(button);
    button.click();
    resp = wc.getCurrentPage();
  }

  /**
   * Make the page of a path relative to the test location the active page.
   * @param relativePath The path, relative to webedTestLocation.
   * @throws SAXException
   * @throws IOException
   */
  protected void getPage(String relativePath) throws SAXException, IOException {
    resp = wc.getResponse(webedTestLocation + "/" + relativePath);
  }
  
  /**
   * Faced with the login form, perform the necessary steps to log in.
   * @throws IOException
   * @throws SAXException
   */
  protected void login() throws IOException, SAXException {
    // If required to log in, then do so.
    if (resp.getElementWithID("name") != null && resp.getElementWithID("passw") != null) {
      changeField("name", "johndoe");
      changeField("passw", "secret");
      clickButton("submit");
    }
  }
}
