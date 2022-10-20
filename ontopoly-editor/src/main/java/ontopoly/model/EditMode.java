/*
 * #!
 * Ontopoly Editor
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

package ontopoly.model;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents the edit mode of a field.
 */
public class EditMode extends Topic {

  /**
   * Creates a new EditMode object.
   */
  public EditMode(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof EditMode)) {
      return false;
    }

    EditMode cardinality = (EditMode) obj;
    return (getTopicIF().equals(cardinality.getTopicIF()));
  }

  public LocatorIF getLocator() {
    Collection<LocatorIF> subjectIdentifiers = getTopicIF().getSubjectIdentifiers();
    if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_EXISTING_VALUES_ONLY)) {
      return PSI.ON_EDIT_MODE_EXISTING_VALUES_ONLY;
    } else if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_NEW_VALUES_ONLY)) {
      return PSI.ON_EDIT_MODE_NEW_VALUES_ONLY;
    } else if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_NO_EDIT)) {
      return PSI.ON_EDIT_MODE_NO_EDIT;
    } else if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_NORMAL)) {
      return PSI.ON_EDIT_MODE_NORMAL;
    } else if (subjectIdentifiers.contains(PSI.ON_EDIT_MODE_OWNED_VALUES)) {
      return PSI.ON_EDIT_MODE_OWNED_VALUES;
    } else {
      return null;
    }
  }

  public boolean isExistingValuesOnly() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_EDIT_MODE_EXISTING_VALUES_ONLY));
  }

  public boolean isNewValuesOnly() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_EDIT_MODE_NEW_VALUES_ONLY));
  }

  public boolean isOwnedValues() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_EDIT_MODE_OWNED_VALUES));
  }

  public boolean isNoEdit() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_EDIT_MODE_NO_EDIT));
  }

  /**
   * Returns the default edit mode (normal)
   */
  public static EditMode getDefaultEditMode(TopicMap tm) {
    return new EditMode(tm.getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_EDIT_MODE_NORMAL), tm);
  }

}
