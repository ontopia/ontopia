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

import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.core.index.IndexIF;

/**
 * INTERNAL: An abstract dynamic index superclass.
 */
public abstract class BasicIndex extends AbstractIndex implements EventListenerIF {
  
  protected Map<String, EventListenerIF> handlers = new HashMap<String, EventListenerIF>();

  public IndexIF getIndex() {
    return this;
  }

  // -----------------------------------------------------------------------------
  // EventListenerIF
  // -----------------------------------------------------------------------------

  public void processEvent(Object object, String event, Object new_value, Object old_value) {
    if (handlers.containsKey(event)) {
      handlers.get(event).processEvent(object, event, new_value, old_value);
    }
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  public abstract class EventHandler<K, V> implements EventListenerIF<K, V> {
    public abstract void processEvent(K object, String event, V new_value, V old_value);
    protected void addEvent(Object object, String event, Object value) {
      handlers.get(event).processEvent(object, event, value, null);
    }
    protected void removeEvent(Object object, String event, Object value) {
      handlers.get(event).processEvent(object, event, null, value);
    }
  }
  
}
