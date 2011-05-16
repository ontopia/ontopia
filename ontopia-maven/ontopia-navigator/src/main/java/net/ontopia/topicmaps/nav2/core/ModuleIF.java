// $Id: ModuleIF.java,v 1.6 2004/11/29 19:22:58 grove Exp $

package net.ontopia.topicmaps.nav2.core;

import java.net.URL;
import java.util.Collection;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL: Implemented by an object which represents a module. That is
 * a collection of functions. The module is read in from a location
 * specified by an URL.
 *
 * @see net.ontopia.topicmaps.nav2.core.FunctionIF
 */
public interface ModuleIF {

  /**
   * Gets the URL from where this module was read in.
   */
  public URL getURL();

  /**
   * Checks if the resource has changed in the meantime by comparing
   * the lastModified fields.
   */
  public boolean hasResourceChanged();
  
  /**
   * Reads in functions contained in module from resource.
   */
  public void readIn() throws NavigatorRuntimeException;

  /**
   * Removes all existing functions.
   */
  public void clearFunctions();
  
  /**
   * Gets a collection of FunctionIF objects that are contained in
   * this module.
   *
   * @see net.ontopia.topicmaps.nav2.core.FunctionIF
   */
  public Collection getFunctions();

  /**
   * Adds a function to this module.
   */
  public void addFunction(FunctionIF func);

  /**
   * Returns a string representation of this object.
   */
  public String toString();
  
}
