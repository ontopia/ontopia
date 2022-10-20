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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import ontopoly.OntopolyContext;
import ontopoly.model.FieldDefinition;
import ontopoly.model.TopicMap;
import org.apache.wicket.model.LoadableDetachableModel;

public class FieldDefinitionModel extends LoadableDetachableModel<FieldDefinition> {

  private String topicMapId;
  
  private int fieldType;
  private String fieldId;
  
  public FieldDefinitionModel(FieldDefinition fieldDefinition) {
    super(fieldDefinition);
    Objects.requireNonNull(fieldDefinition, "fieldDefinition parameter cannot be null.");
    
    TopicMap topicMap = fieldDefinition.getTopicMap();
    this.topicMapId = topicMap.getId();
    this.fieldType = fieldDefinition.getFieldType();     
    this.fieldId = fieldDefinition.getId();
  }
  
  public FieldDefinition getFieldDefinition() {
    return getObject();
  }

  @Override
  protected FieldDefinition load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
      
    return FieldDefinition.getFieldDefinition(fieldId, fieldType, tm);
  }

  public static List<FieldDefinitionModel> wrapInFieldDefinitionModels(List<FieldDefinition> fieldDefinitions) {
    List<FieldDefinitionModel> result = new ArrayList<FieldDefinitionModel>(fieldDefinitions.size());
    Iterator<FieldDefinition> iter = fieldDefinitions.iterator();
    while (iter.hasNext()) {
      FieldDefinition fieldDefinition = iter.next();
      result.add(new FieldDefinitionModel(fieldDefinition));
    }
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof FieldDefinitionModel)) {
      return false;
    }
    
    FieldDefinitionModel fam = (FieldDefinitionModel)other;
    return Objects.equals(getFieldDefinition(), fam.getFieldDefinition());
  }
  @Override
  public int hashCode() {
    return getFieldDefinition().hashCode();
  }

}
