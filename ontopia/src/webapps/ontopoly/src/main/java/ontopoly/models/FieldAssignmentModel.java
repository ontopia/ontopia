package ontopoly.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.OntopolyContext;
import ontopoly.model.FieldAssignmentIF;
import ontopoly.model.FieldDefinitionIF;
import ontopoly.model.IdentityFieldIF;
import ontopoly.model.NameFieldIF;
import ontopoly.model.OccurrenceFieldIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.model.TopicTypeIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class FieldAssignmentModel extends LoadableDetachableModel<FieldAssignmentIF> {
  private String topicMapId; 
  private String topicTypeId;
  private String declaredTopicTypeId;
  private int fieldType;
  private String fieldId;
  
  public FieldAssignmentModel(FieldAssignmentIF fieldAssignment) {
    super(fieldAssignment);
    if (fieldAssignment == null)
      throw new NullPointerException("fieldAssignment parameter cannot be null.");
       
    TopicTypeIF topicType = fieldAssignment.getTopicType();
    this.topicTypeId = topicType.getId();

    TopicTypeIF declaredTopicType = fieldAssignment.getDeclaredTopicType();
    this.declaredTopicTypeId = declaredTopicType.getId();
    
    OntopolyTopicMapIF topicMap = topicType.getTopicMap();
    this.topicMapId = topicMap.getId();
    
    FieldDefinitionIF fieldDefinition = fieldAssignment.getFieldDefinition();
    this.fieldType = fieldDefinition.getFieldType();

    this.fieldId = fieldDefinition.getId();
  }
  
  public FieldAssignmentIF getFieldAssignment() {
    return (FieldAssignmentIF)getObject();
  }

  @Override
  protected FieldAssignmentIF load() {
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);

    TopicTypeIF topicType = tm.findTopicType(topicTypeId);
    TopicTypeIF declaredTopicType = tm.findTopicType(declaredTopicTypeId);
    FieldDefinitionIF fieldDefinition = tm.findFieldDefinition(fieldId, fieldType);
    return topicType.getFieldAssignment(declaredTopicType, fieldDefinition);
  }

  public static List<FieldAssignmentModel> wrapInFieldAssignmentModels(List<FieldAssignmentIF> fieldAssignments) {
    List<FieldAssignmentModel> result = new ArrayList<FieldAssignmentModel>(fieldAssignments.size());
    Iterator<FieldAssignmentIF> iter = fieldAssignments.iterator();
    while (iter.hasNext()) {
      FieldAssignmentIF fieldAssignment = iter.next();
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
