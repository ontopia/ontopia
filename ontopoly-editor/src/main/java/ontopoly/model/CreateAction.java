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

import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents the edit mode of a field.
 */
public class CreateAction extends Topic {

  /**
   * Creates a new CreateAction object.
   */
  public CreateAction(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CreateAction)) {
      return false;
    }

    CreateAction cardinality = (CreateAction) obj;
    return (getTopicIF().equals(cardinality.getTopicIF()));
  }

  public boolean isNone() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CREATE_ACTION_NONE));
  }

  public boolean isPopup() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CREATE_ACTION_POPUP));
  }

  public boolean isNavigate() {
    return (getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CREATE_ACTION_NAVIGATE));
  }

  /**
   * Returns the default createa action (popup)
   */
  public static CreateAction getDefaultCreateAction(TopicMap tm) {
    return new CreateAction(tm.getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_CREATE_ACTION_POPUP), tm);
  }

}
