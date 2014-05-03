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

package net.ontopia.topicmaps.core;

public interface NameIF extends TMObjectIF {

  /**
   * PUBLIC: Gets the topic to which this name belongs.
   *
   * @return The topic named by this name; an object implementing TopicIF.
   *
   */
  public TopicIF getTopic();

  /**
   * PUBLIC: Gets the value of this name. This corresponds to
   * the content of the 'baseNameString' element in XTM 1.0, as a
   * string.
   *
   * Where this method is implemented by an object implementing VariantNameIF,
   * the contents of the 'variantName' element are returned instead.
   *
   * @return A string which is the value of this topic name.
   */
  public String getValue();

  /**
   * PUBLIC: Sets the value of this topic name. This corresponds to
   * the content of the 'baseNameString' element in XTM 1.0, as a
   * string.
   * Where this method is implemented by an object implementing VariantNameIF,
   * the contents of the 'variantName' element are returned instead.
   *
   * @param name A string which is the value of this topic name.
   */
  public void setValue(String name);

}
