/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.impl.utils;

/**
 * INTERNAL: An event manager listener interface.
 */

public interface EventManagerIF extends EventListenerIF {

  /**
   * INTERNAL: Register the listener as a listener for the event.
   */
  public void addListener(EventListenerIF listener, String event);

  /**
   * INTERNAL: Unregister the listener as a listener for the event.
   */
  public void removeListener(EventListenerIF listener, String event);

}





