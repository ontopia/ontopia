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


import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;

/**
 * INTERNAL.
 */
public class LinkTagTest extends AbstractWebBasedTestCase {

  public LinkTagTest(String aName) {
    super(aName);
  }

  /**
   * Tests the usage of the <webed:link> tag.
   * Tests that the tag submits the form and that it forwards to the 
   * correct page.
   */
  public void testLinkTag() throws Exception {
    // First remove topics that may give the test false positives.
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/LinkTag/testLinkTagCleanup.jsp");
    WebForm form = resp.getForms()[0];
    form.submit();
    
    // Go to the test page.
    resp = wc.getResponse(webedTestLocation
        + "/test/LinkTag/testLinkTag.jsp");
    // Make sure that no topics that could cause false positives exist.
    assertTrue("testLinkTag topic already exists before the test." , 
        wc.getCurrentPage().getText().indexOf("Topics: {}") >= 0);

    // Click the link.
    resp.getLinkWith("add topic with name \"testLinkTag\".").click();
    
    // Get the new page.
    resp = wc.getCurrentPage();
    // Check that one topic was created (and given name) as required.
    assertTrue("testLinkTag topic was not created by test." + wc.getCurrentPage().getText(), 
        wc.getCurrentPage().getText().indexOf("Topics: {1}") >= 0);
  }

  public void cleanme() throws Exception {
    // First remove topics that may give the test false positives.
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/LinkTag/testLinkTagCleanup.jsp");
    WebForm form = resp.getForms()[0];
    form.submit();
  }

  public void testReadonlyTrue() throws Exception {
    // First remove topics that may give the test false positives.
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/LinkTag/testLinkTagCleanup.jsp");
    WebForm form = resp.getForms()[0];
    form.submit();
    
    // Go to the test page.
    resp = wc.getResponse(webedTestLocation
        + "/test/LinkTag/testReadonlyTrue.jsp");
    // Make sure that no topics that could cause false positives exist.
    assertTrue("testLinkTag topic already exists before the test." , 
        wc.getCurrentPage().getText().indexOf("Topics: {}") >= 0);

    // Click the link.
    resp.getLinkWith("add topic with name \"testLinkTag\".").click();
    
    // Get the new page.
    resp = wc.getCurrentPage();
    // Check that one topic was created (and given name) as required.
    assertFalse("webed:link element ran action on readonly form.", 
        wc.getCurrentPage().getText().indexOf("Topics: {1}") >= 0);
  }
  
  public void testReadonlyTrueLinkFalse() throws Exception {
    // First remove topics that may give the test false positives.
    WebResponse resp = wc.getResponse(webedTestLocation
        + "/test/LinkTag/testLinkTagCleanup.jsp");
    WebForm form = resp.getForms()[0];
    form.submit();
    
    // Go to the test page.
    resp = wc.getResponse(webedTestLocation
        + "/test/LinkTag/testReadonlyTrueLinkFalse.jsp");
    // Make sure that no topics that could cause false positives exist.
    assertTrue("testLinkTag topic already exists before the test." , 
        wc.getCurrentPage().getText().indexOf("Topics: {}") >= 0);

    // Click the link.
    resp.getLinkWith("add topic with name \"testLinkTag\".").click();
    
    // Get the new page.
    resp = wc.getCurrentPage();
    // Check that one topic was created (and given name) as required.
    assertTrue("webed:link element with readonly=\"false\" didn't run action " +
        "on readonly form.", 
        wc.getCurrentPage().getText().indexOf("Topics: {1}") >= 0);
  }
}
