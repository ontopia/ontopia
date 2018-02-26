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

package net.ontopia.topicmaps.nav2.impl.basic;

import net.ontopia.utils.DeciderIF;
import net.ontopia.topicmaps.nav2.core.NavigatorDeciderIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;


/**
 * INTERNAL: A NavigatorDeciderIF implementation which wraps DeciderIF,
 * so that they can work with the navigator decider interface.
 */
public class DeciderIFWrapper implements NavigatorDeciderIF {

  protected DeciderIF decider;
  
  /**
   * INTERNAL: Default constructor.
   */
  public DeciderIFWrapper(DeciderIF decider) {
    this.decider = decider;
  }
    
  // -----------------------------------------------------------
  // Implementation of NavigatorDeciderIF
  // -----------------------------------------------------------

  @Override
  public boolean ok(NavigatorPageIF contextTag, Object obj) {
    return decider.ok(obj);
  }
  
}





