  
//$Id: TestSetSubjectIndicator2.java,v 1.2 2008/06/13 08:17:57 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.test;

import java.io.IOException;
import java.util.Collections;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.topic.SetSubjectIndicator2;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

public class TestSetSubjectIndicator2 extends TestSetSubjectIndicator {
  
  public TestSetSubjectIndicator2(String name) {
    super(name);
  }

  public void setUp() {
    super.setUp();
    action = new SetSubjectIndicator2();
  }

  // --- Tests

  public void testEmptyURL() throws IOException {
    TopicIF topic = getTopicById(tm, "gamst");
    LocatorIF newSL = new URILocator("http://www.slashdot.org");
    topic.addSubjectIdentifier(newSL);
    int sisbefore = topic.getSubjectIdentifiers().size();
    
    // execute
    ActionParametersIF params = makeParameters(makeList(topic, newSL), "");
    ActionResponseIF response = makeResponse();
    action.perform(params, response);
    
    // test
    int sisnow = topic.getSubjectIdentifiers().size();
    assertTrue("Subject indicator was not deleted",
               sisbefore == sisnow + 1);
  }
  
}
