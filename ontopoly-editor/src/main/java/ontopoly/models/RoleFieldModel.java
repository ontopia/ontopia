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
package ontopoly.models;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.RoleField;
import ontopoly.model.TopicMap;
import org.apache.wicket.model.LoadableDetachableModel;

public class RoleFieldModel extends LoadableDetachableModel<RoleField> {
  
  private String topicMapId;
  private String fieldId;
  
  public RoleFieldModel(RoleField roleField) {
    super(roleField);
    Objects.requireNonNull(roleField, "roleField parameter cannot be null.");
    topicMapId = roleField.getTopicMap().getId();
    fieldId = roleField.getId();
  }

  public RoleFieldModel(String topicMapId, String fieldId) {
    Objects.requireNonNull(topicMapId, "topicMapId parameter cannot be null.");
    Objects.requireNonNull(fieldId, "fieldId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.fieldId = fieldId;
  }

  public RoleField getRoleField() {
    return (RoleField)getObject();
  }
  
  @Override
  protected RoleField load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
    return new RoleField(fieldTopic, tm);    
  }

  public static List<RoleFieldModel> wrapInRoleFieldModels(Collection<RoleField> roleFields) {
    List<RoleFieldModel> result = new ArrayList<RoleFieldModel>(roleFields.size());
    Iterator<RoleField> iter = roleFields.iterator();
    while (iter.hasNext()) {
      RoleField roleField = iter.next();
      result.add(new RoleFieldModel(roleField));
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof RoleFieldModel)
      return Objects.equals(topicMapId, ((RoleFieldModel)obj).topicMapId) &&
        Objects.equals(fieldId, ((RoleFieldModel)obj).fieldId);
    else
      return false;
  }

  @Override
  public int hashCode() {
    return topicMapId.hashCode() + fieldId.hashCode();
  }
  
}
