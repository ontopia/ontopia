
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
public class TestValueAction implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response)
      throws ActionRuntimeException {

    String value = params.getStringValue();
    if (value==null) value = (String) params.get(0);
    String param = response.getParameter("value");
    String result;
    if (param==null) result = value;
    else result = param + "-" + value;
    
    if (result != null) response.addParameter("value", result);

  }

}
