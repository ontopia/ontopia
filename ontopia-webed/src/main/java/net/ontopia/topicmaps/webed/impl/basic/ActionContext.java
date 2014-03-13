/*
 * #!
 * Ontopia Webed
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.webed.impl.basic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;
import net.ontopia.topicmaps.webed.impl.utils.Parameters;
import net.ontopia.utils.DebugUtils;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Implementation of the ActionContextIF interface. This
 * class serves mainly as a wrapper class for
 * <code>javax.servlet.ServletRequest</code> to store to maps (one for
 * parameter value pairs and one for attribute value pairs).
 */
public class ActionContext implements ActionContextIF {

  // --- define a logging category.
  private static Logger log = LoggerFactory.getLogger(ActionContext.class.getName());
  
  protected Parameters params;
  protected UserIF user;

  public ActionContext(UserIF user, Parameters params) {
    this.user = user;
    this.params = params;
    if (log.isDebugEnabled())
      log.debug("New ActionContext object with params: " + stringifyParams());
  }
  
  // -------------------------------------------------------------
  // implement ActionContextIF methods
  // -------------------------------------------------------------

  public UserIF getUser() {
    return user;
  }
  
  public Map getParameters() {
    return params.getMap();
  }

  public String[] getParameterValues(String paramName) {
    if (log.isDebugEnabled()) {
      log.debug("getParameterValues("+paramName+"): "+params.getValues(paramName));
    }
    return params.getValues(paramName);
  }

  public String getParameterSingleValue(String paramName) {
    if (params.getValues(paramName) == null)
      return null;
    String[] values = params.getValues(paramName);
    if (values.length != 1)
      throw new IllegalArgumentException("Required exactly one value for parameter: "+
                                         paramName + ", but got "  + values.length +
                                         " : " + DebugUtils.toString(values));
    return values[0];
  }
  
  public Collection getParameterNames() {
    return params.getNames();
  }

  public Collection getAllActions() {
    String request_id = params.get(Constants.RP_REQUEST_ID);
    if (request_id == null)
      throw new OntopiaRuntimeException("No request id parameter included in ProcessServlet request");
    log.debug("Found request id: " + request_id);

    Object bundle = user.getWorkingBundle(request_id);

    if (bundle == null)
      throw new NoActionDataFoundException("No action data found for request " + request_id + ", request either corrupt or expired.");

    if (bundle instanceof ActionData)
      return Collections.singleton(bundle);
    else
      return ((ActionDataSet)bundle).getAllActionData();
  }

  // -------------------------------------------------------------
  // overwrite Object methods
  // -------------------------------------------------------------
  
  public int hashCode() {
    return params.hashCode();
  }

  public String toString() {
    return "[Params: " + params + "]";
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof ActionContext))
      return false;
    ActionContext cmp = (ActionContext) obj;
    Iterator it = params.getNames().iterator();
    while (it.hasNext()) {
      String paramName = (String) it.next();
      String[] parValues = params.getValues(paramName);
      String[] cmpValues = cmp.getParameterValues(paramName);
      if (!Arrays.equals(parValues, cmpValues))
        return false;
    }
    return true;
  }

  // -------------------------------------------------------------
  // internal helper method(s)
  // -------------------------------------------------------------

  private String stringifyParams() {
    StringBuilder sb = new StringBuilder(89);
    Iterator it_params = params.getNames().iterator();
    while (it_params.hasNext()) {
      String name = (String) it_params.next();
      String[] vals = params.getValues(name);
      sb.append(name).append(": ");
      for (int i=0; i < vals.length; i++) {
        sb.append("'").append(vals[i]).append("'");
        if (i < vals.length-1)
          sb.append(", ");
      }
      sb.append("; ");
    }
    return sb.toString();
  }
  
}
