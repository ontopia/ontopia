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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * INTERNAL: Decider that checks all subdeciders and returns true of one
 * of them gives a positive decision.</p>
 *
 * Note that the decision is shortcircuited when the first decider
 * gives a positive decision, the rest is then not checked.</p>
 */

@Deprecated
public class OrDecider implements DeciderIF {

  protected Set deciders = new HashSet();
  
  public OrDecider(DeciderIF decider) {
    this.deciders.add(decider);
  }

  public OrDecider(Set deciders) {
    this.deciders = deciders;
  }

  /**
   * Gets the subdeciders.
   */
  public Set getDeciders() {
    return deciders;
  }

  /**
   * Add a subdecider.
   */
  public void addDecider(DeciderIF decider) {
    deciders.add(decider);
  }

  /**
   * Remove a subdecider.
   */
  public void removeDecider(DeciderIF decider) {
    deciders.remove(decider);
  }
  
  @Override
  public boolean ok(Object object) {
    Iterator iter = deciders.iterator();
    while (iter.hasNext()) {
      DeciderIF decider = (DeciderIF)iter.next();
        if (decider.ok(object)) return true;
    }
    return false;
  }

}




