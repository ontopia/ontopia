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

import java.util.Map;
import java.util.HashMap;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: This class keeps an interning table of LocatorIF objects,
 * allowing implementations to get rid of duplicate locators to save
 * memory.
 */
public class LocatorInterningTable {
  private static final Map<LocatorIF,LocatorIF> interningTable =
    new HashMap<LocatorIF,LocatorIF>();
  
  private LocatorInterningTable() {
  }

  public static synchronized LocatorIF intern(LocatorIF loc) {
    LocatorIF interned = interningTable.get(loc);
    if (interned == null) {
      interned = loc;
      interningTable.put(loc, loc);
    }
    return interned;
  }

  public synchronized void clear(){
    interningTable.clear();
  }   
}