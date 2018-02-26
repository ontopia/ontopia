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

package net.ontopia.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * INTERNAL: Decider that checks all subdeciders and returns true if all
 * of them gives a positive decision. Note that the decision is
 * shortcircuited when the first decider gives a negative decision,
 * the rest is then not checked.</p>
 */

public class AndDecider<T> implements DeciderIF<T> {

  protected Collection<DeciderIF<T>> deciders = new HashSet<DeciderIF<T>>();

  public AndDecider(Collection<DeciderIF<T>> deciders) {
    this.deciders = deciders;
  }

  /**
   * Gets the subdeciders.
   */
  public Collection<DeciderIF<T>> getDeciders() {
    return deciders;
  }

  /**
   * Add a subdecider.
   */
  public void addDecider(DeciderIF<T> decider) {
    deciders.add(decider);
  }

  /**
   * Remove a subdecider.
   */
  public void removeDecider(DeciderIF<T> decider) {
    deciders.remove(decider);
  }
  
  @Override
  public boolean ok(T object) {
    Iterator<DeciderIF<T>> iter = deciders.iterator();
    while (iter.hasNext()) {
      DeciderIF<T> decider = iter.next();
        if (!decider.ok(object)) return false;
    }
    return true;
  }

}
