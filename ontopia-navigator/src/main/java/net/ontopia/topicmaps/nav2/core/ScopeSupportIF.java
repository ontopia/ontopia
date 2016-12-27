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

/**
 * INTERNAL: Interface which defines basic properties (like constants)
 * needed by classes that implement scope access.
 */
public interface ScopeSupportIF {

  // --- constants
  // for specifying the scope type
  int SCOPE_BASENAMES    = 1;
  int SCOPE_VARIANTS     = 2;
  int SCOPE_OCCURRENCES  = 3;
  int SCOPE_ASSOCIATIONS = 4;

  // for specifying the decider type
  String DEC_INTERSECTION  = "intersection";
  String DEC_APPLICABLE_IN = "applicableIn";
  String DEC_WITHIN        = "within";
  String DEC_SUPERSET      = "superset";
  String DEC_SUBSET        = "subset";

}





