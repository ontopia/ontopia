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

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;

import org.apache.wicket.model.LoadableDetachableModel;

public class FieldInstanceModel extends LoadableDetachableModel<FieldInstance> {

  private String topicMapId;
  private String topicId;

  private String topicTypeId;
  private String declaredTopicTypeId;
  
  private int fieldType;
  private String fieldId;

  public FieldInstanceModel(FieldInstance fieldInstance) {
    super(fieldInstance);
    Objects.requireNonNull(fieldInstance, "fieldInstance parameter cannot be null.");
    Topic topic = fieldInstance.getInstance();
    this.topicId = topic.getId();
    TopicMap topicMap = topic.getTopicMap();
    this.topicMapId = topicMap.getId();
    
    FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
    TopicType topicType = fieldAssignment.getTopicType();
    this.topicTypeId = topicType.getId();
    TopicType declaredTopicType = fieldAssignment.getDeclaredTopicType();
    this.declaredTopicTypeId = declaredTopicType.getId();
    
    FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();
    this.fieldType = fieldDefinition.getFieldType();
    this.fieldId = fieldDefinition.getId();
  }

  public int getFieldType() {
    return fieldType;
  }
  
  public FieldInstance getFieldInstance() {
    return getObject();
  }
  
  @Override
  protected FieldInstance load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF topicIf = tm.getTopicIFById(topicId);
    Topic topic = new Topic(topicIf, tm);

    TopicIF topicTypeIf = tm.getTopicIFById(topicTypeId);
    TopicType topicType = new TopicType(topicTypeIf, tm);

    TopicIF declaredTopicTypeIf = tm.getTopicIFById(declaredTopicTypeId);
    TopicType declaredTopicType = new TopicType(declaredTopicTypeIf, tm);
      
    FieldDefinition fieldDefinition = FieldDefinition.getFieldDefinition(fieldId, fieldType, tm);
    FieldAssignment fieldAssignment = new FieldAssignment(topicType, declaredTopicType, fieldDefinition);
    return newFieldInstance(topic, fieldAssignment);
  }
  
  protected FieldInstance newFieldInstance(Topic topic, FieldAssignment fieldAssignment) {
    return new FieldInstance(topic, fieldAssignment);    
  }

  public static List<FieldInstanceModel> wrapInFieldInstanceModels(List<FieldInstance> fieldInstances) {
    List<FieldInstanceModel> result = new ArrayList<FieldInstanceModel>(fieldInstances.size());
    Iterator<FieldInstance> iter = fieldInstances.iterator();
    while (iter.hasNext()) {
      FieldInstance fieldInstance = iter.next();
      result.add(new FieldInstanceModel(fieldInstance));
    }
    return result;
  }
  
}
