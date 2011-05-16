
//$Id: TestAction.java,v 1.2 2006/05/25 14:02:13 larsga Exp $

package net.ontopia.topicmaps.webed.actions;

import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;

import org.junit.Ignore;

/**
 * INTERNAL.
 */
@Ignore
public class TestAction implements ActionIF {

    public void perform(ActionParametersIF params, ActionResponseIF response)
        throws ActionRuntimeException {
      response.addParameter("result", "SUCCESS");
    }
  
}
