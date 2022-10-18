/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.core;

import java.util.List;
import net.ontopia.topicmaps.nav.context.UserFilterContextStore;
import net.ontopia.topicmaps.nav2.utils.HistoryMap;

/**
 * INTERNAL: interface for classes which implement user data management
 * usually stored in the session.
 */
public interface UserIF {

  /** Default User identifier */
  String COMMON_USER = "defaultUser";
  
  /** Default Model name */
  String DEFAULT_MODEL = "complete";

  /** Default View/Template */
  String DEFAULT_VIEW = "no_frames";

  /** Default Skin/CSS */
  String DEFAULT_SKIN = "ontopia";

  /**
   * INTERNAL: Gets the user identifier.
   */
  String getId();
  
  /**
   * INTERNAL: Gets the Filter Context object which stores for each
   * topicmap a set of themes the user has selected.
   */
  UserFilterContextStore getFilterContext();

  /**
   * INTERNAL: Gets the last used objects (instances of Object,
   * specialisation through the web application) which the user has
   * visited.
   *
   * @since 1.2.5
   */
  HistoryMap getHistory();

  /**
   * INTERNAL: Sets the last used objects that are in relation to the
   * user and his path through the web application.
   *
   * @since 1.2.5
   */
  void setHistory(HistoryMap hm);

  /**
   * INTERNAL: Adds a message to the user's log. The order of these
   * messages is preserved, but if too many messages are added, the
   * latest ones are lost.
   */
  void addLogMessage(String message);

  /**
   * INTERNAL: Clears the user's log.
   */
  void clearLog();
  
  /**
   * INTERNAL: Gets the current log messages from the ring buffer.
   *
   * @since 1.3.2
   */
  List getLogMessages();

  /**
   * INTERNAL: Stores a working bundle of objects under the specified
   * <code>id</code>.
   *
   * @since 1.3.2
   */
  void addWorkingBundle(String bundle_id, Object object);

  /**
   * INTERNAL: Gets an ordered lists of objects (parameter name as
   * key, list of objects as value) grouped together by the given
   * identifier <code>id</code>.
   *
   * @since 1.3.2
   */
  Object getWorkingBundle(String bundle_id);

  /**
   * INTERNAL: Removes the specified working bundle.
   *
   * @since 1.3.2
   */
  void removeWorkingBundle(String bundle_id);   
  
  
  // -----------------------------------------------------------
  // Model-View-Skin (MVS) accessor and mutator methods
  // -----------------------------------------------------------
  
  /**
   * Get Model Setting for MVS.
   */
  String getModel();

  /**
   * Set Model Setting.
   */
  void setModel(String model);
  
  /**
   * Get View (Template) Setting for MVS.
   */
  String getView();

  /**
   * Set View Setting.
   */
  void setView(String view);
  
  /**
   * Get Skin (Stylesheet) Setting for MVS.
   */
  String getSkin();
  
  /**
   * Set Skin Setting.
   */
  void setSkin(String skin);
  
}
