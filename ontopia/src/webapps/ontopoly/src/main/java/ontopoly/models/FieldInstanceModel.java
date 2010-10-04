package ontopoly.models;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.FieldAssignmentIF;
import ontopoly.model.FieldDefinitionIF;
import ontopoly.model.FieldInstanceIF;
import ontopoly.model.IdentityFieldIF;
import ontopoly.model.NameFieldIF;
import ontopoly.model.OccurrenceFieldIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.model.TopicTypeIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class FieldInstanceModel extends LoadableDetachableModel<FieldInstanceIF> {
  private String topicMapId;
  private String topicId;
  private String topicTypeId;
  private String declaredTopicTypeId; 
  private int fieldType;
  private String fieldId;

  public FieldInstanceModel(FieldInstanceIF fieldInstance) {
    super(fieldInstance);
    if (fieldInstance == null)
      throw new NullPointerException("fieldInstance parameter cannot be null.");
    OntopolyTopicIF topic = fieldInstance.getInstance();
    this.topicId = topic.getId();
    OntopolyTopicMapIF topicMap = topic.getTopicMap();
    this.topicMapId = topicMap.getId();
    
    FieldAssignmentIF fieldAssignment = fieldInstance.getFieldAssignment();
    TopicTypeIF topicType = fieldAssignment.getTopicType();
    this.topicTypeId = topicType.getId();
    TopicTypeIF declaredTopicType = fieldAssignment.getDeclaredTopicType();
    this.declaredTopicTypeId = declaredTopicType.getId();
    
    FieldDefinitionIF fieldDefinition = fieldAssignment.getFieldDefinition();
    this.fieldType = fieldDefinition.getFieldType();
    this.fieldId = fieldDefinition.getId();
  }

  public int getFieldType() {
    return fieldType;
  }
  
  public FieldInstanceIF getFieldInstance() {
    return (FieldInstanceIF)getObject();
  }
  
  @Override
  protected FieldInstanceIF load() {
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    OntopolyTopicIF topic = tm.findTopic(topicId);
    TopicTypeIF topicType = tm.findTopicType(topicTypeId);
    TopicTypeIF declaredTopicType = tm.findTopicType(declaredTopicTypeId);
      
    FieldDefinitionIF fieldDefinition = tm.findFieldDefinition(fieldId, fieldType);
    FieldAssignmentIF fieldAssignment = topicType.getFieldAssignment(declaredTopicType, fieldDefinition);
    return newFieldInstance(topic, fieldAssignment);
  }
  
  protected FieldInstanceIF newFieldInstance(OntopolyTopicIF topic, FieldAssignmentIF fieldAssignment) {
    return topic.getFieldInstance(fieldAssignment);
  }

  public static List<FieldInstanceModel> wrapInFieldInstanceModels(List<FieldInstanceIF> fieldInstances) {
    List<FieldInstanceModel> result = new ArrayList<FieldInstanceModel>(fieldInstances.size());
    Iterator<FieldInstanceIF> iter = fieldInstances.iterator();
    while (iter.hasNext()) {
      FieldInstanceIF fieldInstance = iter.next();
      result.add(new FieldInstanceModel(fieldInstance));
    }
    return result;
  }
  
}
