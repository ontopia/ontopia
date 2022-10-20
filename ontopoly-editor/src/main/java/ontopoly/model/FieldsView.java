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

import java.util.Iterator;
import java.util.Objects;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.utils.OntopolyModelUtils;

/**
 * Represents a fields view.
 */
public class FieldsView extends Topic {

  private int isEmbeddedView = -1;
  
  public FieldsView(Topic topic) {
    super(topic.getTopicIF(), topic.getTopicMap());
  }
  
  public FieldsView(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FieldsView)) {
      return false;
    }

    FieldsView other = (FieldsView) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  public boolean isEmbeddedView() {
    // NOTE: value is cached
    if (isEmbeddedView == 0) {
      return false;
    } else if (isEmbeddedView == 1) {
      return true;
    }
    
    // view is embedded is part of on:is-embedded-view(x : on:fields-view)
    TopicIF associationType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_IS_EMBEDDED_VIEW);    
    Iterator<AssociationRoleIF> iter = getTopicIF().getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role = iter.next();
      AssociationIF assoc = role.getAssociation();
      if (Objects.equals(assoc.getType(), associationType)) {
        isEmbeddedView = 1;
        return true;
      }
    }
    isEmbeddedView = 0;
    return false;
  }
  
  public static FieldsView getDefaultFieldsView(TopicMap tm) {
    return new FieldsView(OntopolyModelUtils.getTopicIF(tm, PSI.ON_DEFAULT_FIELDS_VIEW), tm);
  }

  public boolean isDefaultView() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.ON_DEFAULT_FIELDS_VIEW);
  }

}
