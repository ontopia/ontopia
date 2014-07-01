/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

package net.ontopia.topicmaps.schema.core;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * INTERNAL: Implementations of this interface can match topic map objects
 * independently of any specific topic map.
 * @deprecated The schema tools are no longer maintained in favor of a future TMCL implementation
 */ 
@Deprecated
public interface TMObjectMatcherIF {

  /**
   * INTERNAL: Returns true if this object is matched by the matcher.
   */
  public boolean matches(TMObjectIF object);

  /**
   * INTERNAL: Returns true if this object equals the given parameter.
   */
  public boolean equals(TMObjectMatcherIF object);
}





