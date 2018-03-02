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

/**
 * INTERNAL: Interface for processing a document. Implementations would
 * typically modify, add or remove fields.<p>
 */

public interface DocumentProcessorIF {

  /**
   * INTERNAL: Can be used to figure out if it is necessary to process
   * the document.<p>
   *
   * This method should be used to quickly decide whether or not the
   * document needs to be processed. Note that this method should
   * return quickly, since it would normally be executed serially.<p>
   *
   * @return Returns true if the document should be processed.
   */
  boolean needsProcessing(DocumentIF document);

  /**
   * INTERNAL: Processes the specified document.
   */
  void process(DocumentIF document) throws Exception;
  
}





