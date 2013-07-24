/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

/**
 * INTERNAL: Interface that encapsulates the support for a given
 * document format.
 */
public interface FormatModuleIF {

  /**
   * INTERNAL: Returns true if the content of the classifiable content
   * is considered to be of the supported format.
   */
  public boolean matchesContent(ClassifiableContentIF cc);

  /**
   * INTERNAL: Returns true if the identifier of the classifiable
   * content is considered to be indicating the supported format.
   */
  public boolean matchesIdentifier(ClassifiableContentIF cc);

  /**
   * INTERNAL: Reads and analyzes the classifiable content and
   * triggers callbacks on the text handler to identify the text and
   * the structure of the classifiable content.
   */
  public void readContent(ClassifiableContentIF cc, TextHandlerIF handler);
  
}
