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

package net.ontopia.topicmaps.nav.conf;

/**
 * PUBLIC: An interface representing a skin.
 */
public interface SkinIF {

  /**
   * PUBLIC: Returns the display title of the skin.
   */
  public String getTitle();

  /**
   * PUBLIC: Sets the display title of the skin.
   */
  public void setTitle(String title);

  /**
   * PUBLIC: Returns the ID of the skin.
   */
  public String getId();

  /**
   * PUBLIC: Sets the ID of the skin.
   */
  public void setId(String id);
  
}





