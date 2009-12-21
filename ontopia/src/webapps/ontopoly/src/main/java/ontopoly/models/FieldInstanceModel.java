package ontopoly.models;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.IdentityField;
import ontopoly.model.NameField;
import ontopoly.model.OccurrenceField;
import ontopoly.model.RoleField;
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
    if (fieldInstance == null)
      throw new NullPointerException("fieldInstance parameter cannot be null.");
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
    return (FieldInstance)getObject();
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
    
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
      
    FieldDefinition fieldDefinition;
    switch (fieldType) {
    case FieldDefinition.FIELD_TYPE_ROLE:
      fieldDefinition = new RoleField(fieldTopic, tm);
      break;
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      fieldDefinition = new OccurrenceField(fieldTopic, tm);
      break;
    case FieldDefinition.FIELD_TYPE_NAME:
      fieldDefinition = new NameField(fieldTopic, tm);
      break;
    case FieldDefinition.FIELD_TYPE_IDENTITY:
      fieldDefinition = new IdentityField(fieldTopic, tm);
      break;
    default:
      throw new RuntimeException("Unknown field type: " + fieldType);
    }
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
      FieldInstance fieldInstance = (FieldInstance)iter.next();
      result.add(new FieldInstanceModel(fieldInstance));
    }
    return result;
  }
  
}
