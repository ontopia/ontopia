//$Id: TestPeakAction.java,v 1.1 2005/02/23 12:53:02 ian Exp $

package net.ontopia.topicmaps.webed.actions;

import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;

import org.junit.Ignore;

/**
 * INTERNAL:
 * PRIVATE:
 * TESTING:
 */

@Ignore
public class TestPeakAction implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response)
      throws ActionRuntimeException {
  
    ActionParametersIF target = params.getRequest().getActionParameters("dummy");
    if (target == null) response.addParameter("value", "FAILURE: Failed to access 'dummy' action actions parameters");
    else response.addParameter("value",target.getStringValue());
  
  }

}
