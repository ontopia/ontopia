
// $Id: ActionIF.java,v 1.29 2004/01/05 21:05:08 grove Exp $

package net.ontopia.topicmaps.webed.core;

/**
 * PUBLIC: Interface for server-side actions which update the topic
 * map or otherwise act on information received from client-side
 * forms.
 */
public interface ActionIF {
  
  /**
   * PUBLIC: Performs the action using the values provided by the
   * <code>params</code> parameter.
   *
   * <p>The whole set of available parameters and attributes are
   * summarized in the <code>request</code> object, to allow the
   * action access to further relevant information.
   *
   * @exception ActionRuntimeException Thrown if a problem occurs
   *            while executing the action.
   */
  public void perform(ActionParametersIF params, ActionResponseIF response)
    throws ActionRuntimeException;
  
}
