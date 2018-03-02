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

package net.ontopia.topicmaps.query.spi;

/**
 * PUBLIC: Search result interfaced used by implementations of the
 * SearcherIF interface.<p>
 */
public interface SearchResultIF {
  
  /**
   * PUBLIC: Moves ahead to the next result. Returns true if
   * there were more results.
   */
  boolean next();
  
  /**
   * PUBLIC: Gets the current result value.
   */
  Object getValue();

  /**
   * PUBLIC: Gets the score for the current result value;
   */
  float getScore();

  /**
   * PUBLIC: Closes the search result. This method will be called when
   * done with the search results, so that resources can be released.
   */
  void close();
  
}





