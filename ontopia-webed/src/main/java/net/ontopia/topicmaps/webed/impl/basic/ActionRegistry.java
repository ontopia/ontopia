
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
  
  public void addActionGroup(ActionGroupIF actionGroup) {
    actionGroups.put(actionGroup.getName(), actionGroup);
  }

  public ActionGroupIF getActionGroup(String groupName) {
    return (ActionGroupIF) actionGroups.get(groupName);
  }

  public void addImage(ImageInformationIF image) {
    images.put(image.getName(), image);
  }
  
  public ImageInformationIF getImage(String imageName) {
    if (!images.containsKey(imageName))
      throw new IllegalArgumentException("Image with name '" + imageName + "' " +
                                         "is not declared in configuration.");
    return (ImageInformationIF) images.get(imageName);
  }
  
  public void addField(FieldInformationIF field) {
    fields.put(field.getName(), field);
  }
  
  public FieldInformationIF getField(String fieldName) {
    if (!fields.containsKey(fieldName))
      throw new IllegalArgumentException("Field with name '" + fieldName + "' " +
                                         "is not declared in configuration.");
    return (FieldInformationIF) fields.get(fieldName);
  }

  public boolean hasImage(String imageName) {
    return images.containsKey(imageName);
  }
  // --- overwrite method(s) from Object implementation
  
  public String toString() {
    return "[ActionRegistry: actionGroups=" + actionGroups + "]";
  }

  
}
