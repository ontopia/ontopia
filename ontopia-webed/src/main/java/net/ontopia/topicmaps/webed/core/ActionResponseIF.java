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

package net.ontopia.topicmaps.webed.core;

import java.util.Map;

/**
 * PUBLIC: Interface for passing information from an action to the
 * forward request.
 */
public interface ActionResponseIF {

  /**
   * PUBLIC: Gets the parameter map which contains (key, value) pairs
   * of String objects representing request parameters that should be
   * included in the forward request.
   */
  public Map getParameters();

  /**
   * PUBLIC: Gets the value of the named request parameter.
   *
   * @since 2.0
   */
  public String getParameter(String key);

  /**
   * PUBLIC: Sets a parameter value in the forward request.
   */
  public void addParameter(String key, String value);

  /**
   * PUBLIC: Tells the editor framework which page to go to after form
   * processing is complete. The URL set here is relative to the root
   * of the web application, and will override URLs set by earlier
   * actions as well as the forwarding rules in the actions file.
   *
   * @since 2.0
   */
  public void setForward(String relativeUrl);

  /**
   * PUBLIC: Returns the forward URL.
   *
   * @since 2.0
   */
  public String getForward();
}




