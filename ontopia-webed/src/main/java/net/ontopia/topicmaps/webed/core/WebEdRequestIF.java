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

import net.ontopia.topicmaps.nav2.core.UserIF;

/**
 * PUBLIC: Represents a request to a web editor framework application.
 *
 * @since 2.0
 */
public interface WebEdRequestIF {
  
  /**
   * PUBLIC: Returns the parameters of the named action.
   */
  public ActionParametersIF getActionParameters(String name);

  /**
   * PUBLIC: Returns the user object connected with this request.
   */
  public UserIF getUser();

  /**
   * PUBLIC: Returns true if at least one action has already been run
   * in this request.
   * @since 2.1.1
   */
  public boolean getActionsExecuted();
}
