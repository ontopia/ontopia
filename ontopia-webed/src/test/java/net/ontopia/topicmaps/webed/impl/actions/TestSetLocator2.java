
// $Id: TestSetLocator2.java,v 1.2 2009/04/27 11:08:58 lars.garshol Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.io.IOException;
import java.util.*;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.occurrence.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * Tests SetLocator2 by overriding the action creation from TestSetLocator
 * and the one test which behaves differently.
 */
public class TestSetLocator2 extends TestSetLocator {
  
  public TestSetLocator2(String name) {
    super(name);
  }

  public void setUp() {
    super.setUp();
    action = new SetLocator2();
  }

  // --- Actions with different behaviour
  
  public void testEmptyURL() throws IOException {
    TopicIF topic = getTopicById(tm, "tromso");
    OccurrenceIF occ = getOccurrenceWithLocator(topic);
    int occsbefore = topic.getOccurrences().size();
    
    ActionParametersIF params = makeParameters(occ, "");
    ActionResponseIF response = makeResponse();
    action.perform(params, response);

    int occsnow = topic.getOccurrences().size();
    assertTrue("Occurrence not deleted from parent topic",
               occsbefore == occsnow + 1);
  }
  
}
