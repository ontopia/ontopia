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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.webed.impl.utils.ActionResponseComposite;

/**
 * INTERNAL: Basic implementation of ActionGroupIF interface, for
 * storing a set of action objects belonging to a common group.
 */
public class ActionGroup implements ActionGroupIF {
  
  /**
   * The name which identifies this action group.
   */
  private String name;
  
  /**
   * Map for storing actions by their name
   * (key: String, value: ActionInGroup).
   */
  private Map actionsByName;

  /**
   * Stores actions in the order they are to be executed.
   */
  private List actionsInOrder;
  
  /**
   * Map for storing (key: Integer for action response type, value:
   * ActionForwardPageIF)
   */
  private Map defaultForwardPages;

  /**
   * The page to which is forwarded if a variable as specified by the
   * FormTag cannot be locked.
   */
  private ActionForwardPageIF lockedForwardPage;
  
  /**
   * Map for storing (key: ActionResponseComposite, value:
   * ActionForwardPageIF)
   */
  private Map forwardPages;

  /**
   * Default constructor.
   */
  public ActionGroup(String actionName) {
    name = actionName;
    actionsByName = new HashMap();
    actionsInOrder = new ArrayList();
    defaultForwardPages = new HashMap();
    forwardPages = new HashMap();
  }

  // -------------------------------------------------------------------
  // implementation of ActionGroupIF
  // -------------------------------------------------------------------
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public void addAction(ActionInGroup action) {
    actionsByName.put(action.getName(), action);
    actionsInOrder.add(action);
  }

  @Override
  public List getActions() {
    return actionsInOrder;
  }

  @Override
  public ActionInGroup getAction(String name) {
    return (ActionInGroup) actionsByName.get(name);
  }

  @Override
  public void setDefaultForwardPage(int responseType,
                                    ActionForwardPageIF forwardPage) {
    defaultForwardPages.put(new Integer(responseType), forwardPage);
  }
  
  @Override
  public ActionForwardPageIF getDefaultForwardPage(int responseType) {
    ActionForwardPageIF fw = (ActionForwardPageIF)
      defaultForwardPages.get(new Integer(responseType));
    if (fw == null)
      fw = (ActionForwardPageIF)
        defaultForwardPages.get(new Integer(Constants.FORWARD_GENERIC));
    return fw;
  }

  @Override
  public Map getDefaultForwardPages() {
    return defaultForwardPages;
  }

   
  @Override
  public void setLockedForwardPage(ActionForwardPageIF forwardPage) {
    this.lockedForwardPage = forwardPage;
  }
   
  @Override
  public ActionForwardPageIF getLockedForwardPage() {
     return lockedForwardPage;
  }

  
  @Override
  public void setForwardPage(ActionInGroup action,
                             int responseType,
                             ActionForwardPageIF forwardPage) {
    forwardPages.put(new ActionResponseComposite(action, responseType),
                 forwardPage);
  }

  @Override
  public ActionForwardPageIF getForwardPage(ActionInGroup action,
                                            boolean error) {
    int responseType = Constants.FORWARD_SUCCESS;
    if (error)
      responseType = Constants.FORWARD_FAILURE;
    ActionResponseComposite arc =
      new ActionResponseComposite(action, responseType);
    if (forwardPages.containsKey(arc))
      return (ActionForwardPageIF) forwardPages.get(arc);
    // second try to get forward page for generic response type
    arc = new ActionResponseComposite(action, Constants.FORWARD_GENERIC);
    if (forwardPages.containsKey(arc))
      return (ActionForwardPageIF) forwardPages.get(arc);
    // if nothing found, make that clear
    return null;
  }

  @Override
  public Map getForwardPages() {
    return forwardPages;
  }
  
  // --- overridden method(s) from Object implementation
  
  @Override
  public String toString() {
    return "[ActionGroup: name = " + name +
      ", actions = " + actionsByName +
      ", defaultForwardPages = " + defaultForwardPages +
      ", forwardPages = " + forwardPages + "]";
  }

  @Override
  public int hashCode() {
    return name.hashCode() + forwardPages.hashCode() +
      defaultForwardPages.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ActionGroupIF))
      return false;
    ActionGroupIF cmp = (ActionGroupIF) obj;
    return (name.equals(cmp.getName())
            && defaultForwardPages.equals(cmp.getDefaultForwardPages())
            && forwardPages.equals(cmp.getForwardPages()));
  }
  
}
