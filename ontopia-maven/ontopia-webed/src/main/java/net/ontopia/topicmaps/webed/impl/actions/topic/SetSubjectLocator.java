
// $Id: SetSubjectLocator.java,v 1.8 2008/06/13 08:17:57 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.topic;

import java.net.MalformedURLException;
import java.util.Iterator;

import net.ontopia.utils.ObjectUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the locator address of the topics
 * subject locator.  If the passed value is an empty string or null
 * and there already exists a subject locator, then this is removed.
 *
 * @since 3.0
 */
public class SetSubjectLocator implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {

    // test params
    ActionSignature paramsType = ActionSignature.getSignature("t ls");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    Object param1 = params.get(1);

    String newAddress = params.getStringValue();

    LocatorIF locator = null;
    if (param1 instanceof LocatorIF)
      locator = (LocatorIF) param1;
    else if (param1 instanceof String) {
      try {
        locator = new URILocator((String) param1);
      } catch (MalformedURLException e) {
        throw new ActionRuntimeException("Malformed URL for subject locator: '"
            + param1 + "'", false);
      }
    }

    // verify that topic really has subject locator being changed
    if (!((locator == null && topic.getSubjectLocators().isEmpty()) ||
          topic.getSubjectLocators().contains(locator)))
      throw new ActionRuntimeException("The supplied locator '"
          + locator.getAddress() + "' is not valid for the topic", false);

    // ok, now do the modification
    try {
      LocatorIF newloc = null;
      if (newAddress != null && newAddress.trim().length() > 0)
        newloc = new URILocator(newAddress);

			// remove existing
			Iterator iter = topic.getSubjectLocators().iterator();
			while (iter.hasNext()) {
				LocatorIF oldloc = (LocatorIF)iter.next();
				if (ObjectUtils.equals(oldloc, newloc)) continue;
				topic.removeSubjectLocator(oldloc);
			}
			// add new
			if (newloc != null)
				topic.addSubjectLocator(newloc);
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for subject locator: '" +
                                       newAddress + "'", false);
    } catch (UniquenessViolationException e) {
      throw new ActionRuntimeException("Some other topic has the given subject"+
                                       " locator: '" + newAddress + "'", false);
    }
  }
}
