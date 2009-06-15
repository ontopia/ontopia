
// $Id: TestSetSubjectLocator.java,v 1.2 2008/06/13 08:17:57 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.test;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.net.MalformedURLException;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.topic.SetSubjectLocator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.infoset.core.LocatorIF;

public class TestSetSubjectLocator extends AbstractWebedTestCase {
  private ActionIF action;           // saves typing
  private ActionResponseIF response; // ditto
  private TopicIF topic;             // ditto
  
  public TestSetSubjectLocator(String name) {
    super(name);
  }

  public void setUp() {
    super.setUp();
    action = new SetSubjectLocator();
    response = makeResponse();
    topic = getTopicById(tm, "gamst");    
  }

  // --- Tests

  public void testAddLocator() throws MalformedURLException {
    // run action
    List plist = new ArrayList();
    plist.add(Collections.singleton(topic));
    plist.add(Collections.EMPTY_SET);
    ActionParametersIF params = makeParameters(plist, "http://www.example.com");
    action.perform(params, response);

    // test
    assertTrue("Topic got no subject locator", topic.getSubjectLocators().size() > 0);
    assertTrue("Topic got wrong subject locator",
               topic.getSubjectLocators().contains(new URILocator("http://www.example.com")));
  }

  public void testChangeLocator() throws MalformedURLException {
    // add locator to topic
    LocatorIF oldloc = new URILocator("urn:x-test:old");
    topic.addSubjectLocator(oldloc);
    
    // run action
    ActionParametersIF params =
      makeParameters(makeList(topic, oldloc), "http://www.example.com");
    action.perform(params, response);

    // test
    assertTrue("Topic got no subject locator", topic.getSubjectLocators().size() > 0);
    assertTrue("Topic got wrong subject locator",
               topic.getSubjectLocators().contains(new URILocator("http://www.example.com")));
  }

  public void testBadURL() throws MalformedURLException {
    // add locator to topic
    LocatorIF oldloc = new URILocator("urn:x-test:old");
    topic.addSubjectLocator(oldloc);
    
    // run action
    try {
      ActionParametersIF params =
        makeParameters(makeList(topic, oldloc), "dette er ikke noen url");
      action.perform(params, response);
      fail("Bad URL accepted");
    } catch (ActionRuntimeException e) {
      assertFalse("Got critical exception", e.getCritical());
    }
  }

  public void testBadURLNoSubject() throws MalformedURLException {
    // run action
    try {
      List plist = new ArrayList();
      plist.add(Collections.singleton(topic));
      plist.add(Collections.EMPTY_SET); 
      ActionParametersIF params = makeParameters(plist, "dette er ingen url");
      action.perform(params, response);
      fail("Bad URL accepted");
    } catch (ActionRuntimeException e) {
      assertFalse("Got critical exception", e.getCritical());
    }
  }

  public void testChangeNonExistentURL() throws MalformedURLException {
    // add locator to topic
    LocatorIF oldloc = new URILocator("urn:x-test:old");
    topic.addSubjectLocator(oldloc);
    LocatorIF paramloc = new URILocator("urn:x-test:param");
    
    // run action
    try {
      ActionParametersIF params = 
        makeParameters(makeList(topic, paramloc), "urn:x-test:new");
      action.perform(params, response);
      fail("Was allowed to change non-existent locator");
    } catch (ActionRuntimeException e) {
      assertFalse("Got critical exception", e.getCritical());
    }
  }

  public void testChangeNonExistentURL2() throws MalformedURLException {
    // create parameter locator
    LocatorIF paramloc = new URILocator("urn:x-test:param");
    
    // run action
    try {
      ActionParametersIF params = 
        makeParameters(makeList(topic, paramloc), "urn:x-test:new");
      action.perform(params, response);
      fail("Was allowed to change non-existent locator");
    } catch (ActionRuntimeException e) {
      assertFalse("Got critical exception", e.getCritical());
    }
  }

  public void testEmptyURL() throws MalformedURLException {
    // add locator to topic
    LocatorIF oldloc = new URILocator("urn:x-test:old");
    topic.addSubjectLocator(oldloc);
    
    // run action
    ActionParametersIF params = makeParameters(makeList(topic, oldloc), "");
    action.perform(params, response);

    // test
    assertTrue("Subject locator not removed", topic.getSubjectLocators().isEmpty());
  }

  public void testEmptyURLNoSubject() throws MalformedURLException {
    // topic has no subject locator
    
    // run action
    List plist = new ArrayList();
    plist.add(Collections.singleton(topic));
    plist.add(Collections.EMPTY_SET); 
    ActionParametersIF params = makeParameters(plist, "");
    action.perform(params, response);

    // test
    assertTrue("Topic now has subject locator", topic.getSubjectLocators().isEmpty());
  }

}
