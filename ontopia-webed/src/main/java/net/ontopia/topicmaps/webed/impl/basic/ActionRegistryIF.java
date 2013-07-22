/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.basic;

/**
 * INTERNAL: Interface for storing the application main registry
 * providing access to individual action groups. Classes implementing
 * this interface are the entry point to the action configuration.
 * Typically an object of a class implementing this interface would be
 * through the application scope.
 */
public interface ActionRegistryIF {

  /**
   * INTERNAL: Adds a principal action group to this registry.
   */
  public void addActionGroup(ActionGroupIF actionGroup);

  /**
   * INTERNAL: Gets the principal action group specified by the action
   * group name.
   */
  public ActionGroupIF getActionGroup(String groupName);

  /**
   * INTERNAL: Adds a image to the registry of known image information
   * map.
   */
  public void addImage(ImageInformationIF image);
  
  /**
   * INTERNAL: Gets the image information object belonging to the given
   * image name.
   */
  public ImageInformationIF getImage(String imageName);
  
  /**
   * INTERNAL: Checks to see if the receiver has an image information
   * object registered with the given name.
   */
  public boolean hasImage(String imageName);
  
  /**
   * INTERNAL: Adds a field to the registry of known field information
   * map.
   */
  public void addField(FieldInformationIF field);
  
  /**
   * INTERNAL: Gets the field information object belonging to the given
   * field name.
   */
  public FieldInformationIF getField(String fieldName);
  
}
