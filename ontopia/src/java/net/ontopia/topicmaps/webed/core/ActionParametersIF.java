
// $Id: ActionParametersIF.java,v 1.12 2005/09/19 10:11:12 grove Exp $

package net.ontopia.topicmaps.webed.core;

import java.util.Collection;
import java.util.List;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * PUBLIC: Contains the parameters of an action.
 */
public interface ActionParametersIF {

  /**
   * PUBLIC: Returns the value of the numbered parameter as a single
   * object. If the parameter had multiple values only the first will
   * be returned.  
   * @param ix The index of the parameter, counting from 0.
   * @return The first object in the collection, or null if it is empty.
   */
  public Object get(int ix);

  /**
   * PUBLIC: Returns the value of the numbered parameter as a
   * collection containing all the parameter values.
   * @param ix The index of the parameter, counting from 0.
   * @return The entire collection.
   */
  public Collection getCollection(int ix);

  /**
   * PUBLIC: Returns the number of parameters.
   *
   * @since 2.0
   */
  public int getParameterCount();
  
  /**
   * PUBLIC: Returns the string value of the request parameter that
   * matched this action. If there is more than one value only the
   * first will be returned.
   */
  public String getStringValue();

  /**
   * PUBLIC: Returns the string values of the request parameter that
   * matched this action.
   */
  public String[] getStringValues();

  /**
   * PUBLIC: Interprets the string value as a topic map object ID and
   * returns that topic map object. Mainly used for selection lists
   * and suchlike.
   */
  public TMObjectIF getTMObjectValue();

  /**
   * PUBLIC: Interprets the string value as a set of topic map object
   * IDs and returns a collection of topic map objects. Mainly used
   * for selection lists and suchlike.
   */
  public Collection getTMObjectValues();

  /**
   * PUBLIC: Returns the parameter value as a file, if it was given as
   * a file. If not, returns null.
   *
   * @since 2.0
   */
  public FileValueIF getFileValue();

  /**
   * PUBLIC: Returns true if the parameter value was 'on'; used with
   * checkboxes to tell if the box was checked or not.
   *
   * @since 2.0
   */
  public boolean getBooleanValue();

  /**
   * PUBLIC: Returns the web editor request that triggered this action.
   *
   * @since 2.0
   */
  public WebEdRequestIF getRequest();

  /**
   * EXPERIMENTAL: Creates clones the ActionParametersIF object,
   * overriding the parameter list to the given list. This is useful
   * for calling existing actions from custom actions.
   *
   * @param newparams The new parameter list. Each element in the list
   * must be a java.util.Collection containing the value(s) for the
   * parameter at that position.
   *
   * @since 2.1
   */
  public ActionParametersIF cloneAndOverride(List newparams);
}
