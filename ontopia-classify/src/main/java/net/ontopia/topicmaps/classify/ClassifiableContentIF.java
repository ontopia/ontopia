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
 * INTERNAL: Interface that holds the identifier and the actual
 * content of a classifiable resource.
 */
public interface ClassifiableContentIF {

  /**
   * INTERNAL: Returns an identifier that identifies the classifiable
   * content. This could e.g. be the absolute filename or an URI of
   * the resource.
   */
  String getIdentifier();

  /**
   * INTERNAL: Returns the actual bytes in the content of the
   * classiable content.
   */
  byte[] getContent();
  
}
