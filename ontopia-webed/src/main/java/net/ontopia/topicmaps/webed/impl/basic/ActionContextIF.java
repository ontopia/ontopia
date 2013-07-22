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

import java.util.Map;
import java.util.Collection;
import net.ontopia.topicmaps.nav2.core.UserIF;

/**
 * INTERNAL: Stores parameters (key-value pairs: where key is the name
 * of the parameter and value is a String objects)
 */
public interface ActionContextIF {

  /**
   * INTERNAL: Gets the user object who executed the requests and in
   * which authority the consequenctly executed actions run.
   */
  public UserIF getUser();

  /**
   * INTERNAL: Gets all the parameter key value pairs.
   *
   * @return A map containing String object as keys and values.
   */
  public Map getParameters();
  
  /**
   * INTERNAL: Gets the parameter values (as a String array) belonging
   * to the given parameter name.
   */
  public String[] getParameterValues(String paramName);

  /**
   * INTERNAL: Checks that for the given parameter name exactly one
   * value is available and returns this.
   */
  public String getParameterSingleValue(String paramName);
  
  /**
   * INTERNAL: Gets all parameter names stored in this map.
   * Convenience method.
   *
   * @return A collection of String objects.   
   */
  public Collection getParameterNames();

  /**
   * INTERNAL: Returns all the ActionData objects created for this
   * request, whether triggered or not.
   */
  public Collection getAllActions();
}
