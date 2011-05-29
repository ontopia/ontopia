
package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import java.net.MalformedURLException;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for adding a source locator to an object.
 */
public class AddSourceLocator implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("x");
    paramsType.validateArguments(params, this);

    TMObjectIF object = (TMObjectIF) params.get(0);
    String value = params.getStringValue().trim();
    if (value == null || value.equals(""))
      return;

    if (value.equals(Constants.RPV_DEFAULT))
      value = Constants.DUMMY_LOCATOR;
    
    try {
      object.addItemIdentifier(new URILocator(value));
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for source locator: '" + value + "'", false);
    } catch (UniquenessViolationException e) {
      throw new ActionRuntimeException("Some other topic map object has the given source locator: '" + value + "'", false);
    }

  }
  
}
