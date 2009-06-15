
// $Id: SetPassword.java,v 1.14 2008/06/13 08:36:30 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.WebEdRequestIF;

/**
 * PUBLIC: Action that sets the password of a user, confirming it
 * against another field in the form to ensure that it was typed
 * correctly by the user. Assumes the use of the userman PSI set.
 *
 * @since 2.0
 */
public class SetPassword implements ActionIF {
  private static final String PSI_URI = "http://psi.ontopia.net/userman/password";
  private LocatorIF psi;
  
  public SetPassword() {
    try {
      psi = new URILocator(PSI_URI);
    } catch (java.net.MalformedURLException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    }
  }

  public void perform(ActionParametersIF params, ActionResponseIF response) {

    String password = params.getStringValue().trim();
    if (password == null || password.equals(""))
      return; // if it's not set, don't do anything
    
    TopicIF topic = (TopicIF) params.get(0);
    String otherfield = ((String) params.get(1)).trim();
    String forwardto = (String) params.get(2);

    TopicMapIF topicmap = topic.getTopicMap();
    if (topicmap == null)
      return; // means the topic was deleted...
    TopicIF passwd = getPasswordTopic(topicmap);

    OccurrenceIF passwdocc = null;
    
    Iterator it = topic.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      if (passwd.equals(occ.getType())) {
        passwdocc = occ;
        break;
      }
    }

    // verify that user typed password correctly
    WebEdRequestIF request = params.getRequest();
    ActionParametersIF otherpm = request.getActionParameters(otherfield);
    String confirm = otherpm.getStringValue().trim();

    if (!password.equals(confirm)) {
      request.getUser().addLogMessage("Passwords were inconsistent. " +
                                      "Please try again.");
      if (forwardto != null)
        response.setForward(forwardto);
      return;
    }

    if (passwdocc == null)
      passwdocc = topicmap.getBuilder().makeOccurrence(topic, passwd, password);
    else if (password.equals(passwdocc.getValue())) {
      request.getUser().addLogMessage("Password set to existing password. " +
                                      "Please try again.");
      if (forwardto != null)
        response.setForward(forwardto);
      return;
    }
    
    passwdocc.setValue(password);
  }

  // Internals

  private TopicIF getPasswordTopic(TopicMapIF topicmap) {
    TopicIF passwd = topicmap.getTopicBySubjectIdentifier(psi);
    if (passwd == null) {
      passwd = topicmap.getBuilder().makeTopic();
      passwd.addSubjectIdentifier(psi);
    }
    return passwd;
  }
  
}
