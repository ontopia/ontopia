
package net.ontopia.topicmaps.webed.impl.actions.topic;

import java.net.MalformedURLException;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for adding a subject indicator to a topic.
 */
public class AddSubjectIndicator implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    
    ActionSignature paramsType = ActionSignature.getSignature("t");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    String untrimmedValue = params.getStringValue();
    if (untrimmedValue == null || untrimmedValue.equals(""))
      return;
    String value = untrimmedValue.trim();

    if (value.equals(Constants.RPV_DEFAULT))
      value = Constants.DUMMY_LOCATOR;
    
    try {
      topic.addSubjectIdentifier(new URILocator(value));
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for subject identifier: '" + value + "'", false);
    } catch (UniquenessViolationException e) {
      throw new ActionRuntimeException("Some other topic has the given subject identifier: '" + value + "'", false);
    }

  }
  
}
