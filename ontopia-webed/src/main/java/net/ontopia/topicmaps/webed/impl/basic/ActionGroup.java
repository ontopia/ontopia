
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
  
  public String getName() {
    return name;
  }
  
  public void addAction(ActionInGroup action) {
    actionsByName.put(action.getName(), action);
    actionsInOrder.add(action);
  }

  public List getActions() {
    return actionsInOrder;
  }

  public ActionInGroup getAction(String name) {
    return (ActionInGroup) actionsByName.get(name);
  }

  public void setDefaultForwardPage(int responseType,
                                    ActionForwardPageIF forwardPage) {
    defaultForwardPages.put(new Integer(responseType), forwardPage);
  }
  
  public ActionForwardPageIF getDefaultForwardPage(int responseType) {
    ActionForwardPageIF fw = (ActionForwardPageIF)
      defaultForwardPages.get(new Integer(responseType));
    if (fw == null)
      fw = (ActionForwardPageIF)
        defaultForwardPages.get(new Integer(Constants.FORWARD_GENERIC));
    return fw;
  }

  public Map getDefaultForwardPages() {
    return defaultForwardPages;
  }

   
  public void setLockedForwardPage(ActionForwardPageIF forwardPage) {
    this.lockedForwardPage = forwardPage;
  }
   
  public ActionForwardPageIF getLockedForwardPage() {
     return lockedForwardPage;
  }

  
  public void setForwardPage(ActionInGroup action,
                             int responseType,
                             ActionForwardPageIF forwardPage) {
    forwardPages.put(new ActionResponseComposite(action, responseType),
                 forwardPage);
  }

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

  public Map getForwardPages() {
    return forwardPages;
  }
  
  // --- overridden method(s) from Object implementation
  
  public String toString() {
    return "[ActionGroup: name = " + name +
      ", actions = " + actionsByName +
      ", defaultForwardPages = " + defaultForwardPages +
      ", forwardPages = " + forwardPages + "]";
  }

  public int hashCode() {
    return name.hashCode() + forwardPages.hashCode() +
      defaultForwardPages.hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof ActionGroupIF))
      return false;
    ActionGroupIF cmp = (ActionGroupIF) obj;
    return (name.equals(cmp.getName())
            && defaultForwardPages.equals(cmp.getDefaultForwardPages())
            && forwardPages.equals(cmp.getForwardPages()));
  }
  
}
