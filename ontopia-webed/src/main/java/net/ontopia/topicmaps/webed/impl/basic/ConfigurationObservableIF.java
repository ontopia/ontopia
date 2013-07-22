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

/**
 * INTERNAL: Classes implementing this interface may want to inform
 * the subscribed observers that a specific configuration event has
 * happend (like for example a new configuration element was
 * found).</p>
 */
public interface ConfigurationObservableIF {

  /**
   * Adds an observer to the set of observers for this object.
   */
  void addObserver(ConfigurationObserverIF o);

  /**
   * Removes an observer from the set of observers of this object.
   */
  void removeObserver(ConfigurationObserverIF o);
  
}
