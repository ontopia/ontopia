package ontopoly.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.OntopolyContext;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;

import org.apache.wicket.model.LoadableDetachableModel;

public class FieldAssignmentModel extends LoadableDetachableModel<FieldAssignment> {

  private String topicMapId;
  
  private String topicTypeId;
  private String declaredTopicTypeId;
  
  private int fieldType;
  private String fieldId;
  
  public FieldAssignmentModel(FieldAssignment fieldAssignment) {
    super(fieldAssignment);
    if (fieldAssignment == null)
      throw new NullPointerException("fieldAssignment parameter cannot be null.");
       
    TopicType topicType = fieldAssignment.getTopicType();
    this.topicTypeId = topicType.getId();

    TopicType declaredTopicType = fieldAssignment.getDeclaredTopicType();
    this.declaredTopicTypeId = declaredTopicType.getId();
    
    TopicMap topicMap = topicType.getTopicMap();
    this.topicMapId = topicMap.getId();
    
    FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition();    
    this.fieldType = fieldDefinition.getFieldType();
      
    this.fieldId = fieldDefinition.getId();
  }
  
  public FieldAssignment getFieldAssignment() {
    return (FieldAssignment)getObject();
  }

  @Override
  protected FieldAssignment load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);

    TopicIF topicTypeIf = tm.getTopicIFById(topicTypeId);
    TopicType topicType = new TopicType(topicTypeIf, tm);

    TopicIF declaredTopicTypeIf = tm.getTopicIFById(declaredTopicTypeId);
    TopicType declaredTopicType = new TopicType(declaredTopicTypeIf, tm);
    
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
      
    FieldDefinition fieldDefinition = FieldDefinitionModel.getFieldDefinition(fieldTopic, fieldType, tm);
    return new FieldAssignment(topicType, declaredTopicType, fieldDefinition);
  }
  
  public static List<FieldAssignmentModel> wrapInFieldAssignmentModels(List<FieldAssignment> fieldAssignments) {
    List<FieldAssignmentModel> result = new ArrayList<FieldAssignmentModel>(fieldAssignments.size());
    Iterator<FieldAssignment> iter = fieldAssignments.iterator();
    while (iter.hasNext()) {
      FieldAssignment fieldAssignment = iter.next();
      result.add(new FieldAssignmentModel(fieldAssignment));
    }
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof FieldAssignmentModel))
      return false;
    
    FieldAssignmentModel fam = (FieldAssignmentModel)other;
    return ObjectUtils.equals(getFieldAssignment(), fam.getFieldAssignment());
  }
  @Override
  public int hashCode() {
    return getFieldAssignment().hashCode();
  }

}
