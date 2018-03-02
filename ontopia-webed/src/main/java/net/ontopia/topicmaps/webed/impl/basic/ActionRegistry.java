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

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: Default implementation of the ActionRegistryIF interface.
 * An object of this class is used in the web application to store the
 * main configuration in the application scope.
 */
public class ActionRegistry implements ActionRegistryIF {

  protected Map actionGroups;
  protected Map images;
  protected Map fields;
  public ActionRegistry() {
    actionGroups = new HashMap();
    images = new HashMap();
    fields = new HashMap();
  }
  
  @Override
  public void addActionGroup(ActionGroupIF actionGroup) {
    actionGroups.put(actionGroup.getName(), actionGroup);
  }

  @Override
  public ActionGroupIF getActionGroup(String groupName) {
    return (ActionGroupIF) actionGroups.get(groupName);
  }

  @Override
  public void addImage(ImageInformationIF image) {
    images.put(image.getName(), image);
  }
  
  @Override
  public ImageInformationIF getImage(String imageName) {
    if (!images.containsKey(imageName))
      throw new IllegalArgumentException("Image with name '" + imageName + "' " +
                                         "is not declared in configuration.");
    return (ImageInformationIF) images.get(imageName);
  }
  
  @Override
  public void addField(FieldInformationIF field) {
    fields.put(field.getName(), field);
  }
  
  @Override
  public FieldInformationIF getField(String fieldName) {
    if (!fields.containsKey(fieldName))
      throw new IllegalArgumentException("Field with name '" + fieldName + "' " +
                                         "is not declared in configuration.");
    return (FieldInformationIF) fields.get(fieldName);
  }

  @Override
  public boolean hasImage(String imageName) {
    return images.containsKey(imageName);
  }
  // --- overwrite method(s) from Object implementation
  
  @Override
  public String toString() {
    return "[ActionRegistry: actionGroups=" + actionGroups + "]";
  }

  
}
