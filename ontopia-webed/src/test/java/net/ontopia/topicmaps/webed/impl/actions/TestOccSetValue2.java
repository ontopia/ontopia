
// $Id: TestOccSetValue2.java,v 1.1 2006/01/04 15:27:54 larsga Exp $

package net.ontopia.topicmaps.webed.impl.actions;

import java.io.IOException;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.occurrence.*;
import net.ontopia.topicmaps.core.*;

public class TestOccSetValue2 extends TestOccSetValue {
  
  public TestOccSetValue2(String name) {
    super(name);
  }

  public void setUp() {
    super.setUp();
    action = new SetValue2();
  }
 
  public void testEmptyValue() throws IOException {
    // get ready
    TopicIF topic = getTopicById(tm, "tromso");
    OccurrenceIF occ = getOccurrenceWithValue(topic);
    int occsbefore = topic.getOccurrences().size();
    
    // run action
    ActionParametersIF params = makeParameters(makeList(occ, topic), "");
    ActionResponseIF response = makeResponse();
    action.perform(params, response);

    // test post-action state
    int occsnow = topic.getOccurrences().size();
    assertTrue("Occurrence was not deleted", occsbefore == occsnow + 1);
    assertTrue("Wrong occurrence deleted",
               occ.getTopic() == null);
  } 
}
