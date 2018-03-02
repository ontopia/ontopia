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
 * INTERNAL: interface for classes which
 * implement some calculation on an object
 * an come to a binary decision.
 * Used by the Deciders in the if and filter tag.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.IfTag
 * @see net.ontopia.topicmaps.nav2.taglibs.TMvalue.FilterTag
 */
public interface NavigatorDeciderIF<T> {

  /**
   * INTERNAL: if implemented criteria are matched: deliver true,
   * otherwise false.
   */
  boolean ok(NavigatorPageIF contextTag, T obj);
  
}





