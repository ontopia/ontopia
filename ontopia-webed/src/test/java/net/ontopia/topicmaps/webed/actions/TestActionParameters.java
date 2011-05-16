//$Id: TestActionParameters.java,v 1.2 2008/06/13 08:36:30 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.actions;

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.utils.OntopiaRuntimeException;

import org.junit.Ignore;

/**
 * INTERNAL: PRIVATE: TESTING:
 */

@Ignore
public class TestActionParameters implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response)
      throws ActionRuntimeException {
  
    TopicMapIF tm = (TopicMapIF) params.get(0);
    TopicIF topic = (TopicIF) params.get(1);
    String string = (String) params.get(2);
    Object empty = params.get(3);
  
    LocatorIF testLocator = null;
    try {
      testLocator = new URILocator("http://psi.ontopia.net/test/test-topic");
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  
    TopicIF actualTopic = tm.getTopicBySubjectIdentifier(testLocator);
    if (actualTopic != topic) {
      response.addParameter("result", "FAILURE: Incorrect topic passed as parameter - got: "
          + actualTopic.toString() + ", expected: " + topic.toString());
      return;
    }
  
    if (!string.equals("STRING")) {
      response.addParameter("result", "FAILURE: Expected string parameter 'STRING' but got: "
          + string.toString());
      return;
    }
    
    if (empty != null) {
      response.addParameter("result", "FAILURE: Expected empty parameter but got: "
          + empty.toString());
      return;
    }
    
    response.addParameter("result", "SUCCESS");
    
  }
}
