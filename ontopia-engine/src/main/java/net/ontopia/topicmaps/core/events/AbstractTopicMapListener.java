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

package net.ontopia.topicmaps.core.events;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

/**
 * PUBLIC: Base class for topic map listeners. Subclass this abstract
 * listener class if you want to create your own listener
 * implementation. Subclassing this class will make sure that any
 * extensions to the TopicMapListenerIF interface will be catered for
 * in the future, preventing inconsistencies. Methods implemented by
 * this abstract class have empty method bodies.
 *
 * @since 3.4.3
 */

public abstract class AbstractTopicMapListener implements TopicMapListenerIF {
  
  public void objectAdded(TMObjectIF snapshot) {
    // no-op
  }

  public void objectModified(TMObjectIF snapshot) {
    // no-op
  }

  public void objectRemoved(TMObjectIF snapshot) {
    // no-op 
  }

  /**
   * INTERNAL: Callback method called when listener is being
   * registered or unregistered with a topic map reference.
   */
  public void setReference(TopicMapReferenceIF ref) {
    // no-op
  }
  
}
