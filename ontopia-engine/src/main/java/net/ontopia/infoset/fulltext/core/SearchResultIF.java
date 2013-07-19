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

package net.ontopia.infoset.fulltext.core;

import java.io.IOException;

/**
 * INTERNAL: A search result containing a list of ranked hits.<p>
 */

public interface SearchResultIF {

  /**
   * INTERNAL: Returns the document located at the given index.
   */
  public DocumentIF getDocument(int hit) throws IOException;

  /**
   * INTERNAL: Returns the score of the document located at the given index.
   */
  public float getScore(int hit) throws IOException;

  /**
   * INTERNAL: Returns the number of hits (documents) in the search result.
   */
  public int hits() throws IOException;
  
}
