
// $Id: SetActionGroup.java,v 1.3 2003/12/22 19:16:09 larsga Exp $

package net.ontopia.topicmaps.webed.webapps.omnieditor;

import java.util.Iterator;
import java.util.Collection;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;

/**
 * INTERNAL.
 */
public class SetActionGroup implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {

    // check if there was a value
    String value = params.getStringValue();
    if (value == null || value.trim().equals(""))
      return;

    response.addParameter(Constants.RP_SHOW_AG, value);
  }
  
}
